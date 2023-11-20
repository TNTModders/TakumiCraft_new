package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCGlassCreeper extends AbstractTCCreeper {

    public TCGlassCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.GLASS;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> this.level().setBlock(pos, TCBlockCore.CREEPER_GLASS.defaultBlockState(), 3));
        event.getExplosion().clearToBlow();
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
    }

    public static class TCGlassCreeperContext implements TCCreeperContext<TCGlassCreeper> {
        private static final String NAME = "glasscreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCGlassCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "がらすたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Anti-Explosion glass for you! An old-fashioned transparent creeper.";
        }

        @Override
        public String getJaJPDesc() {
            return "透明で見えづらい、古い見た目の匠。最新式の硬質硝子を貴方にどうぞ。";
        }

        @Override
        public String getEnUSName() {
            return "Glass Creeper";
        }

        @Override
        public String getJaJPName() {
            return "硝子匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 14548991;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCBlockCore.CREEPER_GLASS;
        }
    }
}
