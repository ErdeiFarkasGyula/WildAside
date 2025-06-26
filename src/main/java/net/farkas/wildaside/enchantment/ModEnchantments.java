package net.farkas.wildaside.enchantment;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.util.ModTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> EXTENSIVE_RESEARCH = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "extensive_research"));
    public static final ResourceKey<Enchantment> CUSHIONING = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "cushioning"));

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        register(context, CUSHIONING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(5, 10),
                        Enchantment.dynamicCost(20, 10),
                        2,
                        EquipmentSlotGroup.FEET))
                .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE)));

        register(context, EXTENSIVE_RESEARCH, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.SHEARS),
                        5,
                        1,
                        Enchantment.dynamicCost(5, 10),
                        Enchantment.dynamicCost(20, 10),
                        2,
                        EquipmentSlotGroup.MAINHAND)));

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}