package net.farkas.wildaside.entity.custom.vibrion;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.util.AdvancementHandler;
import net.farkas.wildaside.util.WeightedFlowerChoice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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

    final List<WeightedFlowerChoice.WeightedFlower> flowerList = new ArrayList<>(List.of(
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
    final WeightedFlowerChoice flowerChoice = new WeightedFlowerChoice(flowerList);

    public FertiliserBombEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.charge = level().random.nextFloat();
        thrower = null;
    }

    public FertiliserBombEntity(Level pLevel, LivingEntity thrower, float charge) {
        super(ModEntities.FERTILISER_BOMB.get(), thrower, pLevel);
        this.charge = charge + 0.1f;
        this.thrower = thrower;
    }

    public FertiliserBombEntity(Level pLevel, double pX, double pY, double pZ) {
        super(ModEntities.FERTILISER_BOMB.get(), pX, pY, pZ, pLevel);
        this.charge = level().random.nextFloat();
        this.thrower = null;
    }

    public FertiliserBombEntity(Level pLevel) {
        super(ModEntities.FERTILISER_BOMB.get(), pLevel);
        this.charge = level().random.nextFloat();
        this.thrower = null;
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
        int verticalRange = 1;
        int radiusSq = radius * radius;
        RandomSource random = level.getRandom();

        Holder<Biome> biomeHolder = level.getBiome(center);
        String biomePath = biomeHolder.unwrapKey().map(key -> key.location().getPath()).orElse("");

        if (biomePath.contains("hickory")) {
            AdvancementHandler.givePlayerAdvancement(thrower, "fertile_forest");
            flowerList.add(new WeightedFlowerChoice.WeightedFlower(4, ModBlocks.SPOTTED_WINTERGREEN.get()));
            flowerList.add(new WeightedFlowerChoice.WeightedFlower(4, ModBlocks.PINKSTER_FLOWER.get()));
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radiusSq) continue;
                for (int dy = -verticalRange; dy <= verticalRange; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    applyEffectAt(level, pos, random, charge);
                }
            }
        }
    }

    private void applyEffectAt(ServerLevel level, BlockPos pos, RandomSource random, float charge) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof FarmBlock farm && chance(random, charge * 0.5f)) {
            int moisture = state.getValue(FarmBlock.MOISTURE);
            if (moisture < 7) {
                level.setBlock(pos, state.setValue(FarmBlock.MOISTURE, 7), 3);
                spawnSplash(level, pos);
            }
            return;
        }

        if (state.isAir() && level.getBlockState(pos.below()).is(BlockTags.DIRT) && chance(random, charge * 0.3f)) {
            BlockState flower = flowerChoice.selectFlower(random);
            level.setBlock(pos, flower, 3);
            spawnHappy(level, pos);
            return;
        }

        if (state.getBlock() instanceof BonemealableBlock bonemealBlock && bonemealBlock.isValidBonemealTarget(level, pos, state, false) && chance(random, charge * 0.3f)) {
            bonemealBlock.performBonemeal(level, random, pos, state);
            level.levelEvent(2005, pos, 0);
            spawnSplash(level, pos);
            return;
        }

        if (state.isAir()) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.getBlock() instanceof BonemealableBlock bonemealBlockBelow && bonemealBlockBelow.isValidBonemealTarget(level, below, belowState, false) && chance(random, charge * 0.2f)) {
                bonemealBlockBelow.performBonemeal(level, random, below, belowState);
                level.levelEvent(2005, below, 0);
                spawnSplash(level, pos);
            }
        }
    }

    private boolean chance(RandomSource random, float factor) {
        return random.nextFloat() < factor;
    }

    private void spawnSplash(Level level, BlockPos pos) {
        level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                5, 0.2, 0.2);
    }

    private void spawnHappy(Level level, BlockPos pos) {
        level.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
                10, 0.2, 0.2);
    }
}
