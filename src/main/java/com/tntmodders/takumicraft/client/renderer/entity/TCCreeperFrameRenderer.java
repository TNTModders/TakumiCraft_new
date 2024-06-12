package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.model.TCCreeperFrameModel;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperFrame;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperGlowingFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class TCCreeperFrameRenderer<T extends TCCreeperFrame> extends ItemFrameRenderer<T> {
    public static final ResourceLocation CREEPER_FRAME_LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/block/creeperframe.png");
    public static final ResourceLocation CREEPER_GLOW_FRAME_LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/block/creeperframe_glowing.png");

    private static final ModelResourceLocation FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=false");
    private static final ModelResourceLocation MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("item_frame", "map=true");
    private static final ModelResourceLocation GLOW_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=false");
    private static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = ModelResourceLocation.vanilla("glow_item_frame", "map=true");

    private final ModelPart modelPart;

    public TCCreeperFrameRenderer(EntityRendererProvider.Context p_174204_) {
        super(p_174204_);
        this.modelPart = p_174204_.bakeLayer(TCRenderCore.FRAME);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_115071_) {
        return p_115071_.getType() == TCCreeperGlowingFrame.GLOWING_FRAME ? CREEPER_GLOW_FRAME_LOCATION : CREEPER_FRAME_LOCATION;
    }

    @Override
    protected int getBlockLightLevel(T p_174216_, BlockPos p_174217_) {
        return p_174216_.getType() == TCCreeperGlowingFrame.GLOWING_FRAME ? Math.max(5, super.getBlockLightLevel(p_174216_, p_174217_)) : super.getBlockLightLevel(p_174216_, p_174217_);
    }

    private void baseRender(T creeperframe, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource bufferSource, int p_114490_) {
        var event = net.minecraftforge.client.event.ForgeEventFactoryClient.fireRenderNameTagEvent(creeperframe, creeperframe.getDisplayName(), this, poseStack, bufferSource, p_114490_, p_114487_);
        if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(creeperframe))) {
            this.renderNameTag(creeperframe, event.getContent(), poseStack, bufferSource, p_114490_, p_114487_);
        }
    }

    @Override
    public void render(T creeperframe, float p_115077_, float p_115078_, PoseStack poseStack, MultiBufferSource bufferSource, int p_115081_) {
        this.baseRender(creeperframe, p_115077_, p_115078_, poseStack, bufferSource, p_115081_);
        poseStack.pushPose();
        Direction direction = creeperframe.getDirection();
        Vec3 vec3 = this.getRenderOffset(creeperframe, p_115078_);
        poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
        double d0 = 0.46875;
        poseStack.translate((double) direction.getStepX() * 0.46875, (double) direction.getStepY() * 0.46875, (double) direction.getStepZ() * 0.46875);
        poseStack.mulPose(Axis.XP.rotationDegrees(creeperframe.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - creeperframe.getYRot()));
        boolean flag = creeperframe.isInvisible();
        ItemStack itemstack = creeperframe.getItem();
        if (!flag) {
            ModelManager modelmanager = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager();
            ModelResourceLocation modelresourcelocation = this.getFrameModelResourceLoc(creeperframe, itemstack);
            BakedModel model = new TCCreeperFrameModel(modelmanager.getModel(modelresourcelocation));
            poseStack.pushPose();
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), null, model, 1.0F, 1.0F, 1.0F, p_115081_, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 0.5F);
            RenderType type = creeperframe.getType() == TCCreeperGlowingFrame.GLOWING_FRAME ? RenderType.energySwirl(this.getTextureLocation(creeperframe), 0f, 0f) : RenderType.entityTranslucent(this.getTextureLocation(creeperframe));
            this.modelPart.render(poseStack, bufferSource.getBuffer(type).color(0.5f, 0.5f, 0.5f, 0.5f), p_115081_, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if (flag) {
            poseStack.translate(0.0F, 0.0F, 0.5F);
        } else {
            poseStack.translate(0.0F, 0.0F, 0.4375F);
        }

        if (!itemstack.isEmpty()) {
            MapId mapid = creeperframe.getFramedMapId();
            int k = this.getLightVal(creeperframe, 15728880, p_115081_);
            int j = mapid != null ? creeperframe.getRotation() % 4 * 2 : creeperframe.getRotation();
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0F / 8.0F));
            if (!net.minecraftforge.client.event.ForgeEventFactoryClient.onRenderItemInFrame(creeperframe, this, poseStack, bufferSource, p_115081_)) {
                if (mapid != null) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    float f = 0.0078125F;
                    poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
                    poseStack.translate(-64.0F, -64.0F, 0.0F);
                    MapItemSavedData mapitemsaveddata = MapItem.getSavedData(mapid, creeperframe.level());
                    poseStack.translate(0.0F, 0.0F, -1.0F);
                    if (mapitemsaveddata != null) {
                        int i = this.getLightVal(creeperframe, 15728850, p_115081_);
                        Minecraft.getInstance().gameRenderer.getMapRenderer().render(poseStack, bufferSource, mapid, mapitemsaveddata, true, i);
                    }
                } else {
                    //int k = this.getLightVal(creeperframe, 15728880, p_115081_);
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                    Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemDisplayContext.FIXED, k, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, creeperframe.level(), creeperframe.getId());
                }
            }
        }

        poseStack.popPose();
    }

    private int getLightVal(T p_174209_, int p_174210_, int p_174211_) {
        return p_174209_.getType() == TCCreeperGlowingFrame.GLOWING_FRAME ? p_174210_ : p_174211_;
    }

    private ModelResourceLocation getFrameModelResourceLoc(T p_174213_, ItemStack p_174214_) {
        boolean flag = p_174213_.getType() == TCCreeperGlowingFrame.GLOWING_FRAME;
        if (p_174214_.getItem() instanceof MapItem) {
            return flag ? GLOW_MAP_FRAME_LOCATION : MAP_FRAME_LOCATION;
        } else {
            return flag ? GLOW_FRAME_LOCATION : FRAME_LOCATION;
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -8, -0.6f, 16, 16, 0), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 16, 16);
    }
}
