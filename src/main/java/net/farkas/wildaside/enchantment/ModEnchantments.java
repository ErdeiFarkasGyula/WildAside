package net.farkas.wildaside.enchantment;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.enchantment.custom.ExtensiveResearch;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, WildAside.MOD_ID);

    public static final RegistryObject<Enchantment> EXTENSIVE_RESEARCH =
            ENCHANTMENTS.register("extensive_research",
                    () -> new ExtensiveResearch(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND }));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}