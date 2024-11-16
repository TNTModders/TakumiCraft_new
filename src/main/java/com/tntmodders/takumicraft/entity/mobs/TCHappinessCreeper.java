package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCHappinessCreeper extends AbstractTCCreeper {

    public TCHappinessCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.HAPPINESS;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setHealth(livingEntity.getMaxHealth());
                livingEntity.removeAllEffects();
            }
        });
        event.getAffectedEntities().clear();
    }

    public static class TCHappinessCreeperContext implements TCCreeperContext<TCHappinessCreeper> {
        private static final String NAME = "happinesscreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCHappinessCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getJaJPRead() {
            return "ほほえみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Heal or destroy? This cause a huge explosion heal you.";
        }

        @Override
        public String getJaJPDesc() {
            return "回復効果のある大爆発を起こす。読みはどうしてこうなった。";
        }

        @Override
        public String getEnUSName() {
            return "Happiness Creeper";
        }

        @Override
        public String getJaJPName() {
            return "微匠";
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
            return 16711935;
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
