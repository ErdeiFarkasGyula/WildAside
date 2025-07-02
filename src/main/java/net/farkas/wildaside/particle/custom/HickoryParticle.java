package net.farkas.wildaside.particle.custom;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

public class HickoryParticle extends TextureSheetParticle {
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HickoryParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private double initialX;
    private double initialZ;
    private float phaseOffset;
    private float driftAmplitudeX;
    private float driftAmplitudeZ;
    private float rollAmplitude;

    protected HickoryParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.initialX = x;
        this.initialZ = z;
        this.phaseOffset = world.random.nextFloat() * (float)Math.PI * 2;
        this.driftAmplitudeX = 0.1f + world.random.nextFloat() * 0.1f;
        this.driftAmplitudeZ = 0.1f + world.random.nextFloat() * 0.1f;
        this.rollAmplitude = 0.5f + world.random.nextFloat() * 0.5f;
        this.setSize(0.2f, 0.2f);
        this.quadSize = (this.random.nextFloat() + 1) / 4f;
        this.lifetime = 1024;
        this.gravity = 0.01f + world.random.nextFloat() * 0.01f;
        this.hasPhysics = true;
        this.xd = vx * 0.1;
        this.yd = vy * 0.1 - (0.01 + world.random.nextFloat() * 0.02);
        this.zd = vz * 0.1;
        this.roll = world.random.nextFloat() * (float)Math.PI * 2;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.removed) return;

        if (this.onGround) {
            this.remove();
            return;
        }

        float ageFactor = (this.age + this.phaseOffset) * 0.1f;
        float swayX = Mth.sin(ageFactor) * driftAmplitudeX;
        float swayZ = Mth.cos(ageFactor) * driftAmplitudeZ;
        this.x = initialX + swayX;
        this.z = initialZ + swayZ;

        this.oRoll = this.roll;
        this.roll = (float) (Mth.sin(ageFactor * 0.5f) * rollAmplitude - (Math.PI / 8));
    }
}
