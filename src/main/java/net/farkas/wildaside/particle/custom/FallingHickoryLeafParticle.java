package net.farkas.wildaside.particle.custom;

import net.farkas.wildaside.client.ClientWindData;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FallingHickoryLeafParticle extends TextureSheetParticle {
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new FallingHickoryLeafParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final float phaseOffset;
    private final float driftAmplitudeX;
    private final float driftAmplitudeZ;
    private final float rollAmplitude;

    protected FallingHickoryLeafParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.phaseOffset = level.random.nextFloat() * (float)Math.PI * 2;
        this.driftAmplitudeX = 0.1f + level.random.nextFloat() * 0.1f;
        this.driftAmplitudeZ = 0.1f + level.random.nextFloat() * 0.1f;
        this.rollAmplitude = 0.5f + level.random.nextFloat() * 0.5f;
        this.setSize(0.2f, 0.2f);
        this.quadSize = (this.random.nextFloat() + 1) / 4f;
        this.lifetime = 1024;
        this.gravity = 0.05f + level.random.nextFloat() * 0.02f;
        this.hasPhysics = true;

        this.xd = vx * 0.1;
        this.yd = vy * 0.1 - (0.01 + level.random.nextFloat() * 0.02);
        this.zd = vz * 0.1;

        this.roll = level.random.nextFloat() * (float)Math.PI * 2;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.removed) return;

        Vec3 wind = ClientWindData.getWind();

        double windInfluence = 0.035;
        this.xd += wind.x * windInfluence;
        this.yd += wind.y * windInfluence;
        this.zd += wind.z * windInfluence;

        float ageFactor = (this.age + this.phaseOffset) * 0.15f;
        this.xd += Mth.sin(ageFactor) * driftAmplitudeX * 0.01;
        this.zd += Mth.cos(ageFactor) * driftAmplitudeZ * 0.01;

        this.xd *= 0.98;
        this.yd *= 0.98;
        this.zd *= 0.98;

        super.tick();

        this.oRoll = this.roll;
        this.roll = (float)(Mth.sin(ageFactor * 0.5f) * rollAmplitude + (Math.PI / 6));

        if (this.onGround) this.remove();
    }
}