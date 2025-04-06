package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.enchantment.ModEnchantments;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class OvergrownEntoriumOre extends EntoriumOre {
    public OvergrownEntoriumOre(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            var playerItem = pPlayer.getItemInHand(pHand);

            if (playerItem.getItem() == Items.SHEARS) {
                BlockState newBlock = ModBlocks.ENTORIUM_ORE.get().defaultBlockState();

                pLevel.setBlock(pPos, newBlock, 2);
                pLevel.playSound(null, pPos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.mooshroom.shear")), SoundSource.BLOCKS, 1, 1);
                pPlayer.swing(pHand);

                if (!pPlayer.isCreative()) {
                    playerItem.hurt(1, RandomSource.create(), null);
                }

                if (playerItem.getItem().getEnchantmentLevel(playerItem, ModEnchantments.EXTENSIVE_RESEARCH.get()) > 0) {
                    AdvancementHandler.givePlayerAdvancement((ServerPlayer) pPlayer, "extensive_research");
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            for (int k = -1; k <= 1; k++) {
                                BlockPos newPos = pPos.offset(i, j, k);
                                if (pLevel.getBlockState(newPos).is(ModBlocks.OVERGROWN_ENTORIUM_ORE.get())) {
                                    pLevel.setBlock(newPos, newBlock, 2);
                                }
                            }
                        }
                    }
                }

                return InteractionResult.SUCCESS;

            }
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextFloat() < 0.5) {
            pLevel.addParticle(ModParticles.ENTORIUM_PARTICLE.get(), pPos.getX() + pRandom.nextFloat(), pPos.getY() + 1, pPos.getZ() + pRandom.nextFloat(), 0, 0, 0);
            super.animateTick(pState, pLevel, pPos, pRandom);
        }
    }
}
