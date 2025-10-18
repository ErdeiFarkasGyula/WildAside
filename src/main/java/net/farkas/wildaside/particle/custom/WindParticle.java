package net.farkas.wildaside.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class WindParticle extends TextureSheetParticle {
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new StillSubstiliumParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final Vec3 direction;

    protected WindParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.setSize(2, 2);
        this.direction = new Vec3(vx, vz, vy).scale(100);
        this.lifetime = 100 + level.random.nextInt(50);
        this.gravity = 0.0F;
        this.quadSize = 2 + random.nextFloat() * 0.05F;
        float tint = 0.8F + random.nextFloat() * 0.2F;
        setColor(0.8F * tint, 0.7F * tint, 0.6F * tint);
        setAlpha(0.0F);
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        double ageFactor = age / (double) lifetime;
        double wobble = Math.sin(age * 0.2) * 0.01;
        xd = direction.x + wobble;
        yd = direction.y + wobble * 0.5;
        zd = direction.z;

        float fade = (float) (Math.sin(Math.PI * ageFactor));
        setAlpha(0.1f * fade);

        if (age++ >= lifetime) remove();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
