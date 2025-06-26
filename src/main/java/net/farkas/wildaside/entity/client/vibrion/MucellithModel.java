package net.farkas.wildaside.entity.client.vibrion;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.farkas.wildaside.entity.animations.MucellithAnimations;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
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

		PartDefinition base = mucellith.addOrReplaceChild("base", CubeListBuilder.create().texOffs(36, 24).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = mucellith.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 58).addBox(-2.0F, -12.5171F, -2.0111F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition bottomLeaves = bottom.addOrReplaceChild("bottomLeaves", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5171F, -0.2611F));

		PartDefinition bottomLeaves_r1 = bottomLeaves.addOrReplaceChild("bottomLeaves_r1", CubeListBuilder.create().texOffs(36, 16).addBox(-8.0F, 0.0F, -8.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition bottomLeaves_r2 = bottomLeaves.addOrReplaceChild("bottomLeaves_r2", CubeListBuilder.create().texOffs(36, 8).addBox(0.0F, 0.0F, 0.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition bottomLeaves_r3 = bottomLeaves.addOrReplaceChild("bottomLeaves_r3", CubeListBuilder.create().texOffs(36, 0).addBox(0.0F, 0.0F, -8.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition bottomLeaves_r4 = bottomLeaves.addOrReplaceChild("bottomLeaves_r4", CubeListBuilder.create().texOffs(0, 36).addBox(-8.0F, 0.0F, 0.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition lowerMid = bottom.addOrReplaceChild("lowerMid", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0171F, -0.2611F));

		PartDefinition lowerMidLeaves = lowerMid.addOrReplaceChild("lowerMidLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.4957F, 0.0653F, 0.1745F, 0.0F, 0.0F));

		PartDefinition lowerMidLeaves_r1 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r1", CubeListBuilder.create().texOffs(0, 51).addBox(-7.0F, 0.0F, -7.0F, 7.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0434F, 0.0038F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition lowerMidLeaves_r2 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r2", CubeListBuilder.create().texOffs(28, 44).addBox(0.0F, 0.0F, 0.0F, 7.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0434F, 0.0038F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition lowerMidLeaves_r3 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r3", CubeListBuilder.create().texOffs(0, 44).addBox(0.0F, 0.0F, -7.0F, 7.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0434F, 0.0038F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition lowerMidLeaves_r4 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r4", CubeListBuilder.create().texOffs(32, 37).addBox(-7.0F, 0.0F, 0.0F, 7.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.0434F, 0.0038F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition higherMid = lowerMid.addOrReplaceChild("higherMid", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition higherMidLeaves = higherMid.addOrReplaceChild("higherMidLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -3.6783F, 0.5936F, 0.3491F, 0.0F, 0.0F));

		PartDefinition higherMidLeaves_r1 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r1", CubeListBuilder.create().texOffs(16, 63).addBox(-6.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.171F, 0.0302F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition higherMidLeaves_r2 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r2", CubeListBuilder.create().texOffs(60, 36).addBox(0.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.171F, 0.0302F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition higherMidLeaves_r3 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r3", CubeListBuilder.create().texOffs(60, 30).addBox(0.0F, 0.0F, -6.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.171F, 0.0302F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition higherMidLeaves_r4 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r4", CubeListBuilder.create().texOffs(60, 24).addBox(-6.0F, 0.0F, 0.0F, 6.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.171F, 0.0302F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition top = higherMid.addOrReplaceChild("top", CubeListBuilder.create().texOffs(68, 10).addBox(-1.5F, -7.4896F, -1.1084F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.15F, 0.4363F, 0.0F, 0.0F));

		PartDefinition topLeaves = top.addOrReplaceChild("topLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.4411F, 2.3164F, 0.2618F, 0.0F, 0.0F));

		PartDefinition topLeaves_r1 = topLeaves.addOrReplaceChild("topLeaves_r1", CubeListBuilder.create().texOffs(68, 5).addBox(-5.0F, 0.0F, -5.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4276F, -1.6213F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition topLeaves_r2 = topLeaves.addOrReplaceChild("topLeaves_r2", CubeListBuilder.create().texOffs(60, 63).addBox(0.0F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4276F, -1.6213F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition topLeaves_r3 = topLeaves.addOrReplaceChild("topLeaves_r3", CubeListBuilder.create().texOffs(68, 0).addBox(0.0F, 0.0F, -5.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4276F, -1.6213F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition topLeaves_r4 = topLeaves.addOrReplaceChild("topLeaves_r4", CubeListBuilder.create().texOffs(40, 63).addBox(-5.0F, 0.0F, 0.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4276F, -1.6213F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition biggerHead = top.addOrReplaceChild("biggerHead", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -7.762F, 0.708F, 0.7418F, 0.0F, 0.0F));

		PartDefinition headLeaves = biggerHead.addOrReplaceChild("headLeaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.7517F, 0.0F));

		PartDefinition headLeaves_r1 = headLeaves.addOrReplaceChild("headLeaves_r1", CubeListBuilder.create().texOffs(0, 9).addBox(-9.0F, 0.0F, -9.0F, 9.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2906F, -0.183F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition headLeaves_r2 = headLeaves.addOrReplaceChild("headLeaves_r2", CubeListBuilder.create().texOffs(0, 27).addBox(0.0F, 0.0F, -0.25F, 9.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2906F, 0.067F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition headLeaves_r3 = headLeaves.addOrReplaceChild("headLeaves_r3", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -9.0F, 9.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2906F, -0.183F, -0.1309F, 0.0F, -0.1309F));

		PartDefinition headLeaves_r4 = headLeaves.addOrReplaceChild("headLeaves_r4", CubeListBuilder.create().texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2906F, -0.183F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition head = biggerHead.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, -0.1737F, 0.1309F, 0.0F, 0.0F));

		PartDefinition mouth_base = head.addOrReplaceChild("mouth_base", CubeListBuilder.create().texOffs(56, 44).addBox(-3.5F, -1.2056F, -2.042F, 7.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mouth_bottom = head.addOrReplaceChild("mouth_bottom", CubeListBuilder.create().texOffs(50, 51).addBox(-4.0F, -8.9577F, -2.3094F, 8.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition mouth_top = head.addOrReplaceChild("mouth_top", CubeListBuilder.create().texOffs(28, 51).addBox(-4.0F, -8.9577F, 0.6906F, 8.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
		mucellith.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
	}

	@Override
	public ModelPart root() {
		return mucellith;
	}

	@Override
	public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.applyHeadRotation(pNetHeadYaw, pHeadPitch, pAgeInTicks);

		this.animate(((MucellithEntity)pEntity).idleAnimation, MucellithAnimations.IDLE, pAgeInTicks, 1f);
		this.animate(((MucellithEntity)pEntity).attackAnimation, MucellithAnimations.ATTACK, pAgeInTicks, 1f);
		this.animate(((MucellithEntity)pEntity).defenseAnimation, MucellithAnimations.TO_DEFENSE, pAgeInTicks, 1f);
		this.animate(((MucellithEntity)pEntity).defenseAnimationReverse, MucellithAnimations.REVERSE_DEFENSE, pAgeInTicks, 1f);
	}

	private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
		pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
		pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F) + 40;

		this.biggerHead.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
		this.biggerHead.xRot = pHeadPitch * ((float)Math.PI / 180F);
	}

}