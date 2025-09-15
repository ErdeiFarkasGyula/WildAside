package net.farkas.wildaside.entity.client.vibrion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ContaminatedCreeperRenderer extends MobRenderer<ContaminatedCreeperEntity, HumanoidModel<ContaminatedCreeperEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WildAside.MOD_ID, "textures/entity/hickory_treant.png");

    public ContaminatedCreeperRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HumanoidModel<>(pContext.bakeLayer(ModelLayers.HUSK)), 1.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(ContaminatedCreeperEntity pEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(ContaminatedCreeperEntity entity, PoseStack poseStack, float partialTick) {
        float s = 1.0f;
        poseStack.scale(s, s, s);
    }
}
