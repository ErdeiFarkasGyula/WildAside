package net.farkas.wildaside.block.entity;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.sign.ModHangingSignBlockEntity;
import net.farkas.wildaside.block.entity.sign.ModSignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WildAside.MOD_ID);

    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> MOD_SIGN =
            BLOCK_ENTITIES.register("mod_sign", () -> BlockEntityType.Builder.of(ModSignBlockEntity::new,
                    ModBlocks.SUBSTILIUM_SIGN.get(), ModBlocks.SUBSTILIUM_WALL_SIGN.get(),
                    ModBlocks.HICKORY_SIGN.get(), ModBlocks.HICKORY_HANGING_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModHangingSignBlockEntity>> MOD_HANGING_SIGN =
            BLOCK_ENTITIES.register("mod_hanging_sign", () -> BlockEntityType.Builder.of(ModHangingSignBlockEntity::new,
                    ModBlocks.SUBSTILIUM_HANGING_SIGN.get(), ModBlocks.SUBSTILIUM_WALL_HANGING_SIGN.get(),
                    ModBlocks.HICKORY_HANGING_SIGN.get(),  ModBlocks.HICKORY_WALL_HANGING_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<BioengineeringWorkstationBlockEntity>> BIOENGINEERING_WORKSTATION =
            BLOCK_ENTITIES.register("bioengineering_workstation", () -> BlockEntityType.Builder.of(BioengineeringWorkstationBlockEntity::new,
                    ModBlocks.BIOENGINEERING_WORKSTATION.get()).build(null));

    public static final RegistryObject<BlockEntityType<PotionBlasterBlockEntity>> POTION_BLASTER =
            BLOCK_ENTITIES.register("potion_blaster",
                    () -> BlockEntityType.Builder.of(PotionBlasterBlockEntity::new, ModBlocks.POTION_BLASTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SporeBlasterBlockEntity>> SPORE_BLASTER =
            BLOCK_ENTITIES.register("spore_blaster",
                    () -> BlockEntityType.Builder.of(SporeBlasterBlockEntity::new, ModBlocks.SPORE_BLASTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<NaturalSporeBlasterBlockEntity>> NATURAL_SPORE_BLASTER =
            BLOCK_ENTITIES.register("natural_spore_blaster",
                    () -> BlockEntityType.Builder.of(NaturalSporeBlasterBlockEntity::new, ModBlocks.NATURAL_SPORE_BLASTER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
