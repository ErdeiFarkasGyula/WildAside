package net.farkas.wildaside.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.farkas.wildaside.config.Config;
import net.farkas.wildaside.network.WindSavedData;
import net.farkas.wildaside.util.ContaminationHandler;
import net.farkas.wildaside.network.WindData;
import net.farkas.wildaside.util.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

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

                                                                    Vec3 vec = new Vec3(x, y ,z);
                                                                    WindManager.setWind(vec, s);

                                                                    ServerLevel serverLevel = ctx.getSource().getLevel();
                                                                    WindSavedData windSavedData = WindSavedData.get(serverLevel);
                                                                    windSavedData.setWeather(serverLevel.isRaining(), serverLevel.isThundering());
                                                                    windSavedData.setWind(vec, s);

                                                                    ctx.getSource().sendSuccess(
                                                                            () -> Component.literal("Set wind to (" + x + ", " + y + ", " + z + ") strength=" + s),
                                                                            true
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
                                            () -> Component.literal("Set random wind to (" + dir.x + ", " + dir.y + ", " + dir.z + ") strength=" + windData.strength()),
                                            true
                                    );
                                    return Command.SINGLE_SUCCESS;
                                })
                        )

                        .then(Commands.literal("get")
                                .executes(ctx -> {
                                    float strength = WindManager.getStrength();
                                    Vec3 dir = WindManager.getDirection();
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("Current wind: (" + dir.x + ", " + dir.y + ", " + dir.z + ") strength=" + strength),
                                            false
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
                                        Config.setShowUpdates(value);
                                        context.getSource().sendSuccess(() ->
                                                Component.literal("§7[Wild Aside] Update notifications " + (value ? "enabled" : "disabled") + "."), false);
                                        return Command.SINGLE_SUCCESS;
                                })
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
                    source.sendSuccess(() -> Component.literal("Added " + amount + " of contamination to " + living.getName().getString()), false);
                    affected++;
                }
                case "get" -> {
                    int current = ContaminationHandler.getDose(living);
                    source.sendSuccess(() -> Component.literal(living.getName().getString() + " has " + current + " contamination"), false);
                    affected++;
                }
                case "set" -> {
                    ContaminationHandler.setDose(living, amount);
                    source.sendSuccess(() -> Component.literal("Set contamination of " + living.getName().getString() + " to " + amount), false);
                    affected++;
                }
                case "clear" -> {
                    ContaminationHandler.setDose(living, 0);
                    source.sendSuccess(() -> Component.literal("Cleared contamination of " + living.getName().getString()), false);
                    affected++;
                }
                default -> {
                    source.sendFailure(Component.literal("Invalid action: " + action));
                    return 0;
                }
            }
        }

        if (affected > 0) {
            String entityString = affected > 1 ? "entities" : "entity";
            int finalAffected = affected;
            source.sendSuccess(() -> Component.literal("Applied '" + action + "' to " + finalAffected + " " + entityString), true);
        } else {
            source.sendFailure(Component.literal("No valid living entities found."));
        }
        return affected;
    }
}