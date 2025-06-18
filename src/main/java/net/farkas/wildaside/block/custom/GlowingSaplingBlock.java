package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Math;
import org.stringtemplate.v4.ST;

public class GlowingSaplingBlock extends SaplingBlock {
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    private static final int minLight = 0;
    private static final int maxLight = 7;
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", minLight, maxLight);
    public static BooleanProperty FIXED_LIGHTING = BooleanProperty.create("fixed_lighting");

    public GlowingSaplingBlock(AbstractTreeGrower pTreeGrower, Properties pProperties) {
        super(pTreeGrower, pProperties.lightLevel(s -> s.getValue(LIGHT)));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STAGE, 0)
                .setValue(LIGHT, 0)
                .setValue(FIXED_LIGHTING, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STAGE, LIGHT, FIXED_LIGHTING);
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
        if (pHand == InteractionHand.OFF_HAND) return InteractionResult.PASS;

        var playerItem = pPlayer.getItemInHand(pHand);

        if (playerItem.getItem().equals(ModItems.VIBRION.get())) {
            if (pState.getValue(GlowingLeavesBlock.FIXED_LIGHTING)) return InteractionResult.PASS;
            pLevel.setBlock(pPos, pState.setValue(GlowingSaplingBlock.FIXED_LIGHTING, true), 3);
            pLevel.playSound(null, pPos, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.honeycomb.wax_on")), SoundSource.BLOCKS, 1, 1);
            pPlayer.swing(pHand);

            if (!pPlayer.isCreative()) {
                playerItem.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        if (pState.getValue(GlowingSaplingBlock.FIXED_LIGHTING)) return;

        int time = (int)pLevel.dayTime();
        int currentLight = pLevel.getBlockState(pPos).getValue(LIGHT);
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
            pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LIGHT, newLight));
        }

        pLevel.scheduleTick(pPos, this, 100);

    }
}
