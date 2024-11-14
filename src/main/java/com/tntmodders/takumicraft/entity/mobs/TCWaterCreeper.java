package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCWaterCreeper extends AbstractTCCreeper {

    public TCWaterCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.WATER;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> this.level().setBlock(pos, Blocks.WATER.defaultBlockState(), 3));
        event.getAffectedBlocks().clear();
    }

    public static class TCWaterCreeperContext implements TCCreeperContext<TCWaterCreeper> {
        private static final String NAME = "watercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCWaterCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "みずたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Water flood, don't stay in water! To breathe, you have to swim up.";
        }

        @Override
        public String getJaJPDesc() {
            return "水で閉じ込めれば、もう溺れる他無い。バケツを持っていこう。";
        }

        @Override
        public String getEnUSName() {
            return "Water Creeper";
        }

        @Override
        public String getJaJPName() {
            return "水匠";
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
            return 0x0000ff;
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
