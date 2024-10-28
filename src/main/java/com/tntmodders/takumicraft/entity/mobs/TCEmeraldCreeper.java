package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class TCEmeraldCreeper extends AbstractTCCreeper {

    public TCEmeraldCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.EMERALD;
    }

    public static class TCEmeraldCreeperContext implements TCCreeperContext<TCEmeraldCreeper> {
        private static final String NAME = "emeraldcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCEmeraldCreeper::new, MobCategory.MONSTER).sized(1.2F, 3.4F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "えめらるどたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with emerald toughness. Too big, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "巨大な体躯と強力な耐久、エメラルドの匠は倒れない。";
        }

        @Override
        public String getEnUSName() {
            return "Emerald Creeper";
        }

        @Override
        public String getJaJPName() {
            return "エメラルド匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x90a090;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00ff00;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 80).add(Attributes.MOVEMENT_SPEED, 0.175);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.EMERALD_BLOCK;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1.0F, 1.0F);
        }

        @Override
        public float getSizeFactor() {
            return 2f;
        }
    }
}
