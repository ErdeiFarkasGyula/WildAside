package net.farkas.wildaside.worldgen.feature.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.custom.FallenHickoryLeavesBlock;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class FallenLeavesDecorator extends TreeDecorator {
    private final float chance;
    private final HickoryColour colour;

    public static final Codec<FallenLeavesDecorator> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.FLOAT.fieldOf("chance").forGetter(d -> d.chance),
                    Codec.STRING
                            .xmap(HickoryColour::valueOf, HickoryColour::name)
                            .fieldOf("colour")
                            .forGetter(d -> d.colour)
            ).apply(inst, FallenLeavesDecorator::new)
    );

    public FallenLeavesDecorator(float chance, HickoryColour colour) {
        this.chance = chance;
        this.colour = colour;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecorators.FALLEN_LEAVES.get();
    }

    @Override
    public void place(Context pContext) {
        RandomSource random = pContext.random();

        var logs = pContext.logs();
        BlockPos anyLog = logs.get(0);
        int cx = anyLog.getX();
        int cz = anyLog.getZ();

        LevelSimulatedReader reader = pContext.level();

        int radius = 6;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (random.nextFloat() < chance) {
                    int x = cx + dx;
                    int z = cz + dz;

                    int startY = logs.stream().mapToInt(BlockPos::getY).min().orElse(0) + 5;

                    int groundY = -100;
                    for (int y = startY; y >= -64; y--) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if (reader.isStateAtPosition(pos, bs -> bs.is(BlockTags.DIRT))) {
                            if (reader.isStateAtPosition(pos.above(), bs -> !bs.isSolid())) {
                                groundY = y;
                                break;
                            }
                            break;
                        }
                    }

                    if (groundY > -64) {
                        BlockPos target = new BlockPos(x, groundY + 1, z);

                        int count = 1 + random.nextInt(3);
                        HickoryColour colour = this.colour;
                        Direction face = Direction.Plane.HORIZONTAL.getRandomDirection(random);

                        BlockState leafSt = ModBlocks.FALLEN_HICKORY_LEAVES.get()
                                .defaultBlockState()
                                .setValue(FallenHickoryLeavesBlock.COUNT, count)
                                .setValue(FallenHickoryLeavesBlock.COLOUR, colour)
                                .setValue(FallenHickoryLeavesBlock.FACING, face);

                        pContext.setBlock(target, leafSt);
                    }
                }

            }
        }
    }
}
