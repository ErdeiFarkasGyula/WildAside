package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class RootBushBlock extends SweetBerryBushBlock {
    private static final VoxelShape SAPLING_SHAPE = Block.box(2, 0.0D, 2, 14, 10, 14);

    public RootBushBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (pEntity instanceof LivingEntity && pEntity.getType() != EntityType.FOX && pEntity.getType() != EntityType.BEE && !(pEntity instanceof HickoryTreantEntity)) {
            pEntity.makeStuckInBlock(pState, new Vec3(0.8F, 0.75D, 0.8F));
            if (!pLevel.isClientSide && pState.getValue(AGE) > 0 && (pEntity.xOld != pEntity.getX() || pEntity.zOld != pEntity.getZ())) {
                double d0 = Math.abs(pEntity.getX() - pEntity.xOld);
                double d1 = Math.abs(pEntity.getZ() - pEntity.zOld);
                if (d0 >= (double)0.003F || d1 >= (double)0.003F) {
                    pEntity.hurt(pLevel.damageSources().sweetBerryBush(), 1.0F);
                }
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(ModBlocks.HICKORY_ROOT_BUSH.get());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        return List.of();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SAPLING_SHAPE;
    }
}
