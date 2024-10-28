package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCSandCreeper extends AbstractTCCreeper {

    public TCSandCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SAND;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> this.level().setBlock(pos, Blocks.SAND.defaultBlockState(), 3));
        event.getExplosion().clearToBlow();
    }

    public static class TCSandCreeperContext implements TCCreeperContext<TCSandCreeper> {
        private static final String NAME = "sandcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCSandCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "すなたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Explosion sandwich you in sand, soon only your items rise up.";
        }

        @Override
        public String getJaJPDesc() {
            return "砂で埋め立てれば、もう逃げるすべは無い。松明を持っていこう。";
        }

        @Override
        public String getEnUSName() {
            return "Sand Creeper";
        }

        @Override
        public String getJaJPName() {
            return "砂匠";
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
            return 12303206;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
