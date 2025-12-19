package net.farkas.wildaside.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.farkas.wildaside.capability.bioengineering.BioengineeringSkillsCapability;
import net.farkas.wildaside.capability.bioengineering.IBioengineeringSkills;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillRegistry;
import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Set;

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

        root.then(
                Commands.literal(DNA)
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument(TARGET, EntityArgument.entity())
                                .then(Commands.argument(TRAIT, StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            for (Trait trait : TraitRegistry.TRAITS) {
                                                builder.suggest(STABILITY);
                                                builder.suggest(trait.getName());
                                            }
                                            return builder.buildFuture();
                                        })

                                        .then(Commands.literal("get")
                                                .executes(ctx -> {
                                                    var target = EntityArgument.getEntity(ctx, TARGET);
                                                    return getGene(ctx, target);
                                                }))

                                        .then(Commands.literal("set")
                                                .then(Commands.argument(VALUE, FloatArgumentType.floatArg())
                                                        .executes(ctx -> {
                                                            var target = EntityArgument.getEntity(ctx, TARGET);
                                                            return applyGene(ctx, target);
                                                        })
                                                )
                                        )
                                )
                        )
        );

        root.then(
                Commands.literal("bio_skill")
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
                        )
        );

        dispatcher.register(root);
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
            String entityString = affected > 1 ? "command.wildaside.contamination.entities" : "command.wildaside.contamination.entity";
            String actionString = "command.wildaside.contamination.action." + action;
            applyContamination(ctx, actionString, affected, entityString);
        }
        else {
            source.sendFailure(Component.translatable("command.wildaside.contamination.no_valid_entities"));
        }
        return affected;
    }

    private static int getGene(CommandContext<CommandSourceStack> ctx, Entity target) {
        if (!(target instanceof LivingEntity living)) return 0;

        String traitName = StringArgumentType.getString(ctx, TRAIT);

        Trait trait = TraitRegistry.getByName(traitName);
        if (trait == null) {
            unknownTrait(ctx, traitName);
            return 0;
        }

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Gene gene = dna.getGenes().get(trait);
            if (gene == null) {
                ctx.getSource().sendFailure(Component.translatable("dna.wildaside.no_gene_for_trait", traitName));
                return;
            }

            Allele a = gene.getAlleleA();
            Allele b = gene.getAlleleB();

            ctx.getSource().sendSuccess(() -> Component.translatable("item.wildaside.gene")
                    .append(": ")
                    .append(TraitRegistry.translatableTrait(trait)), false);

            sendAllele(ctx, "A", a);
            sendAllele(ctx, "B", b);

            ctx.getSource().sendSuccess(() ->
                            Component.translatable("dna.wildaside.expressed_value").append(gene.getExpressedValueHolder().format()),
                    false
            );
        });

        return Command.SINGLE_SUCCESS;
    }

    private static void sendAllele(CommandContext<CommandSourceStack> ctx, String label, Allele allele) {
        ctx.getSource().sendSuccess(() ->
                        Component.literal(" - ")
                                .append(Component.translatable("dna.wildaside.allele"))
                                .append(" " + label + ": ")
                                .append(allele.getValueHolder().format())
                                .append(" | ")
                                .append(allele.getDominance().getComponent())
                                .append(" | ")
                                .append(Component.translatable("dna.wildaside.mutation_rate"))
                                .append(" = " + allele.getMutationRate())
                                .append(" | ")
                                .append(Component.translatable("dna.wildaside.stability"))
                                .append(" = " + allele.getStability()),
                false
        );
    }


    private static int applyGene(CommandContext<CommandSourceStack> ctx, Entity target) {
        if (!(target instanceof LivingEntity living)) return 0;

        String traitName = StringArgumentType.getString(ctx, TRAIT);
        Trait trait = TraitRegistry.getByName(traitName);

        if (trait == null) {
            unknownTrait(ctx, traitName);
            return 0;
        }

        living.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Gene old = dna.getGenes().get(trait);
            if (old == null) return;

            AlleleValue newValue = TraitRegistry.parseValue(trait, old.getExpressedValueHolder(), ctx);

            Allele a = old.getAlleleA().copyWithValue(newValue);
            Allele b = old.getAlleleB().copyWithValue(newValue);

            Gene gene = new Gene(trait, a, b);
            dna.getGenes().put(trait, gene);
            dna.applyGene(living, gene);
        });

        ctx.getSource().sendSuccess(() ->
                        Component.literal("Updated gene ").append(TraitRegistry.translatableTrait(trait)),
                true
        );

        return Command.SINGLE_SUCCESS;
    }


    private static void unknownTrait(CommandContext<CommandSourceStack> context, String traitName) {
        context.getSource().sendFailure(
                Component.translatable("command.wildaside.dna.unknown_trait", traitName));
    }

    public static void getTrait(CommandContext<CommandSourceStack> context, LivingEntity livingEntity, Trait trait, AlleleValue value) {
        context.getSource().sendSuccess(() ->
                Component.translatable("command.wildaside.dna.get_trait", livingEntity.getName(), value.format().getString(), TraitRegistry.translatableTrait(trait)), false);
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
            ctx.getSource().sendFailure(Component.literal("No players found"));
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