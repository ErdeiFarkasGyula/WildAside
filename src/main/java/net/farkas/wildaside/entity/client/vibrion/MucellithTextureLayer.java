package net.farkas.wildaside.entity.client.vibrion;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.client.AnimatedModelTextureLayer;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class MucellithTextureLayer extends AnimatedModelTextureLayer<MucellithEntity, MucellithModel<MucellithEntity>> {
    private static final ResourceLocation FRAME_0 = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "textures/entity/mucellith_0.png");
    private static final ResourceLocation FRAME_1 = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "textures/entity/mucellith_1.png");

    private static final int FRAME_TIME = 32;
    private static final int TOTAL_FRAMES = 4;
    private static final int[] FRAME_SEQUENCE = {0, 1, 0, 0};

    public MucellithTextureLayer(RenderLayerParent renderer) {
        super(renderer, FRAME_0, FRAME_1, FRAME_TIME, TOTAL_FRAMES, FRAME_SEQUENCE);
    }
}
