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
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
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
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutout(new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/shield.png")));
            this.shieldModel.handle().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            if (flag) {
                List<Pair<Holder<BannerPattern>, DyeColor>> list = BannerBlockEntity.createPatterns(ShieldItem.getColor(stack), BannerBlockEntity.getItemPatterns(stack));
                BannerRenderer.renderPatterns(poseStack, bufferSource, p_108834_, p_108835_, this.shieldModel.plate(), material, false, list, stack.hasFoil());
            } else {
                this.shieldModel.plate().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            poseStack.popPose();
        } else {
            super.renderByItem(stack, p_108831_, poseStack, bufferSource, p_108834_, p_108835_);
        }
    }
}
