package net.farkas.wildaside.entity;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.custom.*;
import net.farkas.wildaside.entity.custom.hickory.HickoryLeafProjectile;
import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.farkas.wildaside.entity.custom.vibrion.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WildAside.MOD_ID);

    public static final RegistryObject<EntityType<ModBoatEntity>> MOD_BOAT =
            ENTITY_TYPES.register("mod_boat", () -> EntityType.Builder.<ModBoatEntity>of(ModBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .build("mod_boat"));
    public static final RegistryObject<EntityType<ModChestBoatEntity>> MOD_CHEST_BOAT =
            ENTITY_TYPES.register("mod_chest_boat", () -> EntityType.Builder.<ModChestBoatEntity>of(ModChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375f, 0.5625f)
                    .build("mod_chest_boat"));

    public static final RegistryObject<EntityType<SporeBombEntity>> SPORE_BOMB =
            ENTITY_TYPES.register("spore_bomb", () -> EntityType.Builder.<SporeBombEntity>of(SporeBombEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("spore_bomb"));
    public static final RegistryObject<EntityType<FertiliserBombEntity>> FERTILISER_BOMB =
            ENTITY_TYPES.register("fertiliser_bomb", () -> EntityType.Builder.<FertiliserBombEntity>of(FertiliserBombEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("fertiliser_bomb"));
    public static final RegistryObject<EntityType<SporeArrowEntity>> SPORE_ARROW =
            ENTITY_TYPES.register("spore_arrow", () -> EntityType.Builder.<SporeArrowEntity>of(SporeArrowEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("spore_arrow"));

    public static final RegistryObject<EntityType<MucellithEntity>> MUCELLITH =
            ENTITY_TYPES.register("mucellith", () -> EntityType.Builder.<MucellithEntity>of(MucellithEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 2f)
                    .build("mucellith"));

    public static final RegistryObject<EntityType<ContaminatedCreeperEntity>> CONTAMINATED_CREEPER =
            ENTITY_TYPES.register("contaminated_creeper", () -> EntityType.Builder.<ContaminatedCreeperEntity>of(ContaminatedCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .build("contaminated_creeper"));

    public static final RegistryObject<EntityType<BacillusBlobEntity>> BACILLUS_BLOB =
            ENTITY_TYPES.register("bacillus_blob", () -> EntityType.Builder.<BacillusBlobEntity>of(BacillusBlobEntity::new, MobCategory.CREATURE)
                    .sized(0.3f, 0.4f)
                    .clientTrackingRange(8)
                    .build("bacillus_blob"));

    public static final RegistryObject<EntityType<HickoryTreantEntity>> HICKORY_TREANT =
            ENTITY_TYPES.register("hickory_treant", () -> EntityType.Builder.<HickoryTreantEntity>of(HickoryTreantEntity::new, MobCategory.MONSTER)
                    .sized(1f, 2f)
                    .build("hickory_treant"));
    public static final RegistryObject<EntityType<HickoryLeafProjectile>> HICKORY_LEAF_PROJECTILE =
            ENTITY_TYPES.register("hickory_leaf_projectile", () -> EntityType.Builder.<HickoryLeafProjectile>of(HickoryLeafProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .build("hickory_leaf_projectile"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}