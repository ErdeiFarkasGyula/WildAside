package net.farkas.wildaside.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.bioengineering_skill.BioengineeringSkillsCapability;
import net.farkas.wildaside.capability.bioengineering_skill.IBioengineeringSkills;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.IDna;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.appearance.AppearanceGeneRegistry;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusExpression;
import net.farkas.wildaside.dna.locus.LocusSource;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.dna.trait.TraitTransition;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.network.WindSavedData;
import net.farkas.wildaside.util.ContaminationHandler;
import net.farkas.wildaside.network.WindData;
import net.farkas.wildaside.util.WindManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("wildaside")
                .requires(cs -> cs.hasPermission(2));

        root.then(
                Commands.literal("contamination")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("action", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                new String[]{"add", "get", "set", "clear"}, builder
                                        ))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> executeContamination(ctx, true))
                                        )
                                        .executes(ctx -> executeContamination(ctx, false))
                                )
                        )
        );

        root.then(
                Commands.literal("wind")
                        .then(Commands.literal("set")
                                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("strength", FloatArgumentType.floatArg(0))
                                                                .executes(ctx -> {
                                                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                                                    double z = DoubleArgumentType.getDouble(ctx, "z");
                                                                    float s = FloatArgumentType.getFloat(ctx, "strength");

                                                                    Vec3 vec = new Vec3(x, y, z);
                                                                    WindManager.setWind(vec, s);

                                                                    ServerLevel serverLevel = ctx.getSource().getLevel();
                                                                    WindSavedData windSavedData = WindSavedData.get(serverLevel);
                                                                    windSavedData.setWeather(serverLevel.isRaining(), serverLevel.isThundering());
                                                                    windSavedData.setWind(vec, s);

                                                                    ctx.getSource().sendSuccess(
                                                                            () -> Component.translatable("command.wildaside.wind.set", x, y, z, s), true
                                                                    );
                                                                    return Command.SINGLE_SUCCESS;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("random")
                                .executes(ctx -> {
                                    ServerLevel serverLevel = ctx.getSource().getLevel();
                                    WindData windData = WindManager.calculateAndSetWind(serverLevel, true);
                                    Vec3 dir = windData.direction();

                                    ctx.getSource().sendSuccess(
                                            () -> Component.translatable("command.wildaside.wind.random", dir.x, dir.y, dir.z, windData.strength()), true
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .then(Commands.literal("get")
                                .executes(ctx -> {
                                    float strength = WindManager.getStrength();
                                    Vec3 dir = WindManager.getDirection();
                                    ctx.getSource().sendSuccess(
                                            () -> Component.translatable("command.wildaside.wind.get", dir.x, dir.y, dir.z, strength), false
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );

        root.then(
                Commands.literal("update_notification")
                        .requires(cs -> cs.hasPermission(0))
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean value = BoolArgumentType.getBool(context, "enabled");
                                    ModConfig.setShowUpdates(value);
                                    context.getSource().sendSuccess(() ->
                                            Component.translatable("command.wildaside.update_notification",
                                                    Component.translatable("mod.wildaside"),
                                                    Component.translatable(value ? "general.wildaside.enabled" : "general.wildaside.disabled")), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );

        root.then(buildDnaCommand());

        root.then(buildBioSkillCommand());

        dispatcher.register(root);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildDnaCommand() {
        return Commands.literal("dna")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument(TARGET, EntityArgument.entity())
                        .then(Commands.literal("print")
                                .executes(ModCommands::dnaPrint)
                        )

                        .then(Commands.literal("stress")
                                .then(Commands.literal("get")
                                        .executes(ModCommands::dnaStressGet)
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0, 100))
                                                .executes(ModCommands::dnaStressSet)
                                        )
                                )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(-100, 100))
                                                .executes(ModCommands::dnaStressAdd)
                                        )
                                )
                        )

                        .then(Commands.literal("trait")
                                .then(Commands.argument(TRAIT, StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (Trait trait : TraitRegistry.TRAITS) {
                                                builder.suggest(trait.getName());
                                            }
                                            return builder.buildFuture();
                                        })

                                        .then(Commands.literal("get")
                                                .executes(ModCommands::dnaTraitGet)
                                        )

                                        .then(Commands.literal("set")
                                                .then(Commands.argument(VALUE, StringArgumentType.greedyString())
                                                        .suggests((ctx, builder) -> {
                                                            Entity target;
                                                            try {
                                                                target = EntityArgument.getEntity(ctx, TARGET);
                                                            }
                                                            catch (CommandSyntaxException e) {
                                                                return builder.buildFuture();
                                                            }

                                                            if (!(target instanceof LivingEntity living)) {
                                                                return builder.buildFuture();
                                                            }

                                                            Trait trait = TraitRegistry.getByName(
                                                                    StringArgumentType.getString(ctx, TRAIT)
                                                            );

                                                            AppearanceGeneRegistry
                                                                    .getSuggestions(living, trait)
                                                                    .forEach(builder::suggest);

                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ModCommands::dnaTraitSet)
                                                )
                                        )

                                        .executes(ModCommands::dnaTraitGet)
                                )
                        )

                        .then(Commands.literal("regenerate")
                                .executes(ModCommands::dnaRegenerate)
                        )

                        .then(Commands.literal("clear")
                                .executes(ModCommands::dnaClear)
                        )
                );
    }

    private static int dnaPrint(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        CommandSourceStack src = ctx.getSource();

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Map<Trait, List<GeneLocus>> lociToPrint;
            boolean isGenerated = false;

            if (dna.getGenomeLociView().isEmpty()) {
                lociToPrint = DnaUtils.generateBaseLoci(living);
                isGenerated = true;

                src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.generated_preview")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC), false);
            }
            else {
                lociToPrint = dna.getGenomeLociView();
            }

            src.sendSuccess(() -> Component.literal("").withStyle(ChatFormatting.STRIKETHROUGH)
                    .append("                                                  "), false);
            src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.header", living.getName())
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

            EntityType<?> sourceType = dna.getSource() != null ? dna.getSource() : living.getType();
            ResourceLocation sourceId = ForgeRegistries.ENTITY_TYPES.getKey(sourceType);
            boolean finalIsGenerated = isGenerated;
            src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.source")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(sourceId != null ? sourceId.toString() : "unknown")
                            .withStyle(ChatFormatting.WHITE))
                    .append(finalIsGenerated ? Component.literal(" (preview)").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC) : Component.empty()), false);

            if (!isGenerated) {
                float stress = dna.getStress();
                ChatFormatting stressColor = stress < 30 ? ChatFormatting.GREEN :
                        stress < 60 ? ChatFormatting.YELLOW :
                                stress < 85 ? ChatFormatting.RED : ChatFormatting.DARK_RED;
                src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.stress")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%.1f%%", stress))
                                .withStyle(stressColor)), false);

                int pendingCount = dna.getPendingIntegrations().size();
                if (pendingCount > 0) {
                    src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.pending", pendingCount)
                            .withStyle(ChatFormatting.YELLOW), false);
                }

                int transitionCount = dna.getActiveTransitions().size();
                if (transitionCount > 0) {
                    src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.transitions", transitionCount)
                            .withStyle(ChatFormatting.AQUA), false);
                }
            }

            int totalLoci = lociToPrint.values().stream().mapToInt(List::size).sum();
            int traitCount = lociToPrint.size();
            src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.loci_count", traitCount, totalLoci)
                    .withStyle(ChatFormatting.GRAY), false);

            src.sendSuccess(Component::empty, false);

            Map<TraitType, List<Map.Entry<Trait, List<GeneLocus>>>> byType = new LinkedHashMap<>();
            for (TraitType type : TraitType.values()) {
                byType.put(type, new ArrayList<>());
            }

            for (Map.Entry<Trait, List<GeneLocus>> entry : lociToPrint.entrySet()) {
                byType.get(entry.getKey().getTraitType()).add(entry);
            }

            for (Map.Entry<TraitType, List<Map.Entry<Trait, List<GeneLocus>>>> typeEntry : byType.entrySet()) {
                if (typeEntry.getValue().isEmpty()) continue;

                TraitType type = typeEntry.getKey();
                src.sendSuccess(() -> Component.literal("【 ")
                        .append(Component.literal(type.name()).withStyle(type.getHeaderColour(), ChatFormatting.BOLD))
                        .append(" 】"), false);

                typeEntry.getValue().sort(Comparator.comparing(e -> e.getKey().getName()));

                for (Map.Entry<Trait, List<GeneLocus>> traitEntry : typeEntry.getValue()) {
                    Trait trait = traitEntry.getKey();
                    List<GeneLocus> loci = traitEntry.getValue();

                    AlleleValue expressed = LocusExpression.express(trait, loci);
                    String expressedStr = expressed.format().getString();

                    int nativeCount = 0, integratedCount = 0, transientCount = 0, rejectedCount = 0;
                    for (GeneLocus locus : loci) {
                        switch (locus.getSource()) {
                            case NATIVE -> nativeCount++;
                            case INTEGRATED -> integratedCount++;
                            case TRANSIENT -> transientCount++;
                            case REJECTED -> rejectedCount++;
                        }
                    }

                    int finalNativeCount = nativeCount;
                    int finalIntegratedCount = integratedCount;
                    int finalTransientCount = transientCount;
                    int finalRejectedCount = rejectedCount;

                    src.sendSuccess(() -> {
                        MutableComponent line = Component.literal("  ")
                                .append(Component.translatable("trait.wildaside." + trait.getName())
                                        .withStyle(type.getEntryColour()))
                                .append(Component.literal(":  ").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(expressedStr).withStyle(ChatFormatting.WHITE));

                        if (loci.size() > 1 || finalIntegratedCount > 0 || finalTransientCount > 0 || finalRejectedCount > 0) {
                            line.append(Component.literal(" (").withStyle(ChatFormatting.DARK_GRAY));

                            List<Component> parts = new ArrayList<>();
                            if (finalNativeCount > 0) {
                                parts.add(Component.literal(finalNativeCount + "N").withStyle(ChatFormatting.GREEN));
                            }
                            if (finalIntegratedCount > 0) {
                                parts.add(Component.literal(finalIntegratedCount + "I").withStyle(ChatFormatting.AQUA));
                            }
                            if (finalTransientCount > 0) {
                                parts.add(Component.literal(finalTransientCount + "T").withStyle(ChatFormatting.YELLOW));
                            }
                            if (finalRejectedCount > 0) {
                                parts.add(Component.literal(finalRejectedCount + "R").withStyle(ChatFormatting.RED));
                            }

                            for (int i = 0; i < parts.size(); i++) {
                                if (i > 0) line.append(Component.literal("/").withStyle(ChatFormatting.DARK_GRAY));
                                line.append(parts.get(i));
                            }

                            line.append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY));
                        }

                        return line;
                    }, false);

                    for (GeneLocus locus : loci) {
                        src.sendSuccess(() -> {
                            float stability = locus.getStability();
                            float degradation = locus.getDegradation();

                            return Component.literal("    └ ")
                                    .withStyle(ChatFormatting.DARK_GRAY)
                                    .append(Component.literal(locus.getId()).withStyle(ChatFormatting.GRAY))
                                    .append(Component.literal(" [").withStyle(ChatFormatting.DARK_GRAY))
                                    .append(Component.literal(locus.getSource().name()).withStyle(locus.getSource().getColor()))
                                    .append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY))
                                    .append(Component.literal(" stab=").withStyle(ChatFormatting.DARK_GRAY))
                                    .append(Component.literal(String.format("%.2f", stability))
                                            .withStyle(stability > 0.7f ? ChatFormatting.GREEN :
                                                    stability > 0.4f ? ChatFormatting.YELLOW : ChatFormatting.RED))
                                    .append(degradation > 0.01f ?
                                            Component.literal(" deg=").withStyle(ChatFormatting.DARK_GRAY)
                                                    .append(Component.literal(String.format("%.0f%%", degradation * 100))
                                                            .withStyle(ChatFormatting.RED))
                                            : Component.empty());
                        }, false);
                    }
                }

                src.sendSuccess(Component::empty, false);
            }

            src.sendSuccess(() -> Component.literal("").withStyle(ChatFormatting.STRIKETHROUGH)
                    .append("                                                  "), false);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int dnaStressGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float stress = dna.getStress();
            ChatFormatting color = stress < 30 ? ChatFormatting.GREEN :
                    stress < 60 ? ChatFormatting.YELLOW :
                            stress < 85 ? ChatFormatting.RED : ChatFormatting.DARK_RED;

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("command.wildaside.dna.stress.get", living.getName())
                            .append(Component.literal(String.format(" %.1f%%", stress)).withStyle(color)), false);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int dnaStressSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        float amount = FloatArgumentType.getFloat(ctx, "amount");

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            dna.setStress(amount);
            ctx.getSource().sendSuccess(() ->
                    Component.translatable("command.wildaside.dna.stress.set", living.getName(), String.format("%.1f", amount)), true);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int dnaStressAdd(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        float amount = FloatArgumentType.getFloat(ctx, "amount");

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float oldStress = dna.getStress();
            dna.setStress(oldStress + amount);
            float newStress = dna.getStress();

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("command.wildaside.dna.stress.add",
                            String.format("%.1f", amount),
                            living.getName(),
                            String.format("%.1f → %.1f", oldStress, newStress)), true);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int dnaTraitGet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        String traitName = StringArgumentType.getString(ctx, TRAIT);
        Trait trait = TraitRegistry.getByName(traitName);

        if (trait == null) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.unknown_trait", traitName));
            return 0;
        }

        CommandSourceStack src = ctx.getSource();

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Map<Trait, List<GeneLocus>> lociToPrint;
            boolean isGenerated = false;

            if (dna.getGenomeLociView().isEmpty()) {
                lociToPrint = DnaUtils.generateBaseLoci(living);
                isGenerated = true;

                src.sendSuccess(() -> Component.translatable("command.wildaside.dna.print.generated_preview")
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC), false);
            }
            else {
                lociToPrint = dna.getGenomeLociView();
            }

            List<GeneLocus> loci = lociToPrint.get(trait);
            if (loci == null || loci.isEmpty()) {
                src.sendFailure(Component.translatable("command.wildaside.dna.no_gene_for_trait", traitName));
                return;
            }

            src.sendSuccess(() -> Component.translatable("command.wildaside.dna.trait.header",
                            TraitRegistry.translatableTrait(trait), living.getName())
                    .withStyle(ChatFormatting.GOLD), false);

            AlleleValue expressed = LocusExpression.express(trait, loci);
            src.sendSuccess(() -> Component.translatable("command.wildaside.dna.trait.expressed")
                    .withStyle(ChatFormatting.GRAY)
                    .append(expressed.format().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)), false);

            if (!isGenerated) {
                float currentApplied = dna.getCurrentAppliedValue(trait);
                boolean hasTransition = dna.hasActiveTransition(trait);

                if (hasTransition) {
                    TraitTransition transition = dna.getActiveTransitions().get(trait);
                    src.sendSuccess(() -> Component.translatable("command.wildaside.dna.trait.transitioning")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(Component.literal(String.format(" %.2f -> %.2f (%.0f%%)",
                                            transition.getCurrentValue(),
                                            transition.getTargetValue(),
                                            transition.getProgress(living.level().getGameTime()) * 100))
                                    .withStyle(ChatFormatting.WHITE)), false);
                }
                else if (currentApplied > 0) {
                    src.sendSuccess(() -> Component.translatable("command.wildaside.dna.trait.current_applied")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(String.format(" %.4f", currentApplied))
                                    .withStyle(ChatFormatting.WHITE)), false);
                }
            }

            src.sendSuccess(Component::empty, false);

            for (int i = 0; i < loci.size(); i++) {
                GeneLocus locus = loci.get(i);
                int index = i;

                src.sendSuccess(() -> Component.literal("Locus " + (index + 1) + ": ")
                        .withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(locus.getId()).withStyle(ChatFormatting.WHITE)), false);

                src.sendSuccess(() -> Component.literal("  Source: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(locus.getSource().name()).withStyle(locus.getSource().getColor())), false);

                src.sendSuccess(() -> Component.literal("  Stability: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(String.format("%.2f", locus.getStability()))
                                .withStyle(locus.getStability() > 0.7f ? ChatFormatting.GREEN :
                                        locus.getStability() > 0.4f ? ChatFormatting.YELLOW : ChatFormatting.RED)), false);

                if (locus.getDegradation() > 0.01f) {
                    src.sendSuccess(() -> Component.literal("  Degradation: ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(String.format("%.1f%%", locus.getDegradation() * 100))
                                    .withStyle(ChatFormatting.RED)), false);
                }

                if (!locus.getFlags().isEmpty()) {
                    src.sendSuccess(() -> Component.literal("  Flags: ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(locus.getFlags().toString())
                                    .withStyle(ChatFormatting.YELLOW)), false);
                }

                Allele a = locus.getAlleleA();
                Allele b = locus.getAlleleB();

                src.sendSuccess(() -> Component.literal("  Allele A: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(a.getValueHolder().format())
                        .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(a.getDominance().getComponent())
                        .append(Component.literal(" | mut=").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(String.format("%.3f", a.getMutationRate()))), false);

                src.sendSuccess(() -> Component.literal("  Allele B: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(b.getValueHolder().format())
                        .append(Component.literal(" | ").withStyle(ChatFormatting.DARK_GRAY))
                        .append(b.getDominance().getComponent())
                        .append(Component.literal(" | mut=").withStyle(ChatFormatting.DARK_GRAY))
                        .append(Component.literal(String.format("%.3f", b.getMutationRate()))), false);

                src.sendSuccess(Component::empty, false);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int dnaTraitSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        String traitName = StringArgumentType.getString(ctx, TRAIT);
        Trait trait = TraitRegistry.getByName(traitName);

        if (trait == null) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.unknown_trait", traitName));
            return 0;
        }

        String rawInput = StringArgumentType.getString(ctx, VALUE);

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            if (dna.getGenomeLociView().isEmpty()) {
                dna.setSource(living.getType());
                dna.setGenomeFromLoci(DnaUtils.generateBaseLoci(living));
            }

            List<GeneLocus> loci = dna.getGenomeLociView().get(trait);
            if (loci == null || loci.isEmpty()) {
                ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.no_gene_for_trait", traitName));
                return;
            }

            Gene old = DnaUtils.asGene(trait, loci);
            if (old == null) return;

            try {
                AlleleValue newValue = TraitRegistry.parseValue(old.getExpressedValueHolder(), rawInput);

                List<GeneLocus> updatedLoci = new ArrayList<>();
                for (GeneLocus locus : loci) {
                    Allele a = locus.getAlleleA().copyWithValue(newValue);
                    Allele b = locus.getAlleleB().copyWithValue(newValue);

                    GeneLocus updated = new GeneLocus(
                            locus.getId(), a, b,
                            locus.getFlags(),
                            locus.getStability(),
                            locus.getSource(),
                            locus.getIntegrationTick(),
                            locus.getDegradation()
                    );
                    updatedLoci.add(updated);
                }

                Map<Trait, List<GeneLocus>> allLoci = new HashMap<>(dna.getGenomeLociView());
                allLoci.put(trait, updatedLoci);
                dna.setGenomeFromLoci(allLoci);

                applySingleTrait(living, dna, trait);

                ctx.getSource().sendSuccess(() ->
                        Component.translatable("command.wildaside.dna.trait.set",
                                TraitRegistry.translatableTrait(trait),
                                living.getName(),
                                newValue.format()), true);

            }
            catch (IllegalArgumentException e) {
                ctx.getSource().sendFailure(Component.literal("Invalid value:  " + e.getMessage()));
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    private static void applySingleTrait(LivingEntity entity, IDna dna, Trait trait) {
        List<GeneLocus> loci = dna.getGenomeLociView().get(trait);
        if (loci == null || loci.isEmpty()) return;

        AlleleValue expressed = LocusExpression.express(trait, loci);

        if (expressed instanceof FloatAlleleValue floatValue) {
            float target = floatValue.get();
            float current = dna.getCurrentAppliedValue(trait);

            if (current <= 0) {
                current = trait.getCurrentValue(entity);
            }

            if (Math.abs(target - current) < 0.001f) {
                trait.apply(entity, expressed);
                dna.setCurrentAppliedValue(trait, target);
            }
            else {
                long currentTick = entity.level().getGameTime();
                int duration = getTransitionDurationForTrait(trait);
                TraitTransition transition = new TraitTransition(trait, current, target, currentTick, duration);
                dna.addTransition(transition);

                WildAside.LOGGER.info("Started transition for [{}]:  {} -> {} over {}t",
                        trait.getName(), current, target, duration);
            }
        }
        else {
            trait.apply(entity, expressed);
        }
    }

    private static int getTransitionDurationForTrait(Trait trait) {
        TraitType type = trait.getTraitType();
        return switch (type) {
            case CORE -> TraitTransition.DEFAULT_DURATION;
            case RESISTANCE -> TraitTransition.FAST_DURATION;
            case ABILITY -> TraitTransition.SLOW_DURATION;
            case APPEARANCE -> TraitTransition.FAST_DURATION;
        };
    }

    private static int dnaRegenerate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            dna.removeGenes(living);
            dna.clearTransitions();
            dna.clearPendingIntegrations();
            dna.getCurrentAppliedValues().clear();

            dna.setSource(living.getType());
            dna.setGenomeFromLoci(DnaUtils.generateBaseLoci(living));
            dna.setStress(0f);

            dna.recomputeAndApply(living);

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("command.wildaside.dna.regenerate", living.getName())
                            .withStyle(ChatFormatting.GREEN), true);

            WildAside.LOGGER.info("Regenerated DNA for {} via command", living.getName().getString());
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int dnaClear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, TARGET);
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.dna.not_living"));
            return 0;
        }

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            dna.removeGenes(living);
            dna.clearTransitions();
            dna.clearPendingIntegrations();
            dna.clearInvadingLoci();
            dna.getCurrentAppliedValues().clear();

            dna.setGenomeFromLoci(new HashMap<>());
            dna.setSource(null);
            dna.setStress(0f);

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("command.wildaside.dna.clear", living.getName())
                            .withStyle(ChatFormatting.YELLOW), true);

            WildAside.LOGGER.info("Cleared DNA for {} via command", living.getName().getString());
        });

        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildBioSkillCommand() {
        return Commands.literal("bio_skill")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("players", EntityArgument.players())

                        .then(Commands.literal("skill")
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument("skill", ResourceLocationArgument.id())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                        BioengineeringSkillRegistry.all().stream()
                                                                .map(s -> s.getId().toString())
                                                                .toList(),
                                                        builder
                                                ))
                                                .executes(ModCommands::skillUnlock)
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("skill", ResourceLocationArgument.id())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                        BioengineeringSkillRegistry.all().stream()
                                                                .map(s -> s.getId().toString())
                                                                .toList(),
                                                        builder
                                                ))
                                                .executes(ModCommands::skillRemove)
                                        )
                                )
                                .then(Commands.literal("has")
                                        .then(Commands.argument("skill", ResourceLocationArgument.id())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                        BioengineeringSkillRegistry.all().stream()
                                                                .map(s -> s.getId().toString())
                                                                .toList(),
                                                        builder
                                                ))
                                                .executes(ModCommands::skillHas)
                                        )
                                )
                                .then(Commands.literal("list")
                                        .executes(ModCommands::skillList)
                                )
                        )

                        .then(Commands.literal("point")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> pointOperation(ctx, BioengineeringSkillPointOperation.ADD))
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> pointOperation(ctx, BioengineeringSkillPointOperation.REMOVE))
                                        )
                                )
                                .then(Commands.literal("spend")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> pointOperation(ctx, BioengineeringSkillPointOperation.SPEND))
                                        )
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(ctx -> pointOperation(ctx, BioengineeringSkillPointOperation.SET))
                                        )
                                )
                                .then(Commands.literal("get")
                                        .executes(ModCommands::pointGet)
                                )
                        )
                );
    }

    private static int executeContamination(CommandContext<CommandSourceStack> ctx, boolean hasAmount) throws CommandSyntaxException {
        String action = StringArgumentType.getString(ctx, "action");
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        int amount = hasAmount ? IntegerArgumentType.getInteger(ctx, "amount") : 0;

        int affected = 0;
        CommandSourceStack source = ctx.getSource();

        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity living)) continue;

            switch (action.toLowerCase()) {
                case "add" -> {
                    ContaminationHandler.addDose(living, amount);
                    source.sendSuccess(() -> Component.translatable("command.wildaside.contamination.add_contamination",
                            amount, Component.translatable("effect.wildaside.contamination"), living.getName()), false);
                    affected++;
                }
                case "get" -> {
                    int current = ContaminationHandler.getDose(living);
                    source.sendSuccess(() -> Component.translatable("command.wildaside.contamination.get_contamination",
                            living.getName(), current, Component.translatable("effect.wildaside.contamination")), false);
                    affected++;
                }
                case "set" -> {
                    ContaminationHandler.setDose(living, amount);
                    source.sendSuccess(() -> Component.translatable("command.wildaside.contamination.set_contamination",
                            Component.translatable("effect.wildaside.contamination"), living.getName(), amount), false);
                    affected++;
                }
                case "clear" -> {
                    ContaminationHandler.setDose(living, 0);
                    source.sendSuccess(() -> Component.translatable("command.wildaside.contamination.clear_contamination",
                            Component.translatable("effect.wildaside.contamination"), living.getName()), false);
                    affected++;
                }
                default -> {
                    source.sendFailure(Component.translatable("command.wildaside.contamination.invalid_action", action));
                    return 0;
                }
            }
        }

        if (affected > 0) {
            String entityString = affected > 1 ? "command.wildaside.contamination. entities" : "command.wildaside.contamination.entity";
            String actionString = "command.wildaside.contamination. action." + action;
            applyContamination(ctx, actionString, affected, entityString);
        }
        else {
            source.sendFailure(Component.translatable("command.wildaside.contamination.no_valid_entities"));
        }
        return affected;
    }

    public static void applyContamination(CommandContext<CommandSourceStack> context, String action, int finalAffected, String entityString) {
        context.getSource().sendSuccess(() ->
                Component.translatable("command.wildaside.contamination.apply_contamination",
                        Component.translatable(action).getString().toLowerCase(), finalAffected,
                        Component.translatable(entityString).getString().toLowerCase()), true);
    }

    private static int skillUnlock(CommandContext<CommandSourceStack> ctx) {
        Collection<ServerPlayer> players;
        try {
            players = EntityArgument.getPlayers(ctx, "players");
        }
        catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("No players found"));
            return 0;
        }

        ResourceLocation skillId = ResourceLocationArgument.getId(ctx, "skill");
        CommandSourceStack src = ctx.getSource();

        int count = (int) players.stream().filter(target -> unlockSkill(src, target, skillId) == 1).count();

        src.sendSuccess(() -> Component.translatable("command.wildaside.skill.summary.unlock", skillId.toString(), count), true);

        return count;
    }

    private static int unlockSkill(CommandSourceStack source, ServerPlayer player, ResourceLocation skillId) {
        IBioengineeringSkills skills = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);
        if (skills == null) return 0;

        if (skills.hasSkill(skillId)) {
            source.sendFailure(Component.translatable("command.wildaside.skill.already_unlocked", player.getDisplayName(), skillId));
            return 0;
        }

        BioengineeringSkillUtils.unlockAndSyncToClient(player, skillId, true);
        source.sendSuccess(() -> Component.translatable("command.wildaside.skill.unlocked", player.getDisplayName(), skillId), true);

        return 1;
    }

    private static int skillRemove(CommandContext<CommandSourceStack> ctx) {
        Collection<ServerPlayer> players;
        try {
            players = EntityArgument.getPlayers(ctx, "players");
        }
        catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.no_players_found"));
            return 0;
        }

        ResourceLocation skillId = ResourceLocationArgument.getId(ctx, "skill");
        CommandSourceStack src = ctx.getSource();

        int removed = 0;

        for (ServerPlayer player : players) {
            IBioengineeringSkills skills = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);
            if (skills == null) continue;

            if (!skills.hasSkill(skillId)) {
                src.sendFailure(Component.translatable("command.wildaside.skill.summary.missing", skillId.toString(), player.getDisplayName()));
                continue;
            }

            BioengineeringSkillUtils.removeAndSyncToClient(player, skillId);
            removed++;
        }

        int finalRemoved = removed;
        src.sendSuccess(() -> Component.translatable("command.wildaside.skill.summary.remove", skillId.toString(), finalRemoved), true);

        return removed;
    }

    private static int skillHas(CommandContext<CommandSourceStack> ctx) {
        Collection<ServerPlayer> players;
        try {
            players = EntityArgument.getPlayers(ctx, "players");
        }
        catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("No players found"));
            return 0;
        }

        ResourceLocation skillId = ResourceLocationArgument.getId(ctx, "skill");
        CommandSourceStack src = ctx.getSource();

        int count = (int) players.stream().map(
                        target -> target.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null))
                .filter(skills -> skills != null && skills.hasSkill(skillId)).count();

        if (count == 0) {
            src.sendSuccess(
                    () -> Component.translatable("command.wildaside.skill.summary.missing_all", skillId.toString()), false
            );
        }
        else if (count == 1) {
            src.sendSuccess(
                    () -> Component.translatable("command.wildaside.skill.summary.have_single", skillId.toString()), false
            );
        }
        else {
            src.sendSuccess(
                    () -> Component.translatable("command.wildaside.skill.summary.have", count, skillId.toString()), false
            );
        }

        return count;
    }

    private static int skillList(CommandContext<CommandSourceStack> ctx) {
        Collection<ServerPlayer> players;
        try {
            players = EntityArgument.getPlayers(ctx, "players");
        }
        catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("No players found"));
            return 0;
        }

        CommandSourceStack src = ctx.getSource();

        for (ServerPlayer player : players) {
            player.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {

                Set<ResourceLocation> skills = cap.getSkills();

                if (skills.isEmpty()) {
                    src.sendSuccess(() -> Component.translatable("command.wildaside.skill.list.empty", player.getDisplayName()), false);
                    return;
                }

                MutableComponent header = Component.translatable("command.wildaside.skill.list.header", player.getDisplayName()).withStyle(ChatFormatting.AQUA);

                src.sendSuccess(() -> header, false);

                for (ResourceLocation id : skills) {
                    src.sendSuccess(() -> Component.literal(" - " + id), false);
                }
            });
        }

        return players.size();
    }

    private static int pointOperation(CommandContext<CommandSourceStack> ctx, BioengineeringSkillPointOperation op) {
        Collection<ServerPlayer> players;
        try {
            players = EntityArgument.getPlayers(ctx, "players");
        }
        catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.skill.points.no_players"));
            return 0;
        }

        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        CommandSourceStack src = ctx.getSource();

        for (ServerPlayer player : players) {
            BioengineeringSkillUtils.handlePointsAndSyncToClient(player, amount, op);
        }

        src.sendSuccess(
                () -> Component.translatable(
                        "command.wildaside.skill.points.operation",
                        Component.translatable("command.wildaside.skill.points.op." + op.name().toLowerCase()),
                        amount,
                        players.size()
                ),
                true
        );

        return players.size();
    }

    private static int pointGet(CommandContext<CommandSourceStack> ctx) {
        Collection<ServerPlayer> players;
        try {
            players = EntityArgument.getPlayers(ctx, "players");
        }
        catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.translatable("command.wildaside.skill.points.no_players"));
            return 0;
        }

        CommandSourceStack src = ctx.getSource();

        for (ServerPlayer player : players) {
            player.getCapability(BioengineeringSkillsCapability.INSTANCE).ifPresent(cap -> {
                src.sendSuccess(
                        () -> Component.translatable(
                                "command.wildaside.skill.points.get",
                                player.getDisplayName(),
                                cap.getPoints()
                        ),
                        false
                );
            });
        }

        return players.size();
    }
}