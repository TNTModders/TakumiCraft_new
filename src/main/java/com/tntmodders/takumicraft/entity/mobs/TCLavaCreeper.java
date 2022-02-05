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
import net.minecraftforge.event.world.ExplosionEvent;

public class TCLavaCreeper extends AbstractTCCreeper {

    public TCLavaCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.LAVA;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> this.level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 2));
        event.getExplosion().clearToBlow();
    }

    public static class TCLavatCreeperContext implements TCCreeperContext<TCLavaCreeper> {
        private static final String NAME = "lavacreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = ((EntityType<? extends AbstractTCCreeper>) EntityType.Builder
                .of(TCLavaCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME).setRegistryName(TakumiCraftCore.MODID, NAME));

        @Override
        public String getJaJPRead() {
            return "ようがんたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Lava press you like Tsunami, not to be burnt, you must cold with water.";
        }

        @Override
        public String getJaJPDesc() {
            return "熔岩で閉じ込めれば、もう帰ることは無い。水を持っていこう。";
        }

        @Override
        public String getEnUSName() {
            return "Lava Creeper";
        }

        @Override
        public String getJaJPName() {
            return "熔岩匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 39168;
        }

        @Override
        public int getSecondaryColor() {
            return 16711680;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes();
        }

        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer(((EntityType<Creeper>) type), TCCreeperRenderer::new);
        }

        @Override
        public int getRegisterID() {
            return 6;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.FIRE;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
