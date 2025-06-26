package net.farkas.wildaside.entity.custom.vibrion;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.util.AdvancementHandler;
import net.farkas.wildaside.util.WeightedFlowerChoice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class FertiliserBombEntity extends ThrowableItemProjectile {
    private final float charge;
    private final LivingEntity thrower;

    List<WeightedFlowerChoice.WeightedFlower> flowerList = new ArrayList<>(List.of(
            new WeightedFlowerChoice.WeightedFlower(5, Blocks.DANDELION),
            new WeightedFlowerChoice.WeightedFlower(5, Blocks.POPPY),
            new WeightedFlowerChoice.WeightedFlower(3, Blocks.BLUE_ORCHID),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.ALLIUM),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.ORANGE_TULIP),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.PINK_TULIP),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.RED_TULIP),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.WHITE_TULIP),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.OXEYE_DAISY),
            new WeightedFlowerChoice.WeightedFlower(2, Blocks.CORNFLOWER),
            new WeightedFlowerChoice.WeightedFlower(1, Blocks.AZURE_BLUET)
    ));
    WeightedFlowerChoice flowerChoice = new WeightedFlowerChoice(flowerList);

    public FertiliserBombEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.charge = 0;
        thrower = null;
    }

    public FertiliserBombEntity(Level pLevel, LivingEntity thrower, float charge) {
        super(ModEntities.FERTILISER_BOMB.get(), thrower, pLevel);
        this.charge = charge + 0.1f;
        this.thrower = thrower;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FERTILISER_BOMB.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && result.getType() == HitResult.Type.BLOCK) {
            var loc = result.getLocation();
            BlockPos impactPos = new BlockPos((int) loc.x, (int) loc.y, (int) loc.z);
            applyAreaEffect((ServerLevel) level(), impactPos, charge);
            this.discard();
        }
    }

    private void applyAreaEffect(ServerLevel level, BlockPos center, float charge) {
        int radius = Mth.ceil(2 + charge * 2);
        int radiusSq = radius * radius;
        int verticalRange = 1;
        RandomSource random = level.getRandom();

        Holder<Biome> biomeHolder = level.getBiome(center);
        String biomePath = biomeHolder.unwrapKey()
                .map((ResourceKey<Biome> key) -> key.location().getPath())
                .orElse("");
        if (biomePath.contains("hickory")) {
            AdvancementHandler.givePlayerAdvancement((ServerPlayer)thrower, "fertile_forest");
            flowerList.add(new WeightedFlowerChoice.WeightedFlower(4, ModBlocks.SPOTTED_WINTERGREEN.get()));
            flowerList.add(new WeightedFlowerChoice.WeightedFlower(4, ModBlocks.PINKSTER_FLOWER.get()));
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radiusSq) continue;
                for (int dy = -verticalRange; dy <= verticalRange; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);

                    if (state.getBlock() instanceof FarmBlock) {
                        if (random.nextFloat() < charge * 0.5f) {
                            int currentMoisture = state.getValue(FarmBlock.MOISTURE);
                            if (currentMoisture < 7) {
                                BlockState hydratedState = state.setValue(FarmBlock.MOISTURE, 7);
                                level.setBlock(pos, hydratedState, 3);
                                level.sendParticles(ParticleTypes.SPLASH,
                                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                        5, 0.2, 0.2, 0.2, 0.05);
                            }
                            continue;
                        }
                    }

                    if (state.isAir() && level.getBlockState(pos.below()).is(BlockTags.DIRT)) {
                        if (random.nextFloat() < charge * 0.3f) {
                            BlockState flower = flowerChoice.selectFlower(random);
                            level.setBlock(pos, flower, 3);
                            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                    pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
                                    10, 0.2, 0.2, 0.2, 0.05);
                            continue;
                        }
                    }

                    if (state.getBlock() instanceof BonemealableBlock bonemealBlock) {
                        if (bonemealBlock.isValidBonemealTarget(level, pos, state)) {
                            if (random.nextFloat() < charge * 0.3f) {
                                bonemealBlock.performBonemeal(level, random, pos, state);
                                level.levelEvent(2005, pos, 0);
                                continue;
                            }
                        }
                    } else if (state.isAir()) {
                        BlockPos belowPos = pos.below();
                        BlockState belowState = level.getBlockState(belowPos);
                        if (belowState.getBlock() instanceof BonemealableBlock bonemealBlockBelow &&
                                bonemealBlockBelow.isValidBonemealTarget(level, belowPos, belowState)) {
                            if (random.nextFloat() < charge * 0.2f) {
                                bonemealBlockBelow.performBonemeal(level, random, belowPos, belowState);
                                level.levelEvent(2005, belowPos, 0);
                            }
                        }
                    }
                }
            }
        }
    }
}
