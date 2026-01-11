package net.farkas.wildaside.entity.client.vibrion;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.custom.vibrion.BacillusBlobEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BacillusBlobEntityRenderer extends MobRenderer<BacillusBlobEntity, HumanoidModel<BacillusBlobEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WildAside.MOD_ID, "textures/entity/bacillus_blob.png");

    public BacillusBlobEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HumanoidModel<>(pContext.bakeLayer(ModelLayers.HUSK)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BacillusBlobEntity pEntity) {
        return TEXTURE;
    }
}
