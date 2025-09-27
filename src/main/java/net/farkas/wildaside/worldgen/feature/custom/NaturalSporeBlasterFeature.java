package net.farkas.wildaside.worldgen.feature.custom;

import com.mojang.serialization.Codec;
import net.farkas.wildaside.block.custom.vibrion.NaturalSporeBlaster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.ArrayList;
import java.util.List;

public class NaturalSporeBlasterFeature extends ReplaceBlockFeature {
    public NaturalSporeBlasterFeature(Codec<ReplaceBlockConfiguration> p_66651_) {
        super(p_66651_);
    }

    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> context) {

        WorldGenLevel worldgenlevel = context.level();
        BlockPos blockpos = context.origin();
        ReplaceBlockConfiguration replaceblockconfiguration = context.config();

        for (OreConfiguration.TargetBlockState oreconfiguration$targetblockstate : replaceblockconfiguration.targetStates) {
            if (oreconfiguration$targetblockstate.target.test(worldgenlevel.getBlockState(blockpos), context.random())) {
                Direction.Axis[] axes = { Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z };
                List<Direction.Axis> freeAxes = new ArrayList<>();

                for (Direction.Axis candidate : axes) {
                    int x = candidate.equals(Direction.Axis.X) ? 1 : 0;
                    int y = candidate.equals(Direction.Axis.Y) ? 1 : 0;
                    int z = candidate.equals(Direction.Axis.Z) ? 1 : 0;

                    BlockPos pos1 = blockpos.offset(x, y, z);
                    BlockPos pos2 = blockpos.offset(-x, -y, -z);

                    boolean isFree1 = !worldgenlevel.getBlockState(pos1).isCollisionShapeFullBlock(worldgenlevel, pos1);
                    boolean isFree2 = !worldgenlevel.getBlockState(pos2).isCollisionShapeFullBlock(worldgenlevel, pos2);

                    if (isFree1 || isFree2) {
                        freeAxes.add(candidate);
                    }
                }

                if (freeAxes.isEmpty()) {
                    return false;
                }

                Direction.Axis chosenAxis = freeAxes.get(context.random().nextInt(freeAxes.size()));

                worldgenlevel.setBlock(blockpos, oreconfiguration$targetblockstate.state.setValue(NaturalSporeBlaster.AXIS, chosenAxis), 3);
                worldgenlevel.scheduleTick(blockpos, worldgenlevel.getBlockState(blockpos).getBlock(), 10);
                break;
            }
        }

        return true;
    }
}
