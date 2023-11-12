package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCFallCreeper extends AbstractTCCreeper {

    public TCFallCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 15;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.FALL;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> {
            if (!event.getLevel().getBlockState(pos).isAir()) {
                event.getLevel().setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 3);
            }
        });
        event.getExplosion().clearToBlow();
        event.getAffectedEntities().clear();
    }

    public static class TCFallCreeperContext implements TCCreeperContext<TCFallCreeper> {
        private static final String NAME = "fallcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCFallCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "らくしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "All things fallen. Blocks, entities, even this game fall and shut down...";
        }

        @Override
        public String getJaJPDesc() {
            return "=世界は砂利に包まれた。大地も、生物も、ゲームすら落ちる世界へと。";
        }

        @Override
        public String getEnUSName() {
            return "Fall Creeper";
        }

        @Override
        public String getJaJPName() {
            return "落匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xffdddd;
        }

        @Override
        public int getSecondaryColor() {
            return 0x553333;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
