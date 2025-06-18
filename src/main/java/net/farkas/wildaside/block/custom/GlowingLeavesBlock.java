package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Math;

public class GlowingLeavesBlock extends LeavesBlock {
    private static final int minLight = 0;
    private static final int maxLight = 7;
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", minLight, maxLight);
    public static BooleanProperty FIXED_LIGHTING = BooleanProperty.create("fixed_lighting");

    private SimpleParticleType particle;
    private boolean particleChanged = false;

    public GlowingLeavesBlock(Properties pProperties) {
        super(pProperties.lightLevel(s -> s.getValue(LIGHT)));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, 7)
                .setValue(PERSISTENT, false)
                .setValue(WATERLOGGED, false)
                .setValue(LIGHT, 0)
                .setValue(FIXED_LIGHTING, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DISTANCE, PERSISTENT, WATERLOGGED, LIGHT, FIXED_LIGHTING);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 0);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) return InteractionResult.PASS;
        if (pState.getValue(GlowingLeavesBlock.FIXED_LIGHTING)) return InteractionResult.PASS;
        var playerItem = pPlayer.getItemInHand(pHand);

        if (playerItem.getItem().equals(ModItems.VIBRION.get())) {
            pLevel.setBlock(pPos, pState.setValue(GlowingLeavesBlock.FIXED_LIGHTING, true), 3);
            pLevel.playSound(null, pPos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.honeycomb.wax_on")), SoundSource.BLOCKS, 1, 1);
            pPlayer.swing(pHand);

            if (!pPlayer.isCreative()) {
                playerItem.hurt(1, RandomSource.create(), null);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        if (pState.getValue(GlowingLeavesBlock.FIXED_LIGHTING)) return;

        int time = (int)pLevel.dayTime();
        int currentLight = pState.getValue(LIGHT);
        int newLight = 0;

        if (time > 22000) {
            newLight = Math.round(7 - (maxLight * ((time - 22000f) / 2000f)));
        } else
        if (time > 12000 && time < 14000) {
            newLight = Math.round(maxLight * ((time - 12000f) / 2000f));
        } else
        if (time > 14000) {
            newLight = 7;
        } else
        if (time < 12000) {
            newLight = 0;
        }

        newLight = Math.min(Math.max(0, newLight), 7);

        if (newLight != currentLight) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(LIGHT, newLight));
        }

        pLevel.scheduleTick(pPos, this, 100);

    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (!pLevel.getBlockState(pPos.below()).isAir()) return;

        if (!particleChanged) {
            if (pState.is(ModBlocks.RED_GLOWING_HICKORY_LEAVES.get())) {
                particle = ModParticles.RED_GLOWING_HICKORY_PARTICLE.get();
            } else
                if (pState.is(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES.get())) {
                    particle = ModParticles.BROWN_GLOWING_HICKORY_PARTICLE.get();
                } else
                    if (pState.is(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES.get())) {
                        particle = ModParticles.YELLOW_GLOWING_HICKORY_PARTICLE.get();
                    } else {
                        particle = ModParticles.GREEN_GLOWING_HICKORY_PARTICLE.get();
                    }
            particleChanged = true;
        }

        if (pRandom.nextFloat() < 0.02f) {
            ParticleUtils.spawnHickoryParticles(pLevel, pPos, pRandom, particle);
        }
    }
}
