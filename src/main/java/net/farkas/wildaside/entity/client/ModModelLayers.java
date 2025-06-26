package net.farkas.wildaside.entity.client;

import net.farkas.wildaside.WildAside;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation SUBSTILIUM_BOAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "boat/substilium"), "main");
    public static final ModelLayerLocation SUBSTILIUM_CHEST_BOAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "chest_boat/substilium"), "main");
    public static final ModelLayerLocation HICKORY_BOAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "boat/hickory"), "main");
    public static final ModelLayerLocation HICKORY_CHEST_BOAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "chest_boat/hickory"), "main");

    public static final ModelLayerLocation MUCELLITH_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "mucellith"), "main");
}
