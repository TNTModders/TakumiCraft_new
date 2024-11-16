package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class TCWeatherCreeper extends AbstractTCCreeper {

    public TCWeatherCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.WEATHER;
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        TCEntityUtils.setThunder(this.level());
    }

    public static class TCWeatherCreeperContext implements TCCreeperContext<TCWeatherCreeper> {
        private static final String NAME = "weathercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCWeatherCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "てんたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "The weather changes to thundering, you know how it mean.";
        }

        @Override
        public String getJaJPDesc() {
            return "天候を司り、雷雨を起こす匠。聡明ならばこの意味は歴然であろう。";
        }

        @Override
        public String getEnUSName() {
            return "Weather Creeper";
        }

        @Override
        public String getJaJPName() {
            return "天匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00ff00;
        }

        @Override
        public int getPrimaryColor() {
            return 0x000099;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
