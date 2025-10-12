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

public class HickoryFallingLeafParticle extends TextureSheetParticle {
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HickoryFallingLeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
        }
    }

    private final float phaseOffset;
    private final float driftBaseAmplitude;
    private final float rollBaseAmplitude;

    private float swayTimer = 0f;

    protected HickoryFallingLeafParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.phaseOffset = level.random.nextFloat() * (float)Math.PI * 2;
        this.driftBaseAmplitude = 0.05f + level.random.nextFloat() * 0.05f;
        this.rollBaseAmplitude = 0.5f + level.random.nextFloat() * 0.5f;
        this.setSize(0.2f, 0.2f);
        this.quadSize = (this.random.nextFloat() + 1) / 4f;
        this.lifetime = 800 + this.random.nextInt(400);
        this.gravity = 0.03f + level.random.nextFloat() * 0.02f;
        this.hasPhysics = true;

        this.xd = vx * 0.1;
        this.yd = vy * 0.1 - (0.02 + level.random.nextFloat() * 0.02);
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
        if (this.onGround) {
            this.remove();
            return;
        }

        Vec3 wind = ClientWindData.getWind();
        double windStrength = wind.length();

        this.xd += wind.x * 0.04;
        this.zd += wind.z * 0.04;

        double horizontalSpeed = Math.sqrt(this.xd * this.xd + this.zd * this.zd);
        swayTimer += horizontalSpeed * 2.5f;

        float swayX = Mth.sin(swayTimer + phaseOffset) * driftBaseAmplitude * (0.5f + (float)windStrength * 2f);
        float swayZ = Mth.cos(swayTimer + phaseOffset) * driftBaseAmplitude * (0.5f + (float)windStrength * 2f);
        this.xd += swayX * 0.02;
        this.zd += swayZ * 0.02;

        if (this.random.nextFloat() < 0.002) {
            this.yd += 0.08f + this.random.nextFloat() * 0.05f;
        }
        
        this.xd *= 0.97;
        this.yd *= 0.98;
        this.zd *= 0.97;

        super.tick();
    }
}