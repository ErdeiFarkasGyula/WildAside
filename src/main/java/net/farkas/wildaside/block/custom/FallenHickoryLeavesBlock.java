package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.HickoryLeafItem;
import net.farkas.wildaside.util.GlowingHickoryLightUtil;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;

public class FallenHickoryLeavesBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 0.5, 15);
    public static final int MAX_COUNT = 3;
    public static final EnumProperty<HickoryColour> COLOUR = EnumProperty.create("colour", HickoryColour.class);
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, MAX_COUNT);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final int MIN_LIGHT = 0;
    private static final int MAX_LIGHT = 3;
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", MIN_LIGHT, MAX_LIGHT);
    public static final BooleanProperty FIXED_LIGHTING = BooleanProperty.create("fixed_lighting");

    public FallenHickoryLeavesBlock(Properties pProperties) {
        super(pProperties.lightLevel(s -> s.getValue(LIGHT)));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(COLOUR, HickoryColour.HICKORY)
                .setValue(COUNT, 1)
                .setValue(FACING, Direction.NORTH)
                .setValue(LIGHT, 0)
                .setValue(FIXED_LIGHTING, false)
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        HickoryColour colour = pState.getValue(FallenHickoryLeavesBlock.COLOUR);
        int count = pState.getValue(FallenHickoryLeavesBlock.COUNT);
        return Collections.singletonList(new ItemStack(ModItems.LEAF_ITEMS.get(colour).get(), count));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOUR, COUNT, FACING, LIGHT, FIXED_LIGHTING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        HickoryColour color = HickoryColour.HICKORY;
        if (stack.getItem() instanceof HickoryLeafItem leaf) {
            color = leaf.getColour();
        }
        Direction face = ctx.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(COLOUR, color)
                .setValue(COUNT, 1)
                .setValue(FACING, face);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide || pHand == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        ItemStack playerItem = pPlayer.getItemInHand(pHand);

        if (playerItem.isEmpty()) {
            int count = pState.getValue(FallenHickoryLeavesBlock.COUNT);
            if (count == 1) {
                pLevel.removeBlock(pPos, false);
            } else {
                pLevel.setBlock(pPos, pState.setValue(FallenHickoryLeavesBlock.COUNT, count - 1), 3);
            }

            pPlayer.swing(pHand);
            pPlayer.addItem(new ItemStack(ModItems.LEAF_ITEMS.get(pState.getValue(FallenHickoryLeavesBlock.COLOUR)).get()));
            pLevel.playSound(null, pPos, SoundEvents.BIG_DRIPLEAF_PLACE, SoundSource.BLOCKS, 1, 0.8f);
            if (!pPlayer.isInvulnerable()) {
                playerItem.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }
        else
        if (playerItem.getItem().equals(ModItems.VIBRION.get())) {
            if (pState.getValue(GlowingHickoryLeavesBlock.FIXED_LIGHTING)) return InteractionResult.PASS;
            pLevel.setBlock(pPos, pState.setValue(FallenHickoryLeavesBlock.FIXED_LIGHTING, true), 3);
            pLevel.playSound(null, pPos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1, 1);

            if (!pPlayer.isInvulnerable()) {
                playerItem.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }


        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            pLevel.scheduleTick(pPos, this, 0);
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);

        if (pState.getValue(FallenHickoryLeavesBlock.FIXED_LIGHTING) || pState.getValue(FallenHickoryLeavesBlock.COLOUR) == HickoryColour.HICKORY) return;
        int maxLight = pState.getValue(COUNT);

        int time = (int)pLevel.dayTime();
        int currentLight = pState.getValue(LIGHT);
        int newLight = GlowingHickoryLightUtil.getLight(time, MIN_LIGHT, maxLight);

        if (newLight != currentLight) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(LIGHT, newLight));
        }

        pLevel.scheduleTick(pPos, this, 100);

    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(ModItems.LEAF_ITEMS.get(state.getValue(FallenHickoryLeavesBlock.COLOUR)).get());
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).isFaceSturdy(pLevel, pPos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState st, Direction side, BlockState nb, LevelAccessor w, BlockPos p, BlockPos np) {
        if (side == Direction.DOWN && !this.canSurvive(st, w, p)) {
            w.destroyBlock(p, true);
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(st, side, nb, w, p, np);
    }
}