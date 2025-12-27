package net.farkas.wildaside.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.entity.custom.IncubatorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class IncubatorRenderer implements BlockEntityRenderer<IncubatorBlockEntity> {
    private static final ResourceLocation VIBRION_TEX = new ResourceLocation(WildAside.MOD_ID, "textures/block/vibrion.png");
    private static final RenderType LAYER = RenderType.entityTranslucent(VIBRION_TEX);

    public IncubatorRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(IncubatorBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (!be.hasBlob()) return;

        long time = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        float t = time + partialTicks;

        float maturity = be.getMaturity() / Math.max(0.001f, be.getMaturityRequired());
        maturity = Mth.clamp(maturity, 0f, 1f);

        float cold = be.getColdTicksThreshold() > 0 ? Mth.clamp((float) be.getColdTicks() / be.getColdTicksThreshold(), 0f, 1f) : 0f;
        boolean heated = be.getBurnTime() > 0;

        float baseScale = Mth.lerp(maturity, 0.35f, 0.9f);
        float breathe = 1.0f + 0.05f * Mth.sin(t * 0.2f);
        float scale = baseScale * breathe;

        int healthy = 0x6adfd9;
        int coldTint = 0x6a7a9a;

        float r = Mth.lerp(cold, ((healthy >> 16) & 0xFF) / 255f, ((coldTint >> 16) & 0xFF) / 255f);
        float g = Mth.lerp(cold, ((healthy >> 8) & 0xFF) / 255f, ((coldTint >> 8) & 0xFF) / 255f);
        float b = Mth.lerp(cold, (healthy & 0xFF) / 255f, (coldTint & 0xFF) / 255f);
        float alpha = Mth.lerp(maturity, 0.55f, 0.9f) * (1f - 0.2f * cold);

        if (heated) {
            float heatPulse = 0.02f * Mth.sin(t * 0.5f);
            scale *= (1.0f + heatPulse);
        }

        pose.pushPose();
        BlockPos pos = be.getBlockPos();
        pose.translate(0.5, 1.25, 0.5);
        pose.scale(scale, scale, scale);

        VertexConsumer vc = buffers.getBuffer(LAYER);

        for (int i = 0; i < 3; i++) {
            float phase = t * 0.15f + i * 0.7f;
            float ox = 0.04f * Mth.sin(phase);
            float oy = 0.03f * Mth.cos(phase * 1.2f);
            float oz = 0.04f * Mth.cos(phase);
            pose.pushPose();
            pose.translate(ox, oy, oz);
            float s = 0.9f - i * 0.1f;
            pose.scale(s, s, s);
            drawBillboardQuad(pose, vc, r, g, b, alpha, packedLight, packedOverlay);
            pose.popPose();
        }

        pose.popPose();
    }

    private void drawBillboardQuad(PoseStack pose, VertexConsumer vc, float r, float g, float b, float a, int light, int overlay) {
        PoseStack.Pose p = pose.last();

        float x1 = -0.5f, x2 = 0.5f;
        float y1 = -0.5f, y2 = 0.5f;
        float z = 0f;

        vc.vertex(p.pose(), x1, y1, z).color(r, g, b, a).uv(0f, 1f).overlayCoords(overlay).uv2(light).normal(p.normal(), 0, 0, 1).endVertex();
        vc.vertex(p.pose(), x1, y2, z).color(r, g, b, a).uv(0f, 0f).overlayCoords(overlay).uv2(light).normal(p.normal(), 0, 0, 1).endVertex();
        vc.vertex(p.pose(), x2, y2, z).color(r, g, b, a).uv(1f, 0f).overlayCoords(overlay).uv2(light).normal(p.normal(), 0, 0, 1).endVertex();
        vc.vertex(p.pose(), x2, y1, z).color(r, g, b, a).uv(1f, 1f).overlayCoords(overlay).uv2(light).normal(p.normal(), 0, 0, 1).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(IncubatorBlockEntity be) {
        return true;
    }
}