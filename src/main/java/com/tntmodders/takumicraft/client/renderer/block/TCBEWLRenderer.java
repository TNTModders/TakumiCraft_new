package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperBedBlock;
import com.tntmodders.takumicraft.block.TCCreeperChestBlock;
import com.tntmodders.takumicraft.block.TCCreeperShulkerBoxBlock;
import com.tntmodders.takumicraft.block.TCMonsterBombBlock;
import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
import com.tntmodders.takumicraft.block.entity.TCCreeperChestBlockEntity;
import com.tntmodders.takumicraft.block.entity.TCCreeperShulkerBoxBlockEntity;
import com.tntmodders.takumicraft.client.renderer.block.model.TCSaberModel;
import com.tntmodders.takumicraft.client.renderer.block.model.TCShieldModel;
import com.tntmodders.takumicraft.core.TCBlockCore;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.Objects;

public class TCBEWLRenderer extends BlockEntityWithoutLevelRenderer {
    private final TCSaberModel saberModel;
    private final TCShieldModel shieldModel;

    private final TCCreeperBedBlockEntity bed = new TCCreeperBedBlockEntity(BlockPos.ZERO, Blocks.RED_BED.defaultBlockState());
    private final TCCreeperChestBlockEntity chest = new TCCreeperChestBlockEntity(BlockPos.ZERO, TCBlockCore.CREEPER_CHEST.defaultBlockState());
    private final TCCreeperShulkerBoxBlockEntity shulker = new TCCreeperShulkerBoxBlockEntity(BlockPos.ZERO, TCBlockCore.CREEPER_SHULKER.defaultBlockState());

    public TCBEWLRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.saberModel = new TCSaberModel(Minecraft.getInstance().getEntityModels().bakeLayer(TCRenderCore.SABER));
        this.shieldModel = new TCShieldModel(Minecraft.getInstance().getEntityModels().bakeLayer(TCRenderCore.SHIELD));
    }

    public static void renderPatterns(PoseStack p_112075_, MultiBufferSource p_112076_, int p_112077_, int p_112078_, ModelPart p_112079_, Material p_112080_, boolean p_112081_, DyeColor p_336347_, BannerPatternLayers p_332113_, boolean p_112083_) {
        p_112079_.render(p_112075_, p_112076_.getBuffer(RenderType.entitySolid(new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/shield.png"))), p_112077_, p_112078_);
        renderPatternLayer(p_112075_, p_112076_, p_112077_, p_112078_, p_112079_, p_112081_ ? Sheets.BANNER_BASE : Sheets.SHIELD_BASE, p_336347_);

        for (int i = 0; i < 16 && i < p_332113_.layers().size(); i++) {
            BannerPatternLayers.Layer bannerpatternlayers$layer = p_332113_.layers().get(i);
            Material material = p_112081_ ? Sheets.getBannerMaterial(bannerpatternlayers$layer.pattern()) : Sheets.getShieldMaterial(bannerpatternlayers$layer.pattern());
            renderPatternLayer(p_112075_, p_112076_, p_112077_, p_112078_, p_112079_, material, bannerpatternlayers$layer.color());
        }
    }

    private static void renderPatternLayer(PoseStack p_332210_, MultiBufferSource p_336119_, int p_333952_, int p_335632_, ModelPart p_327937_, Material p_327979_, DyeColor p_331652_) {
        float[] afloat = p_331652_.getTextureDiffuseColors();
        p_327937_.render(p_332210_, p_327979_.buffer(p_336119_, RenderType::entityNoOutline), p_333952_, p_335632_, afloat[0], afloat[1], afloat[2], 0.75F);
    }


    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext p_108831_, PoseStack poseStack, MultiBufferSource bufferSource, int p_108834_, int p_108835_) {
        switch (stack.getItem()) {
            case TCSaberItem tcSaberItem -> {
                poseStack.pushPose();
                poseStack.scale(1.0F, -1.0F, -1.0F);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                VertexConsumer vertexconsumer1 = ItemRenderer.getFoilBufferDirect(bufferSource, this.saberModel.renderType(new ResourceLocation("textures/block/iron_block.png")), false, stack.hasFoil());
                this.saberModel.renderToBufferBase(poseStack, vertexconsumer1, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);

                float f = Minecraft.getInstance().player.tickCount * 0.01f;
                VertexConsumer vertexconsumer2 = bufferSource.getBuffer(RenderType.energySwirl(new ResourceLocation(TakumiCraftCore.MODID, "textures/item/lightsaber_armor.png"), f % 1.0F, f * 0.01F % 1.0F));
                this.saberModel.renderToBufferBlade(poseStack, vertexconsumer2, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }
            case TCCreeperShieldItem tcCreeperShieldItem -> {
                BannerPatternLayers bannerpatternlayers = stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
                DyeColor dyecolor = stack.get(DataComponents.BASE_COLOR);
                boolean flag = !bannerpatternlayers.layers().isEmpty() || dyecolor != null;
                poseStack.pushPose();
                poseStack.scale(1.0F, -1.0F, -1.0F);
                Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
                VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid(new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/shield.png")));
                this.shieldModel.handle().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
                if (flag) {
                    renderPatterns(poseStack, bufferSource, p_108834_, p_108835_, this.shieldModel.plate(), material, false, Objects.requireNonNullElse(dyecolor, DyeColor.WHITE), bannerpatternlayers, stack.hasFoil());
                } else {
                    this.shieldModel.plate().render(poseStack, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
                }
                poseStack.popPose();
            }
            case BlockItem blockItem when blockItem.getBlock() instanceof TCCreeperBedBlock bedBlock -> {
                this.bed.setColor(bedBlock.getColor());
                this.bed.setSuper(bedBlock == TCBlockCore.SUPER_CREEPER_BED);
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.bed, poseStack, bufferSource, p_108834_, p_108835_);
            }
            case BlockItem blockItem when blockItem.getBlock() instanceof TCMonsterBombBlock bombBlock ->
                    TCMonsterBombBlockRenderer.renderBomb(bombBlock, poseStack, bufferSource, p_108834_, p_108835_);
            case BlockItem blockItem when blockItem.getBlock() instanceof TCCreeperChestBlock chestBlock ->
                    Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.chest, poseStack, bufferSource, p_108834_, p_108835_);
            case BlockItem blockItem when blockItem.getBlock() instanceof TCCreeperShulkerBoxBlock shulkerBoxBlock -> {
                Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(this.shulker, poseStack, bufferSource, p_108834_, p_108835_);
            }
            default -> super.renderByItem(stack, p_108831_, poseStack, bufferSource, p_108834_, p_108835_);
        }
    }
}
