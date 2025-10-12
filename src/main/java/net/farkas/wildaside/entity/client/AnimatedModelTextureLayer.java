package net.farkas.wildaside.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class AnimatedModelTextureLayer<T extends LivingEntity, M extends HierarchicalModel<T>> extends RenderLayer<T, M> {
    public ResourceLocation frame_0;
    public ResourceLocation frame_1;

    public int frameTime;
    public int totalFrames;

    public int[] sequence;

    public AnimatedModelTextureLayer(RenderLayerParent<T, HierarchicalModel<T>> renderer, ResourceLocation frame_0, ResourceLocation frame_1, int frameTime, int totalFrames, int[] sequence) {
        super((RenderLayerParent<T, M>) renderer);
        this.frame_0 = frame_0;
        this.frame_1 = frame_1;
        this.frameTime = frameTime;
        this.totalFrames = totalFrames;
        this.sequence = sequence;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int totalTicks = (int) (entity.tickCount + partialTicks);
        int frameIndex = (totalTicks / frameTime) % totalFrames;
        int currentFrame = sequence[frameIndex];
        int nextFrame = sequence[(frameIndex + 1) % sequence.length];

        float interp = (float) (totalTicks % frameTime) / frameTime;
        float alpha = Mth.clamp(interp, 0f, 1f);

        ResourceLocation texA = currentFrame == 0 ? frame_0 : frame_1;
        ResourceLocation texB = nextFrame == 0 ? frame_0 : frame_1;


        VertexConsumer baseBuffer = bufferSource.getBuffer(RenderType.entityTranslucent(texA));
        getParentModel().renderToBuffer(poseStack, baseBuffer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        if (texB != texA) {
            VertexConsumer overlay = bufferSource.getBuffer(RenderType.entityTranslucent(texB));
            getParentModel().renderToBuffer(poseStack, overlay, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, alpha);
        }
    }
}
