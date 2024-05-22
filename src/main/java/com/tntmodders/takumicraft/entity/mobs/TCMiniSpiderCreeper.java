package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCSpiderCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class TCMiniSpiderCreeper extends TCSpiderCreeper {
    public TCMiniSpiderCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.MINI_SPIDER;
    }

    public static class TCMiniSpiderCreeperContext implements TCCreeperContext<TCMiniSpiderCreeper> {
        private static final String NAME = "minispidercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCMiniSpiderCreeper::new, MobCategory.MONSTER).sized(1.4F / 4, 0.9F / 4).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "こぐもたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Little tiny spider creeper. How is the explosion? Of course big.";
        }

        @Override
        public String getJaJPDesc() {
            return "蜘蛛匠から産まれるこの匠は、隙間からでもリフォームするお邪魔虫。";
        }

        @Override
        public String getEnUSName() {
            return "Mini Spider Creeper";
        }

        @Override
        public String getJaJPName() {
            return "子蜘蛛匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 1334334;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.6F);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCSpiderCreeper>) type, TCSpiderCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.FERMENTED_SPIDER_EYE;
        }
    }
}
