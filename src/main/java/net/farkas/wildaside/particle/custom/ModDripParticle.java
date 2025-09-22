package net.farkas.wildaside.particle.custom;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ModDripParticle extends TextureSheetParticle {
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ModDripParticle(level, x, y, z, this.spriteSet);
        }
    }

    private final int hangTime = 60;
    private final int stretchTime = 30;
    private float stretchFactor = 1f;

    protected ModDripParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.01F, 0.01F);
        this.lifetime = hangTime + stretchTime + 60 + this.random.nextInt(20);
        this.gravity = 0.0F;
        this.pickSprite(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age < hangTime) {
            this.yd = 0;
            stretchFactor = 1.0f;
        } else if (this.age < hangTime + stretchTime) {
            float progress = (this.age - hangTime) / (float) stretchTime;
            this.yd = 0;
            stretchFactor = 1.0f + 1.5f * progress;
        } else {
            if (this.gravity == 0.0f) {
                this.gravity = 0.05F;
            }
            this.yd -= this.gravity;
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98F;
        this.yd *= 0.98F;
        this.zd *= 0.98F;

        if (++this.age >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        float originalQuadSize = this.quadSize;
        float stretchedY = originalQuadSize * stretchFactor;

        Vec3 camPos = camera.getPosition();
        float px = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float py = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float pz = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        Quaternionf rotation = new Quaternionf(camera.rotation());
        Vector3f[] quad = new Vector3f[]{
                new Vector3f(-1, -1, 0),
                new Vector3f(-1,  1, 0),
                new Vector3f( 1,  1, 0),
                new Vector3f( 1, -1, 0)
        };

        for (int i = 0; i < 4; ++i) {
            Vector3f vec = quad[i];
            vec.rotate(rotation);
            vec.mul(originalQuadSize, stretchedY, originalQuadSize);
            vec.add(px, py, pz);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        buffer.vertex(quad[0].x(), quad[0].y(), quad[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(quad[1].x(), quad[1].y(), quad[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(quad[2].x(), quad[2].y(), quad[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex(quad[3].x(), quad[3].y(), quad[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }
}