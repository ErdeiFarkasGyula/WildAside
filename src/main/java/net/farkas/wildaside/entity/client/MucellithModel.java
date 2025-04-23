package net.farkas.wildaside.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class MucellithModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart mucellith;
	private final ModelPart base;
	private final ModelPart body;
	private final ModelPart bottom;
	private final ModelPart bottomLeaves;
	private final ModelPart lowerMid;
	private final ModelPart lowerMidLeaves;
	private final ModelPart higherMid;
	private final ModelPart higherMidLeaves;
	private final ModelPart top;
	private final ModelPart topLeaves;
	private final ModelPart biggerHead;
	private final ModelPart headLeaves;
	private final ModelPart head;
	private final ModelPart mouth_base;
	private final ModelPart mouth_bottom;
	private final ModelPart mouth_top;

	public MucellithModel(ModelPart root) {
		this.mucellith = root.getChild("mucellith");
		this.base = this.mucellith.getChild("base");
		this.body = this.mucellith.getChild("body");
		this.bottom = this.body.getChild("bottom");
		this.bottomLeaves = this.bottom.getChild("bottomLeaves");
		this.lowerMid = this.bottom.getChild("lowerMid");
		this.lowerMidLeaves = this.lowerMid.getChild("lowerMidLeaves");
		this.higherMid = this.lowerMid.getChild("higherMid");
		this.higherMidLeaves = this.higherMid.getChild("higherMidLeaves");
		this.top = this.higherMid.getChild("top");
		this.topLeaves = this.top.getChild("topLeaves");
		this.biggerHead = this.top.getChild("biggerHead");
		this.headLeaves = this.biggerHead.getChild("headLeaves");
		this.head = this.biggerHead.getChild("head");
		this.mouth_base = this.head.getChild("mouth_base");
		this.mouth_bottom = this.head.getChild("mouth_bottom");
		this.mouth_top = this.head.getChild("mouth_top");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition mucellith = partdefinition.addOrReplaceChild("mucellith", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition base = mucellith.addOrReplaceChild("base", CubeListBuilder.create().texOffs(24, 28).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = mucellith.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 30).addBox(-1.5F, -9.5F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition bottomLeaves = bottom.addOrReplaceChild("bottomLeaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bottomLeaves_r1 = bottomLeaves.addOrReplaceChild("bottomLeaves_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-6.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition bottomLeaves_r2 = bottomLeaves.addOrReplaceChild("bottomLeaves_r2", CubeListBuilder.create().texOffs(0, 12).addBox(0.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition bottomLeaves_r3 = bottomLeaves.addOrReplaceChild("bottomLeaves_r3", CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition bottomLeaves_r4 = bottomLeaves.addOrReplaceChild("bottomLeaves_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition lowerMid = bottom.addOrReplaceChild("lowerMid", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

		PartDefinition lowerMidLeaves = lowerMid.addOrReplaceChild("lowerMidLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition lowerMidLeaves_r1 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r1", CubeListBuilder.create().texOffs(12, 42).addBox(-5.0F, 0.0F, -5.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition lowerMidLeaves_r2 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r2", CubeListBuilder.create().texOffs(40, 28).addBox(0.0F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition lowerMidLeaves_r3 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r3", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, 0.0F, -5.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition lowerMidLeaves_r4 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r4", CubeListBuilder.create().texOffs(12, 37).addBox(-5.0F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition higherMid = lowerMid.addOrReplaceChild("higherMid", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition higherMidLeaves = higherMid.addOrReplaceChild("higherMidLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition higherMidLeaves_r1 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-4.0F, 0.0F, -4.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition higherMidLeaves_r2 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r2", CubeListBuilder.create().texOffs(32, 46).addBox(0.0F, 0.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition higherMidLeaves_r3 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r3", CubeListBuilder.create().texOffs(32, 42).addBox(0.0F, 0.0F, -4.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition higherMidLeaves_r4 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r4", CubeListBuilder.create().texOffs(40, 33).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition top = higherMid.addOrReplaceChild("top", CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, -6.5F, -0.75F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition topLeaves = top.addOrReplaceChild("topLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition topLeaves_r1 = topLeaves.addOrReplaceChild("topLeaves_r1", CubeListBuilder.create().texOffs(48, 9).addBox(-3.0F, 0.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition topLeaves_r2 = topLeaves.addOrReplaceChild("topLeaves_r2", CubeListBuilder.create().texOffs(0, 43).addBox(0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition topLeaves_r3 = topLeaves.addOrReplaceChild("topLeaves_r3", CubeListBuilder.create().texOffs(12, 33).addBox(0.0F, 0.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition topLeaves_r4 = topLeaves.addOrReplaceChild("topLeaves_r4", CubeListBuilder.create().texOffs(12, 30).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition biggerHead = top.addOrReplaceChild("biggerHead", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -6.0F, 0.1737F, 0.7418F, 0.0F, 0.0F));

		PartDefinition headLeaves = biggerHead.addOrReplaceChild("headLeaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.7517F, 0.0F));

		PartDefinition headLeaves_r1 = headLeaves.addOrReplaceChild("headLeaves_r1", CubeListBuilder.create().texOffs(24, 12).addBox(-6.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2483F, 0.0763F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition headLeaves_r2 = headLeaves.addOrReplaceChild("headLeaves_r2", CubeListBuilder.create().texOffs(24, 6).addBox(0.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2483F, 0.0763F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition headLeaves_r3 = headLeaves.addOrReplaceChild("headLeaves_r3", CubeListBuilder.create().texOffs(24, 0).addBox(0.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2483F, 0.0763F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition headLeaves_r4 = headLeaves.addOrReplaceChild("headLeaves_r4", CubeListBuilder.create().texOffs(0, 24).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2483F, 0.0763F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition head = biggerHead.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1737F, 0.1309F, 0.0F, 0.0F));

		PartDefinition mouth_base = head.addOrReplaceChild("mouth_base", CubeListBuilder.create().texOffs(16, 47).addBox(-2.5F, -1.0F, -2.0F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mouth_bottom = head.addOrReplaceChild("mouth_bottom", CubeListBuilder.create().texOffs(42, 18).addBox(-3.0F, -7.0F, -2.3F, 6.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mouth_top = head.addOrReplaceChild("mouth_top", CubeListBuilder.create().texOffs(24, 18).addBox(-3.0F, -7.0F, -0.3F, 6.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		mucellith.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return mucellith;
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

	}
}