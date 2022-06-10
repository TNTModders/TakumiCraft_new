package com.tntmodders.takumicraft.utils;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.TCZombieVillagerCreeper;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TCEntityUtils {
    public static Component getEntityName(EntityType<?> type) {
        return Component.translatable(type.getDescriptionId());
    }

    public static String getEntityLangCode(EntityType<?> type, @NotNull String suffix) {
        return "entity.takumicraft." + type.toShortString() + suffix;
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderEntity(double p_98851_, double p_98852_, int p_98853_, float f, float f1, EntityType<?> entityType) {
        if (entityType.create(Minecraft.getInstance().level) instanceof LivingEntity entity) {
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.translate(p_98851_, p_98852_, 1050.0D);
            posestack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            PoseStack posestack1 = new PoseStack();
            posestack1.translate(0.0D, 0.0D, 1000.0D);
            posestack1.scale((float) p_98853_, (float) p_98853_, (float) p_98853_);
            Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
            Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
            quaternion.mul(quaternion1);
            posestack1.mulPose(quaternion);
            float f2 = entity.yBodyRot;
            float f3 = entity.getYRot();
            float f4 = entity.getXRot();
            float f5 = entity.yHeadRotO;
            float f6 = entity.yHeadRot;
            entity.yBodyRot = 180.0F + f * 20.0F;
            entity.setYRot(180.0F + f * 40.0F);
            entity.setXRot(-f1 * 20.0F);
            entity.yHeadRot = entity.getYRot();
            entity.yHeadRotO = entity.getYRot();
            Lighting.setupForEntityInInventory();
            EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            quaternion1.conj();
            entityrenderdispatcher.overrideCameraOrientation(quaternion1);
            entityrenderdispatcher.setRenderShadow(false);
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            renderEntitySP(entity);
            entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F,
                    posestack1, multibuffersource$buffersource, TCEntityUtils.checkSlayAdv(entityType) ? 0xf000f0 : -1);
            multibuffersource$buffersource.endBatch();
            entityrenderdispatcher.setRenderShadow(true);
            entity.yBodyRot = f2;
            entity.setYRot(f3);
            entity.setXRot(f4);
            entity.yHeadRotO = f5;
            entity.yHeadRot = f6;
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
        }
    }

    private static void renderEntitySP(Entity entity) {
        if (entity instanceof TCZombieVillagerCreeper creeper) {
            creeper.setVillagerData(new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 0));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean checkSlayAdv(EntityType entity) {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientAdvancements advancements = player.connection.getAdvancements();
        AdvancementProgress progress = advancements.progress.get(advancements.getAdvancements().get(new ResourceLocation(TakumiCraftCore.MODID, "slay/slay_" + entity.toShortString())));
        return progress != null && progress.isDone();
    }
}
