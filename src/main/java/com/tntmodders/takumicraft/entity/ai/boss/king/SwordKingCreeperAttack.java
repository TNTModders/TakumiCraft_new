package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class SwordKingCreeperAttack extends AbstractKingCreeperAttack {
    private static final ItemStack sword = new ItemStack(TCItemCore.CREEPER_SWORD);

    @Override
    public void serverInit(TCKingCreeper creeper) {

    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
        if (swell > creeper.maxSwell - 6) {
            AABB aabb = creeper.getBoundingBox().inflate(10, 0, 10).contract(0, 0.6, 0);
            List<Entity> list = creeper.level().getEntities(creeper, aabb);
            if (!list.isEmpty()) {
                list.forEach(entity -> {
                    if (entity instanceof LivingEntity && !entity.isInvulnerable()) {
                        entity.hurt(creeper.damageSources().mobAttack(creeper), 18);
                    }
                });
            }
            int t = 50 - swell;
            for (int i = 0; i <= 10; i++) {
                double x = creeper.getX() + Math.cos(Math.toRadians(60 * t)) * i;
                double z = creeper.getZ() + Math.sin(Math.toRadians(60 * t)) * i;
                TCExplosionUtils.createExplosion(creeper.level(), creeper, x, creeper.getY(), z, 0f, false);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(LivingEntityRenderState state, TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        pose.pushPose();
        float f = creeper.getSwelling(renderTick);
        f = Math.clamp(f, 0.0F, 1.0F) * 10;
        if (swell > creeper.maxSwell - 6) {
            float deg = 360f / 6 * (swell - 44 + renderTick);
            pose.translate(0, 1, 0);
            pose.rotateAround(Axis.XP.rotationDegrees(90), 1.0f, 0.0f, 0.0f);
            pose.rotateAround(Axis.ZP.rotationDegrees(deg), 0.0f, 0.0f, 1.0f);
        } else {
            pose.translate(0, 0.25 - f / 1.25, 0);
            pose.rotateAround(Axis.ZP.rotationDegrees(-135.0f), 0.0f, 0.0f, 1.0f);
        }
        pose.scale(f, f, f);
        Minecraft.getInstance().getItemRenderer().renderStatic(creeper, sword, ItemDisplayContext.NONE, false, pose, bufferSource, creeper.level(), 0xffffff, LivingEntityRenderer.getOverlayCoords(state, 0.0F), creeper.getId());
        pose.popPose();
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {

    }
}
