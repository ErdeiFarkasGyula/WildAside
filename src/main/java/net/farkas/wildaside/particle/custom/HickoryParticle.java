package net.farkas.wildaside.particle.custom;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

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

    private final SpriteSet spriteSet;
    private float angularVelocity;
    private float angularAcceleration;

    protected HickoryParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.1f, 0.1f);
        this.quadSize = (this.random.nextFloat() + 1f) / 4f;
        this.lifetime = 400;
        this.gravity = world.getGameTime() / 64;
        this.hasPhysics = true;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.angularVelocity = (float) -(this.xd + this.zd) / 5;
        this.angularAcceleration = 0;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += this.angularVelocity;
        this.angularVelocity += this.angularAcceleration;
        this.xd *= 1.02f;
        this.zd *= 1.02f;

        if (onGround) {
            this.angularVelocity = 0;
        }
    }
}
