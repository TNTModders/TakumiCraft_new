package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class TCSilentCreeper extends AbstractTCCreeper {

    public TCSilentCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SILENT;
    }

    @Override
    protected float getSoundVolume() {
        return 0f;
    }

    public static class TCSilentCreeperContext implements TCCreeperContext<TCSilentCreeper> {
        private static final String NAME = "silentcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCSilentCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "さいれんとたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "It came nearby you, without sound. It is too late if you notice.";
        }

        @Override
        public String getJaJPDesc() {
            return "物音を立てず、暗闇に潜み、後から急襲する。気づいたときには、すでに爆音。";
        }

        @Override
        public String getEnUSName() {
            return "Silent Creeper";
        }

        @Override
        public String getJaJPName() {
            return "サイレント匠";
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
            return 11184810;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
