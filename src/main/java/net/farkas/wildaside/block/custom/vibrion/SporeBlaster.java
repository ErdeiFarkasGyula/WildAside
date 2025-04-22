package net.farkas.wildaside.block.custom.vibrion;

import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.ContaminationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SporeBlaster extends Block {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public SporeBlaster(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!world.isClientSide) {
            world.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        int power = world.getBestNeighborSignal(pos);
        if (power > 0) {
            infectAlongLine(world, pos, state.getValue(FACING), power, rand);
        }
        world.scheduleTick(pos, this, 2);
    }

    private void infectAlongLine(ServerLevel world, BlockPos origin, Direction dir, int range, RandomSource rand) {
        for (int i = 1; i <= range; i++) {
            BlockPos step = origin.relative(dir, i);
            if (world.getBlockState(step).isCollisionShapeFullBlock(world, step)) break;

            AABB area = new AABB(step);
            List<LivingEntity> hits = world.getEntitiesOfClass(LivingEntity.class, area,e -> !e.isSpectator());

            SimpleParticleType particle = ModParticles.VIBRION_PARTICLE.get();

            for (int j = 0; j < 2; j++) {
                double x = step.getX() + rand.nextDouble();
                double y = step.getY() + rand.nextDouble();
                double z = step.getZ() + rand.nextDouble();
                world.sendParticles(particle, x, y, z,1,
                        0.02 * dir.getStepX(),
                        0.02 * dir.getStepY(),
                        0.02 * dir.getStepZ(),
                        0.0
                );
            }

            for (LivingEntity entity : hits) {
                ContaminationHandler.applyContamination(entity, 20);
                world.sendParticles(particle,
                        entity.getX(), entity.getY() + 0.5, entity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }
}
