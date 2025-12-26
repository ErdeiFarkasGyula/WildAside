package net.farkas.wildaside.advancement;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.advancements.*;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator;
import net.minecraftforge.common.extensions.IForgeAdvancementBuilder;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModAdvancements implements AdvancementGenerator {
    public static final ResourceLocation WILD_WILDER_WILDEST = id("wild_wilder_wildest");
    public static final ResourceLocation HICKORY_FOREST = id("hickory_forest");
    public static final ResourceLocation COLOURFUL_INNIT = id("colourful_innit");
    public static final ResourceLocation GLOW_UP = id("glow_up");

    public static final ResourceLocation DESCENT = id("descent");
    public static final ResourceLocation THROUGH_HUNDREDS_OF_MUTATIONS = id("through_hundreds_of_mutations");
    public static final ResourceLocation BACTERIA_BRICKS = id("bacteria_bricks");
    public static final ResourceLocation ITS_SHEARING_TIME = id("its_shearing_time");
    public static final ResourceLocation PETRIFIED_PRESCRIPTION = id("petrified_prescription");
    public static final ResourceLocation WE_NEED_TO_COOK = id("we_need_to_cook");
    public static final ResourceLocation BACTERIA_BEACON = id("bacteria_beacon");
    public static final ResourceLocation FERTILE_FOREST = id("fertile_forest");
    public static final ResourceLocation PURIFICATION_PILL = id("purification_pill");
    public static final ResourceLocation WEAPONS_OF_MASS_INFECTION = id("weapons_of_mass_infection");
    public static final ResourceLocation BREW_BARRAGE = id("brew_barrage");
    public static final ResourceLocation BLASTER_BUSTED = id("blaster_busted");
    public static final ResourceLocation GLIMMERING_GLASS = id("glimmering_glass");
    public static final ResourceLocation LIFE_LEECH = id("life_leech");

    public static final ResourceLocation CONTAMINATION = id("contamination");
    public static final ResourceLocation IMMUNITY = id("immunity");
    public static final ResourceLocation LIFESTEAL = id("lifesteal");
    public static final ResourceLocation MUCELLITH = id("mucellith");

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper helper) {
        Advancement wildWilderWildest = builder(WILD_WILDER_WILDEST, ModBlocks.VIBRION_BLOCK.get(), FrameType.TASK,
                new ResourceLocation(WildAside.MOD_ID, "textures/gui/entorium_shroom.png"))
                .showToast(false).announceToChat(false)
                .addImpossible()
                .saveWithHelper(saver, helper);

        Advancement colourfulInnit = builder(COLOURFUL_INNIT, ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES.get(), FrameType.TASK)
                .parent(wildWilderWildest)
                .addCriterion("in_glowing_biome", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.inBiome(ResourceKey.create(Registries.BIOME,
                                new ResourceLocation(WildAside.MOD_ID, "glowing_hickory_forest")))))
                .rewards(AdvancementRewards.Builder.experience(3)
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "spotted_wintergreen_to_dye"))
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "pinkster_flower_to_dye")))
                .saveWithHelper(saver, helper);

        Advancement glowUp = builder(GLOW_UP, ModBlocks.RED_GLOWING_HICKORY_LEAVES.get(), FrameType.GOAL)
                .parent(colourfulInnit)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(10))
                .saveWithHelper(saver, helper);

        Advancement descent = builder(DESCENT, ModBlocks.SUBSTILIUM_SOIL.get(), FrameType.TASK)
                .parent(wildWilderWildest)
                .addCriterion("in_hive", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.inBiome(ResourceKey.create(Registries.BIOME,
                                new ResourceLocation(WildAside.MOD_ID, "vibrion_hive")))))
                .rewards(AdvancementRewards.Builder.experience(2))
                .saveWithHelper(saver, helper);

        Advancement throughHundreds = builder(THROUGH_HUNDREDS_OF_MUTATIONS, ModItems.VIBRION.get(), FrameType.TASK)
                .parent(descent)
                .addCriterion("has_vibrion", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VIBRION.get()))
                .rewards(AdvancementRewards.Builder.experience(3)
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "vibrion_block"))
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "vibrion_to_dye")))
                .saveWithHelper(saver, helper);

        AdvancementRewards.Builder bacteriaRewards = AdvancementRewards.Builder.experience(3);
        bacteriaBrickRecipes().forEach(bacteriaRewards::addRecipe);

        Advancement bacteriaBricks = builder(BACTERIA_BRICKS, ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get(), FrameType.TASK)
                .parent(descent)
                .addCriterion("has_bricks", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get()))
                .rewards(bacteriaRewards)
                .saveWithHelper(saver, helper);

        Advancement itsShearingTime = builder(ITS_SHEARING_TIME, ModBlocks.OVERGROWN_ENTORIUM_ORE.get(), FrameType.TASK)
                .parent(descent)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(5))
                .saveWithHelper(saver, helper);

        Advancement petrifiedPrescription = builder(PETRIFIED_PRESCRIPTION, ModItems.ENTORIUM.get(), FrameType.GOAL)
                .parent(itsShearingTime)
                .addCriterion("has_entorium", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENTORIUM.get()))
                .rewards(AdvancementRewards.Builder.experience(5)
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "entorium_pill"))
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "spore_blaster"))
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "entorium_gauntlet")))
                .saveWithHelper(saver, helper);

        Advancement weNeedToCook = builder(WE_NEED_TO_COOK, ModBlocks.BIOENGINEERING_WORKSTATION.get(), FrameType.GOAL)
                .parent(petrifiedPrescription)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(10))
                .saveWithHelper(saver, helper);

        Advancement bacteriaBeacon = builder(BACTERIA_BEACON, ModBlocks.SPORE_BLASTER.get(), FrameType.TASK)
                .parent(weNeedToCook)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(10))
                .saveWithHelper(saver, helper);

        Advancement fertileForest = builder(FERTILE_FOREST, ModItems.FERTILISER_BOMB.get(), FrameType.CHALLENGE)
                .parent(weNeedToCook)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(6))
                .saveWithHelper(saver, helper);

        Advancement purificationPill = builder(PURIFICATION_PILL, ModItems.ENTORIUM_PILL.get(), FrameType.CHALLENGE)
                .parent(weNeedToCook)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(4))
                .saveWithHelper(saver, helper);

        Advancement weaponsOfMassInfection = builder(WEAPONS_OF_MASS_INFECTION, ModItems.SPORE_BOMB.get(), FrameType.CHALLENGE)
                .parent(weNeedToCook)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(5))
                .saveWithHelper(saver, helper);

        Advancement brewBarrage = builder(BREW_BARRAGE, ModBlocks.POTION_BLASTER.get(), FrameType.CHALLENGE)
                .parent(bacteriaBeacon)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(10))
                .saveWithHelper(saver, helper);

        Advancement blasterBusted = builder(BLASTER_BUSTED, ModBlocks.NATURAL_SPORE_BLASTER.get(), FrameType.TASK)
                .parent(descent)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(5))
                .saveWithHelper(saver, helper);

        Advancement glimmeringGlass = builder(GLIMMERING_GLASS, ModBlocks.LIT_VIBRION_GLASS.get(), FrameType.TASK)
                .parent(weNeedToCook)
                .addCriterion("placed_glass",
                        ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(
                                new AnyOfCondition.Builder()
                                        .or(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.LIT_VIBRION_GLASS.get()))
                                        .or(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.LIT_VIBRION_GLASS_PANE.get()))
                        )
                )
                .rewards(AdvancementRewards.Builder.experience(3))
                .saveWithHelper(saver, helper);

        Advancement lifeLeech = builder(LIFE_LEECH, ModItems.MUCELLITH_JAW.get(), FrameType.GOAL)
                .parent(descent)
                .addImpossible()
                .rewards(AdvancementRewards.Builder.experience(10))
                .saveWithHelper(saver, helper);

        builder(CONTAMINATION, Items.POTION, FrameType.TASK)
                .parent(wildWilderWildest)
                .addCriterion("contamination", EffectsChangedTrigger.TriggerInstance.hasEffects(
                        MobEffectsPredicate.effects().and(ModMobEffects.CONTAMINATION.get(), new MobEffectsPredicate.MobEffectInstancePredicate())))
                .noDisplay()
                .saveWithHelper(saver, helper);

        builder(IMMUNITY, Items.POTION, FrameType.TASK)
                .parent(wildWilderWildest)
                .addCriterion("immunity", EffectsChangedTrigger.TriggerInstance.hasEffects(
                        MobEffectsPredicate.effects().and(ModMobEffects.IMMUNITY.get(), new MobEffectsPredicate.MobEffectInstancePredicate())))
                .noDisplay()
                .saveWithHelper(saver, helper);

        builder(LIFESTEAL, Items.POTION, FrameType.TASK)
                .parent(wildWilderWildest)
                .addCriterion("lifesteal", EffectsChangedTrigger.TriggerInstance.hasEffects(
                        MobEffectsPredicate.effects().and(ModMobEffects.LIFESTEAL.get(), new MobEffectsPredicate.MobEffectInstancePredicate())))
                .noDisplay()
                .saveWithHelper(saver, helper);

        builder(MUCELLITH, Items.SPYGLASS, FrameType.TASK)
                .addImpossible()
                .noDisplay()
                .saveWithHelper(saver, helper);

        Advancement hickoryForest = builder(HICKORY_FOREST, Items.ACACIA_LEAVES, FrameType.TASK)
                .parent(wildWilderWildest)
                .addCriterion("in_biome", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.inBiome(ResourceKey.create(Registries.BIOME,
                                new ResourceLocation(WildAside.MOD_ID, "hickory_forest")))))
                .rewards(AdvancementRewards.Builder.experience(3)
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "spotted_wintergreen_to_dye"))
                        .addRecipe(new ResourceLocation(WildAside.MOD_ID, "pinkster_flower_to_dye")))
                .noDisplay()
                .saveWithHelper(saver, helper);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(WildAside.MOD_ID, path);
    }

    private static BlockPredicate blockOr(Block... blocks) {
        return BlockPredicate.Builder.block().of(blocks).build();
    }

    private static AdvancementBuilder builder(ResourceLocation id, ItemLike icon, FrameType frame) {
        return new AdvancementBuilder(id, icon, frame, null);
    }

    private static AdvancementBuilder builder(ResourceLocation id, ItemLike icon, FrameType frame, ResourceLocation background) {
        return new AdvancementBuilder(id, icon, frame, background);
    }

    private static List<ResourceLocation> bacteriaBrickRecipes() {
        return List.of(
                rl("compressed_substilium_soil"),
                rl("substilium_soil_tiles"),
                rl("chiseled_substilium_soil"),
                rl("cracked_substilium_soil_tiles"),
                rl("cracked_substilium_soil_tiles_2"),
                rl("smooth_substilium_soil"),
                rl("substilium_soil_tile_stairs"),
                rl("substilium_soil_tile_stairs_cut"),
                rl("substilium_soil_tile_slab"),
                rl("substilium_soil_tile_slab_cut"),
                rl("substilium_soil_tile_pressure_plate_craft"),
                rl("substilium_soil_tile_pressure_plate_cut"),
                rl("substilium_soil_tile_wall"),
                rl("substilium_soil_tile_wall_cut"),
                rl("substilium_soil_tile_button_craft"),
                rl("substilium_soil_tile_button_cut"),
                rl("smooth_substilium_soil_button_cut"),
                rl("smooth_substilium_soil_button_craft"),
                rl("smooth_substilium_soil_stairs_craft"),
                rl("smooth_substilium_soil_stairs_cut"),
                rl("smooth_substilium_soil_slab_craft"),
                rl("smooth_substilium_soil_slab_cut"),
                rl("smooth_substilium_soil_wall_craft"),
                rl("smooth_substilium_soil_wall_cut"),
                rl("smooth_substilium_soil_pressure_plate_craft"),
                rl("smooth_substilium_soil_pressure_plate_cut"),
                rl("cracked_substilium_soil_tile_button_cut"),
                rl("cracked_substilium_soil_tile_button_craft"),
                rl("cracked_substilium_soil_tile_pressure_plate_cut"),
                rl("cracked_substilium_soil_tile_stairs_craft"),
                rl("cracked_substilium_soil_tile_stairs_cut"),
                rl("cracked_substilium_soil_tile_slab_craft"),
                rl("cracked_substilium_soil_tile_slab_cut"),
                rl("cracked_substilium_soil_tile_wall_craft"),
                rl("cracked_substilium_soil_tile_wall_cut"),
                rl("cracked_substilium_soil_tile_pressure_plate_craft")
        );
    }

    private static ResourceLocation rl(String path) {
        return new ResourceLocation(WildAside.MOD_ID, path);
    }

    private static class AdvancementBuilder {
        private final ResourceLocation id;
        private final ItemLike icon;
        private final FrameType frame;
        private final ResourceLocation background;
        private boolean showToast = true;
        private boolean announce = true;
        private boolean hidden = false;
        private boolean withDisplay = true;
        private final Advancement.Builder builder;

        AdvancementBuilder(ResourceLocation id, ItemLike icon, FrameType frame, ResourceLocation background) {
            this.id = id;
            this.icon = icon;
            this.frame = frame;
            this.background = background;
            this.builder = Advancement.Builder.advancement();
            rebuildDisplay();
        }

        private void rebuildDisplay() {
            if (!withDisplay) return;
            builder.display(new DisplayInfo(
                    new ItemStack(icon.asItem()),
                    Component.translatable("advancements." + id.getPath() + ".title"),
                    Component.translatable("advancements." + id.getPath() + ".descr"),
                    background,
                    frame,
                    showToast,
                    announce,
                    hidden
            ));
        }

        AdvancementBuilder parent(Advancement parent) {
            builder.parent(parent);
            return this;
        }

        AdvancementBuilder addCriterion(String name, CriterionTriggerInstance criterion) {
            builder.addCriterion(name, criterion);
            return this;
        }

        AdvancementBuilder addImpossible() {
            return addCriterion("impossible", new ImpossibleTrigger.TriggerInstance());
        }

        AdvancementBuilder rewards(AdvancementRewards.Builder rewards) {
            builder.rewards(rewards);
            return this;
        }

        AdvancementBuilder showToast(boolean v) {
            this.showToast = v;
            rebuildDisplay();
            return this;
        }

        AdvancementBuilder announceToChat(boolean v) {
            this.announce = v;
            rebuildDisplay();
            return this;
        }

        AdvancementBuilder hidden(boolean v) {
            this.hidden = v;
            rebuildDisplay();
            return this;
        }

        AdvancementBuilder noDisplay() {
            this.withDisplay = false;
            builder.display(null);
            return this;
        }

        Advancement saveWithHelper(Consumer<Advancement> saver, ExistingFileHelper helper) {
            return ((IForgeAdvancementBuilder) builder).save(saver, id, helper);
        }
    }
}