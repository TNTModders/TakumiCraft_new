package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCBlockCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.tntmodders.takumicraft.client.renderer.block.TCSuperBlockRenderer.POWERED_TEXTURE;

@OnlyIn(Dist.CLIENT)
public class TCBlockCreeperRenderer extends EntityRenderer<AbstractTCBlockCreeper> {
    private final BlockRenderDispatcher dispatcher;
    private final boolean useOverlay;

    public TCBlockCreeperRenderer(EntityRendererProvider.Context context) {
        this(context, false);
    }

    public TCBlockCreeperRenderer(EntityRendererProvider.Context context, boolean overlay) {
        super(context);
        this.shadowRadius = 0F;
        this.dispatcher = context.getBlockRenderDispatcher();
        this.useOverlay = overlay;
    }

    @Override
    public void render(AbstractTCBlockCreeper creeper, float p_114635_, float pertialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int p_114639_) {
        if (creeper.isAlive()) {
            BlockState blockstate = creeper.getBlock();
            if (blockstate.getRenderShape() == RenderShape.MODEL) {
                Level level = creeper.level();
                if (blockstate != level.getBlockState(creeper.blockPosition()) && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                    poseStack.pushPose();
                    float tick = this.getWhiteOverlayProgress(creeper, pertialTicks) / 2;
                    float white = (int) (tick / 4.5f) % 2 == 1 ? 0.0F : Mth.clamp(tick, 0.4F, 0.8F);
                    int overlay = OverlayTexture.pack(OverlayTexture.u(white), OverlayTexture.v(false));
                    float f1 = 1.0F + Mth.sin(tick * 100.0F) * tick * 0.001F;
                    tick = Mth.clamp(tick, 0.0F, 1.0F);
                    float f2 = (1.0F + tick * 0.1F) * f1;
                    TCClientUtils.scaleSwelling(creeper, poseStack, pertialTicks);
                    BlockPos blockpos = BlockPos.containing(creeper.getX(), creeper.getBoundingBox().maxY, creeper.getZ());
                    poseStack.translate(-0.5, 0.0, -0.5);
                    var model = this.dispatcher.getBlockModel(blockstate);
                    for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(blockstate.getSeed(creeper.blockPosition())), net.minecraftforge.client.model.data.ModelData.EMPTY)) {
                        renderType = RenderType.entitySolid(this.getTextureLocation(creeper));
                        this.dispatcher
                                .getModelRenderer()
                                .tesselateBlock(
                                        level,
                                        model,
                                        blockstate,
                                        blockpos,
                                        poseStack,
                                        bufferSource.getBuffer(renderType),
                                        false,
                                        RandomSource.create(),
                                        blockstate.getSeed(creeper.blockPosition()),
                                        creeper.getSwelling(p_114635_) > 0 ? overlay : OverlayTexture.NO_OVERLAY,
                                        net.minecraftforge.client.model.data.ModelData.EMPTY,
                                        renderType
                                );
                        if (this.useOverlay) {
                            poseStack.pushPose();
                            float overrap = 0.03f;
                            poseStack.scale(1 + overrap, 1 + overrap, 1 + overrap);
                            poseStack.translate(-overrap / 2, 0, -overrap / 2);
                            BlockState blockstate1 = TCBlockCore.CREEPER_PROTECTOR.defaultBlockState();
                            model = this.dispatcher.getBlockModel(blockstate1);
                            for (var renderType1 : model.getRenderTypes(blockstate1, RandomSource.create(blockstate1.getSeed(creeper.blockPosition())), net.minecraftforge.client.model.data.ModelData.EMPTY)) {
                                renderType1 = RenderType.entityTranslucent(this.getTextureLocation(creeper));
                                this.dispatcher
                                        .getModelRenderer()
                                        .tesselateBlock(
                                                level,
                                                model,
                                                blockstate1,
                                                blockpos,
                                                poseStack,
                                                bufferSource.getBuffer(renderType1).setColor(0.8f, 0.8f, 0.8f, 0.8f),
                                                false,
                                                RandomSource.create(),
                                                blockstate1.getSeed(creeper.blockPosition()),
                                                creeper.getSwelling(p_114635_) > 0 ? overlay : OverlayTexture.NO_OVERLAY,
                                                net.minecraftforge.client.model.data.ModelData.EMPTY,
                                                renderType1
                                        );
                            }
                            poseStack.popPose();
                        }
                        if (creeper.isPowered()) {
                            poseStack.pushPose();
                            float overrap = 0.05f;
                            poseStack.scale(1 + overrap, 1 + overrap, 1 + overrap);
                            poseStack.translate(-overrap / 2, 0, -overrap / 2);
                            float f = Minecraft.getInstance().player.tickCount * 0.0005f;
                            renderType = RenderType.energySwirl(POWERED_TEXTURE, f % 1f, f % 1f);
                            VertexConsumer consumer = bufferSource.getBuffer(renderType);
                            this.dispatcher
                                    .getModelRenderer()
                                    .tesselateBlock(
                                            level,
                                            model,
                                            blockstate,
                                            blockpos,
                                            poseStack,
                                            consumer,
                                            false,
                                            RandomSource.create(),
                                            blockstate.getSeed(creeper.blockPosition()),
                                            OverlayTexture.NO_OVERLAY,
                                            net.minecraftforge.client.model.data.ModelData.EMPTY,
                                            renderType
                                    );
                            poseStack.popPose();
                        }
                    }
                    poseStack.popPose();
                    super.render(creeper, p_114635_, pertialTicks, poseStack, bufferSource, p_114639_);
                }
            }
        }
    }

    protected float getWhiteOverlayProgress(AbstractTCBlockCreeper p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractTCBlockCreeper p_114632_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}