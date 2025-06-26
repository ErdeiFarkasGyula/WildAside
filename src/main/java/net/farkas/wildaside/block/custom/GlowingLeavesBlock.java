package net.farkas.wildaside.block.custom;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.util.HickoryColour;
import net.farkas.wildaside.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
    private final HickoryColour colour;

    public GlowingLeavesBlock(Properties pProperties, HickoryColour colour) {
        super(pProperties.lightLevel(s -> s.getValue(LIGHT)));
        this.colour = colour;
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
        if (pHand == InteractionHand.OFF_HAND) return InteractionResult.PASS;

        var playerItem = pPlayer.getItemInHand(pHand);

        if (playerItem.getItem().equals(ModItems.VIBRION.get())) {
            pLevel.setBlock(pPos, pState.setValue(GlowingLeavesBlock.FIXED_LIGHTING, true), 3);
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

        if (pState.getValue(GlowingLeavesBlock.FIXED_LIGHTING)) return;

        int time = (int)pLevel.dayTime();
        int currentLight = pState.getValue(LIGHT);
        int newLight = 0;

        if (time > 22000) {
            newLight = Math.round(maxLight - (maxLight * ((time - 22000f) / 2000f)));
        } else
        if (time > 12000 && time < 14000) {
            newLight = Math.round(maxLight * ((time - 12000f) / 2000f));
        } else
        if (time > 14000) {
            newLight = maxLight;
        } else
        if (time < 12000) {
            newLight = minLight;
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
        if (pLevel.getBlockState(pPos.below()).isFaceSturdy(pLevel, pPos.below(), Direction.UP)) return;

        SimpleParticleType particle = ModParticles.HICKORY_PARTICLES.get(colour).get();

        if (pRandom.nextFloat() < 0.025) {
            ParticleUtils.spawnHickoryParticles(pLevel, pPos, pRandom, particle);
        }
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pRandom.nextFloat() < 0.025) {
            spawnNewFallenLeaves(pLevel, pPos, pRandom);
        }
        super.randomTick(pState, pLevel, pPos, pRandom);
    }

    private void spawnNewFallenLeaves(Level level, BlockPos pPos, RandomSource random) {
        if (level.isClientSide) return;

        int x = pPos.getX() + random.nextIntBetweenInclusive(-2, 2);
        int z = pPos.getZ() + random.nextIntBetweenInclusive(-2, 2);


        int groundY = -100;
        for (int y = pPos.getY(); y >= -64; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP)) {
                BlockState aboveState = level.getBlockState(pos.above());
                if (aboveState.isAir() || aboveState.getBlock() instanceof FallenHickoryLeavesBlock) {
                    groundY = y;
                    break;
                }
            }
        }

        if (groundY > -64) {
            BlockPos target = new BlockPos(x, groundY + 1, z);
            BlockState state = level.getBlockState(target);
            Direction direction;
            int count = 1;

            if (state.getBlock() instanceof FallenHickoryLeavesBlock) {
                if (state.getValue(FallenHickoryLeavesBlock.COLOUR) != this.colour) return;
                count = java.lang.Math.min(level.getBlockState(target).getValue(FallenHickoryLeavesBlock.COUNT) + 1, 3);
                direction = level.getBlockState(target).getValue(FallenHickoryLeavesBlock.FACING);
            } else {
                direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            }

            BlockState leafSt = ModBlocks.FALLEN_HICKORY_LEAVES.get()
                    .defaultBlockState()
                    .setValue(FallenHickoryLeavesBlock.COUNT, count)
                    .setValue(FallenHickoryLeavesBlock.COLOUR, this.colour)
                    .setValue(FallenHickoryLeavesBlock.FACING, direction);

            level.setBlock(target, leafSt, 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }
}
