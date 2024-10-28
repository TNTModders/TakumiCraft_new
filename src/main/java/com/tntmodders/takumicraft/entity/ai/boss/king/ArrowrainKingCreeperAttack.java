package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.entity.projectile.TCCreeperArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArrowrainKingCreeperAttack extends AbstractKingCreeperAttack {

    private LivingEntity target;
    private static final ItemStack stack = new ItemStack(TCItemCore.CREEPER_ARROW);
    private static final ItemStack bow = new ItemStack(TCItemCore.CREEPER_BOW);

    @Override
    public void serverInit(TCKingCreeper creeper) {
        creeper.startUsingItem(InteractionHand.MAIN_HAND);
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
        commonTick(creeper, swell);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        commonTick(creeper, swell);
        creeper.setUseItem(bow);
        pose.pushPose();
        float f = creeper.getSwelling(renderTick);
        f = Math.clamp(f, 0.0F, 1.0F) * 3;
        pose.translate(0, 0.25 - f / 1.25, 0);
        pose.rotateAround(Axis.YP.rotationDegrees(90f), 0.0f, 0.0f, 0.0f);
        pose.rotateAround(Axis.ZP.rotationDegrees(-135.0f), 0.0f, 0.0f, 1.0f);
        pose.scale(f, f, f);
        Minecraft.getInstance().getItemRenderer().renderStatic(creeper, bow, ItemDisplayContext.NONE, false, pose, bufferSource, creeper.level(), 0xffffff, LivingEntityRenderer.getOverlayCoords(state, 0.0F), creeper.getId());
        pose.popPose();
    }

    private void commonTick(TCKingCreeper creeper, int swell) {
        if (swell < creeper.maxSwell - 20) {
            if (this.target != null) {
                creeper.lookAt(this.target, 90f, 90f);
            } else {
                LivingEntity entity = creeper.getTarget() != null ? creeper.getTarget() : creeper.level().getNearestPlayer(creeper, 32);
                if (entity != null) {
                    this.target = entity;
                }
            }
            creeper.setXRot(-15);
        }
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        for (int i = 0; i < (creeper.isPowered() ? 300 : 150); i++) {
            TCCreeperArrow arrow = new TCCreeperArrow(creeper.level(), creeper, stack, null);
            arrow.shootFromRotation(creeper, (float) (-70 - creeper.getRandom().nextGaussian() * 10f), creeper.getYRot(), 1.7f, (float) (0.25f + creeper.getRandom().nextGaussian()) * 2.5f, 4f);
            arrow.setDest(true);
            arrow.setPower(creeper.isPowered() ? 4f : 2f);
            arrow.setPos(creeper.getRandomX(15), creeper.getRandomY(5) + 1.7, creeper.getRandomZ(15));
            creeper.level().addFreshEntity(arrow);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientExp(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, float renderTick) {
        super.clientExp(state, creeper, swell, pose, renderTick);
        creeper.setUseItem(ItemStack.EMPTY);
    }
}
