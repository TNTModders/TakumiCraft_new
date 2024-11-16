package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.ExplosionEvent;

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
        event.getAffectedBlocks().forEach(pos -> this.level().setBlock(pos, Blocks.LAVA.defaultBlockState(), 3));
        event.getAffectedBlocks().clear();
    }

    public static class TCLavaCreeperContext implements TCCreeperContext<TCLavaCreeper> {
        private static final String NAME = "lavacreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCLavaCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

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
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.FIRE;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
