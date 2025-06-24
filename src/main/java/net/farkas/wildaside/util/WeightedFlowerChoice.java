package net.farkas.wildaside.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class WeightedFlowerChoice {
    public static class WeightedFlower {
        public final int weight;
        public final BlockState state;

        public WeightedFlower(int weight, Block block) {
            this.weight = weight;
            this.state = block.defaultBlockState();
        }
    }

    private final List<WeightedFlower> weightedFlowers;

    public WeightedFlowerChoice(List<WeightedFlower> weightedFlowers) {
        this.weightedFlowers = weightedFlowers;
    }

    public BlockState selectFlower(RandomSource random) {
        int totalWeight = 0;
        for (WeightedFlower wf : weightedFlowers) {
            totalWeight += wf.weight;
        }
        int choice = random.nextInt(totalWeight);
        for (WeightedFlower wf : weightedFlowers) {
            choice -= wf.weight;
            if (choice < 0) {
                return wf.state;
            }
        }
        return weightedFlowers.get(0).state;
    }
}
