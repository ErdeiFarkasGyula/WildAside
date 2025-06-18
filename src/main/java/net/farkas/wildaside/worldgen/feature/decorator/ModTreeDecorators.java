package net.farkas.wildaside.worldgen.feature.decorator;

import net.farkas.wildaside.WildAside;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> DECORATORS =
            DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, WildAside.MOD_ID);

    public static final RegistryObject<TreeDecoratorType<FallenLeavesDecorator>> FALLEN_LEAVES =
            DECORATORS.register("fallen_leaves", () -> new TreeDecoratorType<>(FallenLeavesDecorator.CODEC));

    public static void register(IEventBus bus) {
        DECORATORS.register(bus);
    }
}
