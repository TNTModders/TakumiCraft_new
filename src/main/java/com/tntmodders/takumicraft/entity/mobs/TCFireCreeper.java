package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCFireCreeper extends AbstractTCCreeper {

    public TCFireCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.FIRE;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> {
            if (pos.getY() > event.getExplosion().center().y() - 0.5) {
                this.level().setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
        });
        event.getExplosion().clearToBlow();
    }

    public static class TCFireCreeperContext implements TCCreeperContext<TCFireCreeper> {
        private static final String NAME = "firecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCFireCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "はっかたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "As soon as it explode, the forest is going to be burnt. Stop it!";
        }

        @Override
        public String getJaJPDesc() {
            return "火がついてしまってはもう遅い。突然現れては森を焦土に変える。";
        }

        @Override
        public String getEnUSName() {
            return "Fire Creeper";
        }

        @Override
        public String getJaJPName() {
            return "発火匠";
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
            return 0xff8888;
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
