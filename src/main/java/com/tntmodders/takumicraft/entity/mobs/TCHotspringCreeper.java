package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCHotspringCreeper extends AbstractTCCreeper {

    public TCHotspringCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.HOTSPRING;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> this.level().setBlock(pos, TCBlockCore.HOTSPRING.defaultBlockState(), 3));
        event.getExplosion().clearToBlow();
    }

    public static class TCHotspringCreeperContext implements TCCreeperContext<TCHotspringCreeper> {
        private static final String NAME = "hotspringcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCHotspringCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ゆたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Thanks for hot spring, this creeper makes you relax.";
        }

        @Override
        public String getJaJPDesc() {
            return "温泉を湧かせる匠。傷も癒え、空腹も消え行く。ありがたや。";
        }

        @Override
        public String getEnUSName() {
            return "Hotspring Creeper";
        }

        @Override
        public String getJaJPName() {
            return "湯匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 16777215;
        }


        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WATER;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
