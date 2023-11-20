package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.block.model.TCSaberModel;
import com.tntmodders.takumicraft.client.renderer.block.model.TCShieldModel;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.item.TCCreeperShieldItem;
import com.tntmodders.takumicraft.item.TCSaberItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.List;

public class TCBEWLRenderer extends BlockEntityWithoutLevelRenderer {
    private final TCSaberModel saberModel;
    private final TCShieldModel shieldModel;

    public TCBEWLRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.saberModel = new TCSaberModel(Minecraft.getInstance().getEntityModels().bakeLayer(TCRenderCore.SABER));
        this.shieldModel = new TCShieldModel(Minecraft.getInstance().getEntityModels().bakeLayer(TCRenderCore.SHIELD));
    }

    static void renderPatterns(PoseStack poseStack, MultiBufferSource bufferSource, int p_112077_, int p_112078_, ModelPart modelPart, Material material, boolean p_112081_, List<Pair<Holder<BannerPattern>, DyeColor>> p_112082_, boolean p_112083_) {
        modelPart.render(poseStack, bufferSource.getBuffer(RenderType.entitySolid(new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/shield.png"))), p_112077_, p_112078_);

        for (int i = 0; i < 17 && i < p_112082_.size(); ++i) {
            final int factor = i;
            Pair<Holder<BannerPattern>, DyeColor> pair = p_112082_.get(i);
            float[] afloat = pair.getSecond().getTextureDiffuseColors();
            pair.getFirst().unwrapKey().map(p_234428_ -> p_112081_ ? Sheets.getBannerMaterial(p_234428_) : Sheets.getShieldMaterial(p_234428_)).ifPresent(p_234425_ -> modelPart.render(poseStack, p_234425_.buffer(bufferSource, RenderType::entityNoOutline), p_112077_, p_112078_, afloat[0], afloat[1], afloat[2], 0.75f));
        }

    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext p_108831_, PoseStack poseStack, MultiBufferSource bufferSource, int p_108834_, int p_108835_) {
        if (stack.getItem() instanceof TCSaberItem) {
            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            VertexConsumer vertexconsumer1 = ItemRenderer.getFoilBufferDirect(bufferSource, this.saberModel.renderType(new ResourceLocation("textures/block/iron_block.png")), false, stack.hasFoil());
            this.saberModel.renderToBufferBase(poseStack, vertexconsumer1, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);

            float f = Minecraft.getInstance().player.tickCount * 0.01f;
            VertexConsumer vertexconsumer2 = bufferSource.getBuffer(RenderType.energySwirl(new ResourceLocation(TakumiCraftCore.MODID, "textures/item/lightsaber_armor.png"), f % 1.0F, f * 0.01F % 1.0F));
            this.saberModel.renderToBufferBlade(poseStack, vertexconsumer2, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        } else if (stack.getItem() instanceof TCCreeperShieldItem) {
            boolean flag = BlockItem.getBlockEntityData(stack) != null;
            poseStack.pushPose();
            poseStack.scale(1.0F, -1.0F, -1.0F);
            Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid(new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/shield.png")));
            this.shieldModel.handle().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            if (flag) {
                List<Pair<Holder<BannerPattern>, DyeColor>> list = BannerBlockEntity.createPatterns(ShieldItem.getColor(stack), BannerBlockEntity.getItemPatterns(stack));
                renderPatterns(poseStack, bufferSource, p_108834_, p_108835_, this.shieldModel.plate(), material, false, list, stack.hasFoil());
            } else {
                this.shieldModel.plate().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            poseStack.popPose();
        } else {
            super.renderByItem(stack, p_108831_, poseStack, bufferSource, p_108834_, p_108835_);
        }
    }
}
