package net.farkas.wildaside.entity.client.hickory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HickoryTreantRenderer extends MobRenderer<HickoryTreantEntity, HumanoidModel<HickoryTreantEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "textures/entity/hickory_treant.png");

    public HickoryTreantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HumanoidModel<>(pContext.bakeLayer(ModelLayers.HUSK)), 1.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(HickoryTreantEntity pEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(HickoryTreantEntity entity, PoseStack poseStack, float partialTick) {
        float s = 2.0f;
        poseStack.scale(s, s, s);
    }
}
