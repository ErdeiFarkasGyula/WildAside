package net.farkas.wildaside.dna.expression;

import java.util.function.BiPredicate;

public enum ActivationCondition {
    ALWAYS((ctx, threshold) -> true),
    HEALTH_ABOVE((ctx, threshold) -> ctx.getHealthPercent() > threshold),
    HEALTH_BELOW((ctx, threshold) -> ctx.getHealthPercent() < threshold),
    ON_FIRE((ctx, threshold) -> ctx.isOnFire()),
    IN_WATER((ctx, threshold) -> ctx.isInWater()),
    IN_NETHER((ctx, threshold) -> ctx.isInNether()),
    IN_END((ctx, threshold) -> ctx.isInEnd()),
    IS_NIGHT((ctx, threshold) -> ctx.isNight()),
    IS_DAY((ctx, threshold) -> ctx.isDay()),
    UNDER_SKY((ctx, threshold) -> ctx.canSeeSky()),
    UNDERGROUND((ctx, threshold) -> !ctx.canSeeSky()),
    IN_COMBAT((ctx, threshold) -> ctx.isInCombat()),
    SPRINTING((ctx, threshold) -> ctx.isSprinting());

    private final BiPredicate<ExpressionContext, Float> predicate;

    ActivationCondition(BiPredicate<ExpressionContext, Float> predicate) {
        this.predicate = predicate;
    }

    public boolean test(ExpressionContext context, float threshold) {
        return predicate.test(context, threshold);
    }
}
