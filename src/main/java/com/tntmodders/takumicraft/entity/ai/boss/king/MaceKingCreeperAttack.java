package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MaceKingCreeperAttack extends AbstractKingCreeperAttack {
    private static final int tick = 20;
    private static final ItemStack mace = new ItemStack(TCItemCore.CREEPER_MACE);

    @Override
    public void serverInit(TCKingCreeper creeper) {

    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {
        if (swell == creeper.maxSwell - tick) {
            creeper.move(MoverType.SELF, new Vec3(0, 40, 0));
            creeper.setDeltaMovement(0, -10, 0);
        } else if (swell > creeper.maxSwell - tick) {
            TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(), creeper.getY(), creeper.getZ(), creeper.isPowered() ? 8f : 5f, false);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {
        pose.pushPose();
        float f = creeper.getSwelling(renderTick);
        f = Math.clamp(f, 0.0F, 1.0F) * 10;
        if (swell > creeper.maxSwell - tick) {
            float deg = 360f / 20 * (swell - 20 + renderTick);
            pose.translate(0, 1, 0);
            pose.rotateAround(Axis.XP.rotationDegrees(deg), 1.0f, 0.0f, 0.0f);
            pose.rotateAround(Axis.YP.rotationDegrees(90), 0.0f, 1.0f, 0.0f);
        } else {
            pose.translate(0, 0.25 - f / 1.25, 0);
            pose.rotateAround(Axis.ZP.rotationDegrees(-135.0f), 0.0f, 0.0f, 1.0f);
        }
        pose.scale(f, f, f);
        Minecraft.getInstance().getItemRenderer().renderStatic(creeper, mace, ItemDisplayContext.NONE, false, pose, bufferSource, creeper.level(), 0xffffff, LivingEntityRenderer.getOverlayCoords(creeper, 0.0F), creeper.getId());
        pose.popPose();
    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
        TCExplosionUtils.createExplosion(creeper.level(), creeper, creeper.getX(), creeper.getY(), creeper.getZ(), creeper.isPowered() ? 10f : 7f, false);
    }
}
