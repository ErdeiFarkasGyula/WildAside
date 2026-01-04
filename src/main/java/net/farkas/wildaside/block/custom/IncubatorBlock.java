package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.entity.custom.IncubatorBlockEntity;
import net.farkas.wildaside.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class IncubatorBlock extends BaseEntityBlock {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public IncubatorBlock(Properties props) {
        super(props);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                        .setValue(OPEN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, OPEN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                ? new IncubatorBlockEntity(pos, state)
                : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER) return null;
        return level.isClientSide()
                ? (lvl, pos, st, be) -> {
            if (be instanceof IncubatorBlockEntity inc) inc.tickClient();
        }
                : (lvl, pos, st, be) -> {
            if (be instanceof IncubatorBlockEntity inc) inc.tickServer();
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(ctx)) {
            return this.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite())
                    .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                    .setValue(OPEN, false);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setValue(OPEN, Boolean.FALSE), 3);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            if (player.getItemInHand(hand).is(ModItems.SYRINGE.get())) {
                return InteractionResult.PASS;
            }

            return InteractionResult.SUCCESS;
        }

        if (player.getItemInHand(hand).is(ModItems.SYRINGE.get())) return InteractionResult.PASS;

        BlockPos basePos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        if (level.getBlockEntity(basePos) instanceof IncubatorBlockEntity blockEntity) {
            InteractionResult res = blockEntity.handleUse(player, hand);
            if (res.consumesAction()) return res;

            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, basePos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (state.getBlock() != newState.getBlock()) {
            DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
            BlockPos other = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(other);
            if (otherState.getBlock() == this) {
                level.setBlock(other, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 35);
            }
            if (half == DoubleBlockHalf.LOWER) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IncubatorBlockEntity inc) {
                    inc.dropContents();
                }
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }
}