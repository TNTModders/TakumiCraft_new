package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCChildCreeperModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCChildCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.entity.mobs.TCChildCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TCChildCreeperRenderer extends MobRenderer<TCChildCreeper, TCChildCreeperRenderState, TCChildCreeperModel> {
    private static final ResourceLocation CREEPER_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/childcreeper.png");

    public TCChildCreeperRenderer(EntityRendererProvider.Context p_173958_) {
        super(p_173958_, new TCChildCreeperModel(p_173958_.bakeLayer(TCRenderCore.CHILD)), 0.5F);
        this.addLayer(new TCCreeperPowerLayer(this, p_173958_.getModelSet(), this.model, TCEntityCore.CHILD, true));
    }

    @Override
    public ResourceLocation getTextureLocation(TCChildCreeperRenderState creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }

    @Override
    protected void scale(TCChildCreeperRenderState state, PoseStack poseStack) {
        float sizeFactor = state.context.getSizeFactor();
        poseStack.scale(sizeFactor, sizeFactor, sizeFactor);
        TCClientUtils.scaleSwelling(state.swelling, poseStack);
    }

    @Override
    protected float getWhiteOverlayProgress(TCChildCreeperRenderState state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public TCChildCreeperRenderState createRenderState() {
        return new TCChildCreeperRenderState();
    }

    @Override
    public void extractRenderState(TCChildCreeper creeper, TCChildCreeperRenderState state, float f) {
        super.extractRenderState(creeper, state, f);
        state.swelling = creeper.getSwelling(f);
        state.isPowered = creeper.isPowered();
        state.context = creeper.getContext();
        state.isChild = creeper.isChild();
        state.isOnBook = creeper.isOnBook();
    }

    @Override
    public void render(TCChildCreeperRenderState state, PoseStack poseStack, MultiBufferSource p_115312_, int p_115313_) {
        boolean flg = this.getModel().head.visible;
        if (flg) {
            boolean flag1 = this.isBodyVisible(state);
            boolean flag = !flag1 && !state.isInvisibleToPlayer;
            RenderType rendertype = this.getRenderType(state, flag1, flag, state.appearsGlowing);
            if (rendertype != null) {
                VertexConsumer vertex = p_115312_.getBuffer(rendertype);
                int i = getOverlayCoords(state, this.getWhiteOverlayProgress(state));
                int j = flag ? 654311423 : -1;
                int k = ARGB.multiply(j, this.getModelTint(state));
                this.getModel().head.render(poseStack, vertex, i, k);
                this.getModel().root.render(poseStack, vertex, i, k);
                this.getModel().leftFrontLeg.render(poseStack, vertex, i, k);
                this.getModel().rightFrontLeg.render(poseStack, vertex, i, k);
                this.getModel().leftHindLeg.render(poseStack, vertex, i, k);
                this.getModel().rightHindLeg.render(poseStack, vertex, i, k);
            }
        }

        poseStack.pushPose();
        if (flg) {
            poseStack.translate(0f, 0.375f, 0.05f);
            poseStack.rotateAround(Axis.XP.rotationDegrees(20), 0, 1, 0);
        } else {
            poseStack.scale(2f, 2f, 2f);
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);
        super.render(state, poseStack, p_115312_, p_115313_);
        poseStack.popPose();
    }
}