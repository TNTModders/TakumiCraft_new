package com.tntmodders.takumicraft.utils.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.*;
import com.tntmodders.takumicraft.entity.mobs.boss.TCRavagerCreeper;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TCClientUtils {
    public static final ResourceLocation POWER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");

    public static void scaleSwelling(float f, PoseStack poseStack) {
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderEntity(PoseStack posestack, double x, double y, int size, float yrot, float xrot, EntityType<?> entityType, boolean isOutline) {
        if (entityType.create(Minecraft.getInstance().level) instanceof AbstractTCCreeper creeper) {
            creeper.setOnBook(true);
            posestack.pushPose();
            posestack.translate(x, y, 1050.0D);
            posestack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            PoseStack posestack1 = posestack;
            posestack1.translate(0.0D, 0.0D, 1000.0D);
            posestack1.scale((float) size, (float) size, (float) size);
            float invscale = 1 / creeper.getContext().getSizeFactor();
            posestack1.scale(invscale, invscale, invscale);
            Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
            Quaternionf quaternion1 = Axis.XP.rotationDegrees(xrot * 20.0F);
            quaternion.mul(quaternion1);
            posestack1.mulPose(quaternion);
            float f2 = creeper.yBodyRot;
            float f3 = creeper.getYRot();
            float f4 = creeper.getXRot();
            float f5 = creeper.yHeadRotO;
            float f6 = creeper.yHeadRot;
            creeper.yBodyRot = 180.0F + yrot * 20.0F;
            creeper.setYRot(180.0F + yrot * 40.0F);
            creeper.setXRot(-xrot * 20.0F);
            creeper.yHeadRot = creeper.getYRot();
            creeper.yHeadRotO = creeper.getYRot();
            Lighting.setupForEntityInInventory();
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            quaternion1.conjugate();
            entityrenderdispatcher.overrideCameraOrientation(quaternion1);
            entityrenderdispatcher.setRenderShadow(false);
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            renderEntitySP(creeper, posestack1, isOutline);
            entityrenderdispatcher.render(creeper, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F,
                    posestack1, multibuffersource$buffersource, checkSlayAdv(entityType) ? 0xf000f0 : -10000);
            multibuffersource$buffersource.endBatch();
            entityrenderdispatcher.setRenderShadow(true);
            creeper.yBodyRot = f2;
            creeper.setYRot(f3);
            creeper.setXRot(f4);
            creeper.yHeadRotO = f5;
            creeper.yHeadRot = f6;
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
        }
    }

    private static void renderEntitySP(Entity entity, PoseStack posestack, boolean isOutline) {
        if (entity instanceof TCZombieVillagerCreeper creeper) {
            creeper.setVillagerData(new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 0));
        } else if (entity instanceof TCSquidCreeper creeper) {
            posestack.scale(0.65f, 0.65f, 0.65f);
            posestack.translate(-0.5, 1.1, 0);
            posestack.rotateAround(Axis.ZP.rotationDegrees(10), 0, 1, 0);
        } else if (!isOutline && entity instanceof TCHorseCreeper creeper) {
            posestack.scale(0.85f, 0.85f, 0.85f);
            posestack.translate(-0.3, 0, 0);
        } else if (entity instanceof TCWitherSkeletonCreeper creeper) {
            creeper.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TCItemCore.CREEPER_SWORD));
        } else if (entity instanceof TCSkeletonCreeper creeper) {
            creeper.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TCItemCore.CREEPER_BOW));
        } else if (entity instanceof TCMaceCreeper creeper) {
            creeper.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TCItemCore.CREEPER_MACE));
        } else if (entity instanceof TCRavagerCreeper creeper) {
            posestack.scale(0.5f, 0.5f, 0.5f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean checkSlayAdv(EntityType entity) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientAdvancements advancements = player.connection.getAdvancements();
        AdvancementProgress progress = advancements.progress.get(advancements.get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "slay/slay_" + entity.toShortString())));
        return progress != null && progress.isDone();
    }

    @OnlyIn(Dist.CLIENT)
    public static Pair<Integer, Integer> checkSlayAllAdv() {
        try {
            LocalPlayer player = Minecraft.getInstance().player;
            ClientAdvancements advancements = player.connection.getAdvancements();
            AdvancementProgress progress = advancements.progress.get(advancements.get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "slay_all")));
            int size = ((List<?>) progress.getCompletedCriteria()).size();
            int allSize = ((List<?>) progress.getRemainingCriteria()).size() + size;
            return Pair.of(size, allSize);
        } catch (Exception ignored) {
        }
        return Pair.of(0, 0);
    }
}
