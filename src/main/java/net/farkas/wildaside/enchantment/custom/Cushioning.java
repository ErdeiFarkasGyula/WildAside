package net.farkas.wildaside.enchantment.custom;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.enchantment.ModEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class Cushioning extends Enchantment {
    public Cushioning(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 5 + 10 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return super.getMinCost(level) + 15;
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        if (pStack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getType().equals(ArmorItem.Type.BOOTS);
        }
        return false;
    }
}

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class CushioningHandler {

    @SubscribeEvent
    public static void onFallDamage(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (!source.is(DamageTypes.FALL)) return;
        if (event.getAmount() <= 0) return;

        if (!(entity instanceof Player player)) return;
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        int level = boots.getEnchantmentLevel(ModEnchantments.CUSHIONING.get());
        if (level <= 0) return;

        int amp = (level >= 3 ? 1 : 0);
        int seconds;
        switch (level) {
            case 1 -> seconds = 5;
            case 2 -> seconds = 7;
            case 3 -> seconds = 10;
            default -> seconds = 5;
        }
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, seconds * 20, amp, true, true));
    }
}
