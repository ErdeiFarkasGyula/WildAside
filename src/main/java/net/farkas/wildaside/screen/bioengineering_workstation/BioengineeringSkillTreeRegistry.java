package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkill;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class BioengineeringSkillTreeRegistry {
    public static final List<SkillNode> NODES = new ArrayList<>();

    private enum Lane {TOP, BOT, CROSS}

    public static final float AMPLITUDE = 48f;
    public static final float PART_WIDTH = 150f;
    public static final float K = (float) (PART_WIDTH / Math.PI);
    public static final float Y0 = 80f;
    public static final int ARC_SAMPLES = 48;

    public static void rebuild() {
        NODES.clear();

        Map<ResourceLocation, BioengineeringSkill> skills = BioengineeringSkillRegistry.all().stream()
                .collect(Collectors.toMap(BioengineeringSkill::getId, s -> s));

        Map<ResourceLocation, List<ResourceLocation>> parents = new HashMap<>();
        Map<ResourceLocation, List<ResourceLocation>> children = new HashMap<>();
        for (BioengineeringSkill skill : skills.values()) {
            List<ResourceLocation> reqs = skill.getRequirement().getRequiredSkills();
            parents.put(skill.getId(), new ArrayList<>(reqs));
            for (ResourceLocation p : reqs) {
                children.computeIfAbsent(p, k -> new ArrayList<>()).add(skill.getId());
            }
        }

        List<ResourceLocation> topo = topologicalOrder(skills.keySet(), parents);

        Map<ResourceLocation, Lane> lane = new HashMap<>();
        if (!topo.isEmpty()) lane.put(topo.get(0), Lane.CROSS);
        for (ResourceLocation n : topo) {
            List<ResourceLocation> ps = parents.getOrDefault(n, List.of());
            if (ps.isEmpty()) continue;
            Lane inherited = null;
            boolean mixed = false;
            for (ResourceLocation p : ps) {
                Lane lp = lane.get(p);
                if (lp == null || lp == Lane.CROSS) continue;
                if (inherited == null) inherited = lp;
                else if (inherited != lp) mixed = true;
            }
            if (mixed) lane.put(n, Lane.CROSS);
            else lane.put(n, inherited != null ? inherited : Lane.TOP);
        }

        Set<ResourceLocation> crossingSet = new LinkedHashSet<>();
        if (!topo.isEmpty()) crossingSet.add(topo.get(0));

        for (ResourceLocation n : topo) {
            if (parents.getOrDefault(n, List.of()).size() >= 2) crossingSet.add(n);
        }

        if (crossingSet.size() == 1 && !topo.isEmpty()) {
            crossingSet.add(topo.get(topo.size() - 1));
        }

        List<ResourceLocation> crossings = topo.stream()
                .filter(crossingSet::contains)
                .distinct()
                .toList();

        List<GenePart> parts = new ArrayList<>();
        for (int i = 0; i < crossings.size() - 1; i++) {
            ResourceLocation start = crossings.get(i);
            ResourceLocation end = crossings.get(i + 1);
            Set<ResourceLocation> interior = new LinkedHashSet<>();
            boolean collecting = false;
            for (ResourceLocation n : topo) {
                if (n.equals(start)) {
                    collecting = true;
                    continue;
                }
                if (n.equals(end)) break;
                if (collecting && !crossingSet.contains(n)) interior.add(n);
            }
            parts.add(new GenePart(start, end, new ArrayList<>(interior)));
        }

        int partIndex = 0;
        for (GenePart part : parts) {
            float tStart = partIndex * (float) Math.PI;
            float tEnd = (partIndex + 1) * (float) Math.PI;
            placePart(part, lane, tStart, tEnd, skills, topo, parents);
            partIndex++;
        }
    }

    private static void placePart(GenePart part, Map<ResourceLocation, Lane> globalLane,
                                  float tStart, float tEnd,
                                  Map<ResourceLocation, BioengineeringSkill> skills,
                                  List<ResourceLocation> topo,
                                  Map<ResourceLocation, List<ResourceLocation>> parents) {

        Map<ResourceLocation, Lane> localLane = new HashMap<>();
        localLane.put(part.start, Lane.CROSS);
        localLane.put(part.end, Lane.CROSS);

        boolean toggle = true;
        for (ResourceLocation n : topo) {
            if (!part.interior.contains(n)) continue;
            Lane ln = null;
            for (ResourceLocation p : parents.getOrDefault(n, List.of())) {
                if (part.start.equals(p) || part.interior.contains(p)) {
                    Lane lp = localLane.get(p);
                    if (lp == Lane.TOP || lp == Lane.BOT) {
                        ln = lp;
                        break;
                    }
                }
            }
            if (ln == null) {
                ln = toggle ? Lane.TOP : Lane.BOT;
                toggle = !toggle;
            }
            localLane.put(n, ln);
        }

        float xStart = kx(tStart), yCross = Y0;
        float xEnd = kx(tEnd);

        List<ResourceLocation> topList = new ArrayList<>();
        List<ResourceLocation> botList = new ArrayList<>();
        for (ResourceLocation n : part.interior) {
            Lane ln = localLane.getOrDefault(n, Lane.TOP);
            if (ln == Lane.BOT) botList.add(n);
            else topList.add(n);
        }

        List<Sample> topArc = sampleArc(tStart, tEnd, false);
        List<Sample> botArc = sampleArc(tStart, tEnd, true);

        addNode(skills.get(part.start), xStart, yCross);
        addNode(skills.get(part.end), xEnd, yCross);

        float topLen = topArc.get(topArc.size() - 1).len();
        for (int i = 0; i < topList.size(); i++) {
            float s = topLen * (i + 1) / (topList.size() + 1);
            float t = lerpArc(topArc, s);
            addNode(skills.get(topList.get(i)), kx(t), yTop(t));
        }

        float botLen = botArc.get(botArc.size() - 1).len();
        for (int i = 0; i < botList.size(); i++) {
            float s = botLen * (i + 1) / (botList.size() + 1);
            float t = lerpArc(botArc, s);
            addNode(skills.get(botList.get(i)), kx(t), yBot(t));
        }
    }

    private static void addNode(BioengineeringSkill skill, float x, float y) {
        if (skill == null) return;
        NODES.add(new SkillNode(skill, Math.round(x), Math.round(y)));
    }

    public static float kx(float t) {
        return K * t;
    }

    public static float yTop(float t) {
        return Y0 + AMPLITUDE * (float) Math.sin(t);
    }

    public static float yBot(float t) {
        return Y0 - AMPLITUDE * (float) Math.sin(t);
    }

    private record Sample(float t, float len) {
    }

    private static List<Sample> sampleArc(float t0, float t1, boolean bottom) {
        List<Sample> acc = new ArrayList<>();
        float prevX = kx(t0);
        float prevY = bottom ? yBot(t0) : yTop(t0);
        float total = 0f;
        acc.add(new Sample(t0, 0f));
        for (int i = 1; i <= ARC_SAMPLES; i++) {
            float t = t0 + (t1 - t0) * (i / (float) ARC_SAMPLES);
            float x = kx(t);
            float y = bottom ? yBot(t) : yTop(t);
            float dx = x - prevX;
            float dy = y - prevY;
            total += (float) Math.hypot(dx, dy);
            acc.add(new Sample(t, total));
            prevX = x;
            prevY = y;
        }
        return acc;
    }

    private static float lerpArc(List<Sample> arc, float target) {
        for (int i = 0; i < arc.size() - 1; i++) {
            float aLen = arc.get(i).len();
            float bLen = arc.get(i + 1).len();
            if (target >= aLen && target <= bLen) {
                float t = (target - aLen) / (bLen - aLen + 1e-6f);
                float ta = arc.get(i).t();
                float tb = arc.get(i + 1).t();
                return ta + t * (tb - ta);
            }
        }
        return arc.get(arc.size() - 1).t();
    }

    private static List<ResourceLocation> topologicalOrder(Set<ResourceLocation> nodes, Map<ResourceLocation, List<ResourceLocation>> parents) {
        Map<ResourceLocation, Integer> indeg = new HashMap<>();
        for (ResourceLocation n : nodes) indeg.put(n, 0);

        for (var e : parents.entrySet()) {
            for (ResourceLocation p : e.getValue()) {
                indeg.merge(e.getKey(), 1, Integer::sum);
            }
        }

        Deque<ResourceLocation> dq = new ArrayDeque<>();
        indeg.forEach((n, d) -> {
            if (d == 0) dq.add(n);
        });

        List<ResourceLocation> out = new ArrayList<>();
        Map<ResourceLocation, List<ResourceLocation>> children = new HashMap<>();

        for (var e : parents.entrySet()) {
            for (ResourceLocation p : e.getValue()) {
                children.computeIfAbsent(p, k -> new ArrayList<>()).add(e.getKey());
            }
        }

        while (!dq.isEmpty()) {
            ResourceLocation n = dq.poll();
            out.add(n);
            for (ResourceLocation c : children.getOrDefault(n, List.of())) {
                indeg.merge(c, -1, Integer::sum);
                if (indeg.get(c) == 0) dq.add(c);
            }
        }
        return out;
    }

    private record GenePart(ResourceLocation start, ResourceLocation end, List<ResourceLocation> interior) {
    }
}