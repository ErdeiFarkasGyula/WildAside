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
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkills;
import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.Traits;
import net.farkas.wildaside.network.WindSavedData;
import net.farkas.wildaside.util.ContaminationHandler;
import net.farkas.wildaside.network.WindData;
import net.farkas.wildaside.util.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("wildaside")
                .requires(cs -> cs.hasPermission(2));

        root.then(
                Commands.literal("contamination")
                        .then(Commands.argument("action", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"add", "get", "set", "clear"}, builder))
                                .then(Commands.argument("targets", EntityArgument.entities())
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
                                            for (Trait trait : Traits.TRAITS) {
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
                        .requires(source -> source.hasPermission(2))

                        .then(Commands.argument(PLAYER, EntityArgument.player())
                                .then(Commands.literal("unlock")
                                        .then(Commands.argument(SKILL, ResourceLocationArgument.id())
                                                .suggests((ctx, builder) ->
                                                        SharedSuggestionProvider.suggest(
                                                                BioengineeringSkills.all()
                                                                        .stream()
                                                                        .map(s -> s.getId().toString())
                                                                        .toList(),
                                                                builder
                                                        )
                                                )
                                                .executes(ctx -> {
                                                    Player player = EntityArgument.getPlayer(ctx, PLAYER);
                                                    ResourceLocation skillId = ResourceLocationArgument.getId(ctx, SKILL);
                                                    return unlockSkill(ctx.getSource(), player, skillId);
                                                })
                                        )
                                )

                                .then(Commands.literal("remove")
                                        .then(Commands.argument(SKILL, ResourceLocationArgument.id())
                                                .suggests((ctx, builder) ->
                                                        SharedSuggestionProvider.suggest(
                                                                BioengineeringSkills.all()
                                                                        .stream()
                                                                        .map(s -> s.getId().toString())
                                                                        .toList(),
                                                                builder
                                                        )
                                                )
                                                .executes(ctx -> {
                                                    ResourceLocation skillId = ResourceLocationArgument.getId(ctx, SKILL);
                                                    return removeSkill(ctx.getSource(), skillId);
                                                })
                                        )
                                )

                                .then(Commands.literal("has")
                                        .then(Commands.argument(SKILL, ResourceLocationArgument.id())
                                                .suggests((ctx, builder) ->
                                                        SharedSuggestionProvider.suggest(
                                                                BioengineeringSkills.all()
                                                                        .stream()
                                                                        .map(s -> s.getId().toString())
                                                                        .toList(),
                                                                builder
                                                        )
                                                )
                                                .executes(ctx -> {
                                                    ResourceLocation skillId = ResourceLocationArgument.getId(ctx, SKILL);
                                                    return checkSkill(ctx.getSource(), skillId);
                                                })
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

    public static int getGene(CommandContext<CommandSourceStack> ctx, Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            String traitName = StringArgumentType.getString(ctx, TRAIT);
            if (traitName.equals(STABILITY)) {
                livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    float value = dna.getStability();
                    ctx.getSource().sendSuccess(() ->
                            Component.translatable("command.wildaside.dna.get_trait", livingEntity.getName(), value,
                                    Component.translatable("dna.wildaside.stability")), false);
                });
            }
            else {
                Trait trait = Traits.getByName(traitName);
                if (trait == null) {
                    unknownTrait(ctx, traitName);
                    return 0;
                }
                livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                    float value = Traits.getTraitValue(dna.getGenes(), trait);
                    getTrait(ctx, livingEntity, trait, value);
                });
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int applyGene(CommandContext<CommandSourceStack> ctx, Entity target) {
        if (!(target instanceof LivingEntity livingEntity)) return 0;

        String traitName = StringArgumentType.getString(ctx, TRAIT);
        float value = FloatArgumentType.getFloat(ctx, VALUE);

        if (traitName.equalsIgnoreCase(STABILITY)) {
            livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                dna.setStability(value);
            });

            ctx.getSource().sendSuccess(() ->
                    Component.translatable("command.wildaside.dna.set_trait",
                            Component.translatable("dna.wildaside.stability"), livingEntity.getName(), value), false);

            return Command.SINGLE_SUCCESS;
        }

        Trait trait = Traits.getByName(traitName);
        if (trait == null) {
            unknownTrait(ctx, traitName);
            return 0;
        }

        Allele alleleA = new Allele(value, 0.05f, trait.getInstabilityModifier(), Dominance.DOMINANT);
        Allele alleleB = new Allele(value, 0.05f, trait.getInstabilityModifier(), Dominance.RECESSIVE);

        livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Gene gene = new Gene(trait, alleleA, alleleB);
            dna.getGenes().put(trait, gene);
            dna.applyGene(livingEntity, gene);
        });

        Component message = Component.translatable(
                "command.wildaside.dna.set_trait",
                Traits.translatableTrait(trait),
                livingEntity.getName(),
                String.valueOf(value)
        );

        ctx.getSource().sendSuccess(() -> message, true);
        return Command.SINGLE_SUCCESS;
    }

    private static void unknownTrait(CommandContext<CommandSourceStack> context, String traitName) {
        context.getSource().sendFailure(
                Component.translatable("command.wildaside.dna.unknown_trait", traitName));
    }

    public static void getTrait(CommandContext<CommandSourceStack> context, LivingEntity livingEntity, Trait trait, float value) {
        context.getSource().sendSuccess(() ->
                Component.translatable("command.wildaside.dna.get_trait", livingEntity.getName(), value, Traits.translatableTrait(trait)), false);
    }

    public static void applyContamination(CommandContext<CommandSourceStack> context, String action, int finalAffected, String entityString) {
        context.getSource().sendSuccess(() ->
                Component.translatable("command.wildaside.contamination.apply_contamination",
                        Component.translatable(action).getString().toLowerCase(), finalAffected,
                        Component.translatable(entityString).getString().toLowerCase()), true);
    }

    private static int unlockSkill(CommandSourceStack source, Player player, ResourceLocation skillId) {
        IBioengineeringSkills skills = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);

        if (skills == null) return 0;
        if (skills.hasSkill(skillId)) {
            source.sendFailure(Component.literal("You already have this skill: " + skillId));
            return 0;
        }

        BioengineeringSkillUtils.unlockSkill(player, skillId);
        source.sendSuccess(() -> Component.literal("Unlocked skill: " + skillId), true);

        return 1;
    }

    private static int removeSkill(CommandSourceStack source, ResourceLocation skillId) {
        ServerPlayer player = source.getPlayer();
        IBioengineeringSkills skills = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);

        if (skills == null) return 0;
        if (!skills.hasSkill(skillId)) {
            source.sendFailure(Component.literal("You don't have this skill: " + skillId));
            return 0;
        }

        skills.removeSkillFromUnlocked(skillId);
        source.sendSuccess(() -> Component.literal("Removed skill: " + skillId), true);
        return 1;
    }

    private static int checkSkill(CommandSourceStack source, ResourceLocation skillId) {
        ServerPlayer player = source.getPlayer();
        IBioengineeringSkills skills = player.getCapability(BioengineeringSkillsCapability.INSTANCE).orElse(null);

        boolean has = skills != null && skills.hasSkill(skillId);

        source.sendSuccess(
                () -> Component.literal("Skill " + skillId + ": " + (has ? "§aUNLOCKED" : "§cNOT unlocked")),
                false
        );

        return has ? 1 : 0;
    }
}