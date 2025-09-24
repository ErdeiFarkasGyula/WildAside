package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.custom.FallenHickoryLeavesBlock;
import net.farkas.wildaside.item.FuelItem;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HickoryLeafItem extends FuelItem {
    private final HickoryColour colour;

    public HickoryLeafItem(Properties pProperties, int burnTime, HickoryColour colour) {
        super(pProperties, burnTime);
        this.colour = colour;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (level.isClientSide() || pContext.getHand() == InteractionHand.OFF_HAND) return InteractionResult.PASS;

        BlockPos pos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        InteractionHand hand = pContext.getHand();
        BlockState blockState = level.getBlockState(pos);

        if (blockState.getBlock() instanceof FallenHickoryLeavesBlock) {
            return addLeaf(level, pos, player, hand);
        }
        else if (blockState.isFaceSturdy(level, pos, Direction.UP)) {
            return placeLeaf(level, pos, player, hand);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult placeLeaf(Level level, BlockPos pos, Player player, InteractionHand hand) {
        BlockPos abovePos = pos.above();
        if (!level.isEmptyBlock(abovePos)) {
            if (level.getBlockState(abovePos).getBlock() instanceof FallenHickoryLeavesBlock) {
                return addLeaf(level, abovePos, player, hand);
            }
            return InteractionResult.PASS;
        }

        BlockState newState = ModBlocks.FALLEN_HICKORY_LEAVES.get().defaultBlockState()
                .setValue(FallenHickoryLeavesBlock.COLOUR, this.colour)
                .setValue(FallenHickoryLeavesBlock.FACING, player.getDirection().getOpposite());
        level.setBlock(abovePos, newState, 3);
        onSuccessfulPlacement(level, abovePos, player, hand);

        return InteractionResult.SUCCESS;
    }


    private InteractionResult addLeaf(Level level, BlockPos pos, Player player, InteractionHand hand) {
        BlockState leaves = level.getBlockState(pos);

        if (this.colour != leaves.getValue(FallenHickoryLeavesBlock.COLOUR)) return InteractionResult.PASS;

        int count = leaves.getValue(FallenHickoryLeavesBlock.COUNT);
        if (count >= FallenHickoryLeavesBlock.MAX_COUNT) return InteractionResult.PASS;

        BlockState newState = leaves.setValue(FallenHickoryLeavesBlock.COUNT, count + 1);
        level.setBlock(pos, newState, 3);
        onSuccessfulPlacement(level, pos, player, hand);

        return InteractionResult.SUCCESS;
    }

    private void onSuccessfulPlacement(Level level, BlockPos pos, Player player, InteractionHand hand) {
        var item = player.getItemInHand(hand);

        level.playSound(null, pos, SoundEvents.BIG_DRIPLEAF_PLACE, SoundSource.BLOCKS, 1, 1.1f);
        if (!player.isInvulnerable()) {
            item.shrink(1);
        }
    }

    public HickoryColour getColour() {
        return this.colour;
    }
}
