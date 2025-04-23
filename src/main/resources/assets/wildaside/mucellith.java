// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class mucellith<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "mucellith"), "main");
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

	public mucellith(ModelPart root) {
		this.base = root.getChild("base");
		this.body = root.getChild("body");
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

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(24, 28).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 30).addBox(-1.5F, -9.9396F, -1.5F, 3.0F, 10.5F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition bottomLeaves = bottom.addOrReplaceChild("bottomLeaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.2517F, -0.0763F));

		PartDefinition bottomLeaves_r1 = bottomLeaves.addOrReplaceChild("bottomLeaves_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-6.5F, 0.0022F, -6.25F, 6.5F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition bottomLeaves_r2 = bottomLeaves.addOrReplaceChild("bottomLeaves_r2", CubeListBuilder.create().texOffs(0, 12).addBox(-0.25F, 0.0022F, 0.0F, 6.5F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition bottomLeaves_r3 = bottomLeaves.addOrReplaceChild("bottomLeaves_r3", CubeListBuilder.create().texOffs(0, 6).addBox(-0.25F, 0.0022F, -6.25F, 6.5F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1288F, -0.0338F, -0.1265F));

		PartDefinition bottomLeaves_r4 = bottomLeaves.addOrReplaceChild("bottomLeaves_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, 0.0022F, 0.0F, 6.5F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition lowerMid = bottom.addOrReplaceChild("lowerMid", CubeListBuilder.create(), PartPose.offset(0.0F, -3.9983F, -0.0763F));

		PartDefinition lowerMidLeaves = lowerMid.addOrReplaceChild("lowerMidLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition lowerMidLeaves_r1 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r1", CubeListBuilder.create().texOffs(12, 42).addBox(-5.25F, 0.0022F, -5.25F, 5.25F, 0.0F, 5.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition lowerMidLeaves_r2 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r2", CubeListBuilder.create().texOffs(40, 28).addBox(0.0F, 0.0022F, 0.0F, 5.25F, 0.0F, 5.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition lowerMidLeaves_r3 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r3", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, 0.0022F, -5.25F, 5.25F, 0.0F, 5.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1288F, -0.0338F, -0.1265F));

		PartDefinition lowerMidLeaves_r4 = lowerMidLeaves.addOrReplaceChild("lowerMidLeaves_r4", CubeListBuilder.create().texOffs(12, 37).addBox(-5.25F, 0.0022F, 0.0F, 5.25F, 0.0F, 5.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition higherMid = lowerMid.addOrReplaceChild("higherMid", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 0.0F));

		PartDefinition higherMidLeaves = higherMid.addOrReplaceChild("higherMidLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.25F, 0.0F, 0.3487F, -0.0043F, -0.0071F));

		PartDefinition higherMidLeaves_r1 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-4.25F, 0.0022F, -4.25F, 4.25F, 0.0F, 4.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition higherMidLeaves_r2 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r2", CubeListBuilder.create().texOffs(32, 46).addBox(0.0F, 0.0022F, 0.0F, 4.25F, 0.0F, 4.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition higherMidLeaves_r3 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r3", CubeListBuilder.create().texOffs(32, 42).addBox(0.0F, 0.0022F, -4.25F, 4.25F, 0.0F, 4.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1288F, -0.0338F, -0.1265F));

		PartDefinition higherMidLeaves_r4 = higherMidLeaves.addOrReplaceChild("higherMidLeaves_r4", CubeListBuilder.create().texOffs(40, 33).addBox(-4.25F, 0.0022F, 0.0F, 4.25F, 0.0F, 4.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition top = higherMid.addOrReplaceChild("top", CubeListBuilder.create().texOffs(48, 0).addBox(-1.25F, -7.0206F, -1.1737F, 2.5F, 7.5F, 2.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition topLeaves = top.addOrReplaceChild("topLeaves", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -1.75F, 0.0F, 0.2618F, 0.0F, 0.0F));

		PartDefinition topLeaves_r1 = topLeaves.addOrReplaceChild("topLeaves_r1", CubeListBuilder.create().texOffs(48, 9).addBox(-3.25F, 0.0022F, -3.25F, 3.25F, 0.0F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition topLeaves_r2 = topLeaves.addOrReplaceChild("topLeaves_r2", CubeListBuilder.create().texOffs(0, 43).addBox(0.0F, 0.0022F, 0.0F, 3.25F, 0.0F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition topLeaves_r3 = topLeaves.addOrReplaceChild("topLeaves_r3", CubeListBuilder.create().texOffs(12, 33).addBox(0.0F, 0.0022F, -3.25F, 3.25F, 0.0F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1288F, -0.0338F, -0.1265F));

		PartDefinition topLeaves_r4 = topLeaves.addOrReplaceChild("topLeaves_r4", CubeListBuilder.create().texOffs(12, 30).addBox(-3.25F, 0.0022F, 0.0F, 3.25F, 0.0F, 3.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition biggerHead = top.addOrReplaceChild("biggerHead", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.7418F, 0.0F, 0.0F));

		PartDefinition headLeaves = biggerHead.addOrReplaceChild("headLeaves", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 0.0F));

		PartDefinition headLeaves_r1 = headLeaves.addOrReplaceChild("headLeaves_r1", CubeListBuilder.create().texOffs(24, 12).addBox(-6.25F, 0.0022F, -6.25F, 6.25F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1309F, 0.0F, 0.1309F));

		PartDefinition headLeaves_r2 = headLeaves.addOrReplaceChild("headLeaves_r2", CubeListBuilder.create().texOffs(24, 6).addBox(0.0F, 0.0022F, 0.0F, 6.25F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, -0.1309F));

		PartDefinition headLeaves_r3 = headLeaves.addOrReplaceChild("headLeaves_r3", CubeListBuilder.create().texOffs(24, 0).addBox(0.0F, 0.0022F, -6.25F, 6.25F, 0.0F, 6.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, -0.1288F, -0.0338F, -0.1265F));

		PartDefinition headLeaves_r4 = headLeaves.addOrReplaceChild("headLeaves_r4", CubeListBuilder.create().texOffs(0, 24).addBox(-6.25F, 0.0022F, 0.25F, 6.25F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.1309F, 0.0F, 0.1309F));

		PartDefinition head = biggerHead.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition mouth_base = head.addOrReplaceChild("mouth_base", CubeListBuilder.create().texOffs(16, 47).addBox(-2.875F, -1.6F, -1.475F, 5.75F, 1.5F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition mouth_bottom = head.addOrReplaceChild("mouth_bottom", CubeListBuilder.create().texOffs(42, 18).addBox(-3.0F, -7.0218F, -1.4995F, 6.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition mouth_top = head.addOrReplaceChild("mouth_top", CubeListBuilder.create().texOffs(24, 18).addBox(-3.0F, -7.0F, -0.5F, 6.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}