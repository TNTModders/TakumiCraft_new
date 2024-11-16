package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.level.ExplosionEvent;
import org.joml.Matrix4f;

public class SPExplosionKingCreeperAttack extends AbstractKingCreeperAttack {
    @OnlyIn(Dist.CLIENT)
    private static void vertex01(VertexConsumer p_254498_, Matrix4f p_253891_, int p_254278_) {
        p_254498_.addVertex(p_253891_, 0.0F, 0.0F, 0.0F).setColor(255, 255, 255, p_254278_);
    }

    @OnlyIn(Dist.CLIENT)
    private static void vertex2(VertexConsumer p_253956_, Matrix4f p_254053_, float p_253704_, float p_253701_) {
        p_253956_.addVertex(p_254053_, -(float) (Math.sqrt(3.0) / 2.0) * p_253701_, p_253704_, -0.5F * p_253701_).setColor(0, 255, 0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    private static void vertex3(VertexConsumer p_253850_, Matrix4f p_254379_, float p_253729_, float p_254030_) {
        p_253850_.addVertex(p_254379_, (float) (Math.sqrt(3.0) / 2.0) * p_254030_, p_253729_, -0.5F * p_254030_).setColor(0, 255, 0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    private static void vertex4(VertexConsumer p_254184_, Matrix4f p_254082_, float p_253649_, float p_253694_) {
        p_254184_.addVertex(p_254082_, 0.0F, p_253649_, p_253694_).setColor(0, 255, 0, 0);
    }

    @Override
    public void serverInit(TCKingCreeper creeper) {

    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        float f5 = ((float) swell + renderTick) / 100.0F;
        float f7 = Math.min(f5 > 0.8F ? (f5 - 0.8F) / 0.2F : 0.0F, 1.0F);
        RandomSource randomsource = RandomSource.create(432L);
        VertexConsumer vertexconsumer2 = bufferSource.getBuffer(RenderType.lightning());
        pose.pushPose();
        pose.translate(0.0F, 0.6F, 0F);

        for (int i = 0; (float) i < (f5 + f5 * f5) / 2.0F * 60.0F; i++) {
            pose.mulPose(Axis.XP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            pose.mulPose(Axis.YP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            pose.mulPose(Axis.ZP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            pose.mulPose(Axis.XP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            pose.mulPose(Axis.YP.rotationDegrees(randomsource.nextFloat() * 360.0F));
            pose.mulPose(Axis.ZP.rotationDegrees(randomsource.nextFloat() * 360.0F + f5 * 90.0F));
            float f3 = randomsource.nextFloat() * 20.0F + 5.0F + f7 * 10.0F;
            float f4 = randomsource.nextFloat() * 2.0F + 1.0F + f7 * 2.0F;
            Matrix4f matrix4f = pose.last().pose();
            int j = (int) (255.0F * (1.0F - f7));
            vertex01(vertexconsumer2, matrix4f, j);
            vertex2(vertexconsumer2, matrix4f, f3, f4);
            vertex3(vertexconsumer2, matrix4f, f3, f4);
            vertex01(vertexconsumer2, matrix4f, j);
            vertex3(vertexconsumer2, matrix4f, f3, f4);
            vertex4(vertexconsumer2, matrix4f, f3, f4);
            vertex01(vertexconsumer2, matrix4f, j);
            vertex4(vertexconsumer2, matrix4f, f3, f4);
            vertex2(vertexconsumer2, matrix4f, f3, f4);
        }

        pose.popPose();
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(), creeper.getY() + 0.5, creeper.getZ(), 10f, false);
        creeper.hurt(creeper.damageSources().mobAttack(creeper), 10f);
        TCEntityUtils.setThunder(creeper.level());
    }

    @Override
    public void serverExpEvent(TCKingCreeper creeper, ExplosionEvent.Detonate event) {
        super.serverExpEvent(creeper, event);
        event.getAffectedBlocks().clear();
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.isBlocking() && livingEntity.getUseItem().is(TCItemCore.EXPLOSIVE_SHIELDS)) {
                    livingEntity.getUseItem().hurtAndBreak(10, creeper, null);
                    livingEntity.stopUsingItem();
                } else {
                    ((LivingEntity) entity).knockback(25, creeper.getX() - entity.getX(), creeper.getZ() - entity.getZ());
                    if (!livingEntity.isInvulnerable()) {
                        if (livingEntity.getHealth() < 2) {
                            entity.hurt(creeper.damageSources().genericKill(), livingEntity.getHealth() + 1);
                        } else {
                            entity.hurt(creeper.damageSources().genericKill(), livingEntity.getHealth() - 1);
                        }
                    }
                }
            }
        });
        event.getAffectedEntities().clear();
    }
}
