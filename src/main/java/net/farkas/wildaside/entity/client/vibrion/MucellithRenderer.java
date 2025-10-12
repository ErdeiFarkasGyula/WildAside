package net.farkas.wildaside.entity.client.vibrion;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.client.ModModelLayers;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MucellithRenderer extends MobRenderer<MucellithEntity, MucellithModel<MucellithEntity>> {
    public MucellithRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MucellithModel<>(pContext.bakeLayer(ModModelLayers.MUCELLITH_LAYER)), 0.5f);
        this.addLayer(new MucellithTextureLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MucellithEntity pEntity) {
        return new ResourceLocation(WildAside.MOD_ID, "textures/entity/mucellith.png");
    }
}
