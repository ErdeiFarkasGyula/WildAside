package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.entity.custom.vibrion.FertiliserBombEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class FertiliserBomb extends Item {
    public FertiliserBomb(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 200;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player) || level.isClientSide) return;

        int chargeTime = this.getUseDuration(stack, entity) - timeLeft;
        float charge = Mth.clamp((float) chargeTime / 20f, 0f, 1f);

        level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

        FertiliserBombEntity thrown = new FertiliserBombEntity(level, player, charge);
        thrown.setItem(stack);
        thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.8F + charge * 0.8F, 1.0F);
        level.addFreshEntity(thrown);

        if (!player.getAbilities().instabuild) {
            player.getCooldowns().addCooldown(this, 100);
            stack.shrink(1);
        }
    }
}
