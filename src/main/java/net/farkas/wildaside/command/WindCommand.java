package net.farkas.wildaside.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.farkas.wildaside.util.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class WindCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setwind")
                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("z",  DoubleArgumentType.doubleArg())
                                .then(Commands.argument("strength",  FloatArgumentType.floatArg(0))
                                        .executes(ctx -> {
                                            double x = DoubleArgumentType.getDouble(ctx, "x");
                                            double y = DoubleArgumentType.getDouble(ctx, "y");
                                            double z = DoubleArgumentType.getDouble(ctx, "z");
                                            float s = FloatArgumentType.getFloat(ctx, "strength");
                                            WindManager.setWind(new Vec3(x, y, z), s);
                                            ctx.getSource().sendSuccess(() -> Component.literal("Set wind to (" + x + " " + y + " " + z + ") strength=" + s), true);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        ))
                )
        );
    }
}
