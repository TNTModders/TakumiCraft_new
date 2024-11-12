package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCZombieVillagerModel;
import com.tntmodders.takumicraft.client.renderer.entity.layer.TCCreeperPowerLayer;
import com.tntmodders.takumicraft.client.renderer.entity.state.TCZombieCreeperRenderState;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCMaceCreeper;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

import java.io.IOException;
import java.util.Optional;

public class TCMaceCreeperRenderer<T extends TCMaceCreeper, S extends TCZombieCreeperRenderState, M extends TCZombieVillagerModel<T>> extends HumanoidMobRenderer<T, M> {

    public TCMaceCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, (M) new TCZombieVillagerModel<T>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new TCZombieVillagerModel<>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)),
                new TCZombieVillagerModel<>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new MaceCreeperProfessionLayer<>(this, context.getResourceManager(), "zombie_villager"));
        this.addLayer(new TCCreeperPowerLayer(this, context.getModelSet(), new TCZombieVillagerModel<>(context.bakeLayer(ModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)),
                TCEntityCore.ZOMBIE_VILLAGER));
    }

    @Override
    public ResourceLocation getTextureLocation(S creeper) {
        return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.context.entityType().toShortString() + ".png");
    }

    @Override
    protected void scale(T p_114046_, PoseStack p_114047_, float p_114048_) {
        TCClientUtils.scaleSwelling(p_114046_, p_114047_, p_114048_);
    }

    @Override
    protected float getWhiteOverlayProgress(T p_114043_, float p_114044_) {
        float f = p_114043_.getSwelling(p_114044_);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    public static class MaceCreeperProfessionLayer<T extends LivingEntity, M extends EntityModel<T> & VillagerHeadModel> extends RenderLayer<T, M> {
        private static final ResourceLocation LEVEL_LOCATION = ResourceLocation.withDefaultNamespace("diamond");
        private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache = new Object2ObjectOpenHashMap<>();
        private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache = new Object2ObjectOpenHashMap<>();
        private final ResourceManager resourceManager;
        private final String path;

        public MaceCreeperProfessionLayer(RenderLayerParent<T, M> p_174550_, ResourceManager p_174551_, String p_174552_) {
            super(p_174550_);
            this.resourceManager = p_174551_;
            this.path = p_174552_;
        }

        @Override
        public void render(PoseStack p_117646_, MultiBufferSource p_117647_, int p_117648_, T p_117649_, float p_117650_, float p_117651_, float p_117652_, float p_117653_, float p_117654_, float p_117655_) {
            if (!p_117649_.isInvisible()) {
                VillagerType villagertype = VillagerType.PLAINS;
                VillagerProfession villagerprofession = VillagerProfession.CLERIC;
                VillagerMetaDataSection.Hat villagermetadatasection$hat = this.getHatData(this.typeHatCache, "type", BuiltInRegistries.VILLAGER_TYPE, villagertype);
                VillagerMetaDataSection.Hat villagermetadatasection$hat1 = this.getHatData(
                        this.professionHatCache, "profession", BuiltInRegistries.VILLAGER_PROFESSION, villagerprofession
                );
                M m = this.getParentModel();
                m.hatVisible(
                        villagermetadatasection$hat1 == VillagerMetaDataSection.Hat.NONE
                                || villagermetadatasection$hat1 == VillagerMetaDataSection.Hat.PARTIAL && villagermetadatasection$hat != VillagerMetaDataSection.Hat.FULL
                );
                ResourceLocation resourcelocation = this.getResourceLocation("type", BuiltInRegistries.VILLAGER_TYPE.getKey(villagertype));
                renderColoredCutoutModel(m, resourcelocation, p_117646_, p_117647_, p_117648_, p_117649_, 1);
                m.hatVisible(true);
                if (villagerprofession != VillagerProfession.NONE && !p_117649_.isBaby()) {
                    ResourceLocation resourcelocation1 = this.getResourceLocation("profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerprofession));
                    renderColoredCutoutModel(m, resourcelocation1, p_117646_, p_117647_, p_117648_, p_117649_, 1);
                    if (villagerprofession != VillagerProfession.NITWIT) {
                        ResourceLocation resourcelocation2 = this.getResourceLocation("profession_level", LEVEL_LOCATION);
                        renderColoredCutoutModel(m, resourcelocation2, p_117646_, p_117647_, p_117648_, p_117649_, 1);
                    }
                }
            }
        }

        private ResourceLocation getResourceLocation(String p_117669_, ResourceLocation p_117670_) {
            return p_117670_.withPath(p_247944_ -> "textures/entity/" + this.path + "/" + p_117669_ + "/" + p_247944_ + ".png");
        }

        public <K> VillagerMetaDataSection.Hat getHatData(Object2ObjectMap<K, VillagerMetaDataSection.Hat> p_117659_, String p_117660_, DefaultedRegistry<K> p_117661_, K p_117662_) {
            return p_117659_.computeIfAbsent(
                    p_117662_, p_258159_ -> this.resourceManager.getResource(this.getResourceLocation(p_117660_, p_117661_.getKey(p_117662_))).flatMap(p_234875_ -> {
                        try {
                            return p_234875_.metadata().getSection(VillagerMetaDataSection.SERIALIZER).map(VillagerMetaDataSection::getHat);
                        } catch (IOException ioexception) {
                            return Optional.empty();
                        }
                    }).orElse(VillagerMetaDataSection.Hat.NONE)
            );
        }
    }
}