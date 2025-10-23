package net.farkas.wildaside.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LargerGroundPathNavigation extends GroundPathNavigation {
    private static float inflate;

    public LargerGroundPathNavigation(Mob mob, Level level, float inflate) {
        super(mob, level);
        this.inflate = inflate;
    }

    @Override
    protected boolean canMoveDirectly(Vec3 from, Vec3 to) {
        Vec3 diff = to.subtract(from);
        AABB pathBox = this.mob.getBoundingBox().inflate(inflate);
        AABB stepBox = pathBox.move(diff.x, diff.y, diff.z);
        return this.level.noCollision(stepBox);
    }
}
