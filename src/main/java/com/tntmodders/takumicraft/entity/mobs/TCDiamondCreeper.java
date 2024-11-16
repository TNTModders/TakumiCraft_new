package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class TCDiamondCreeper extends AbstractTCCreeper {

    public TCDiamondCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.DIAMOND;
    }

    public static class TCDiamondCreeperContext implements TCCreeperContext<TCDiamondCreeper> {
        private static final String NAME = "diamondcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCDiamondCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "だいやもんどたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with diamond heart. Too hard, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "硬い精神と強靭な肉体、ダイヤの匠は砕けない。";
        }

        @Override
        public String getEnUSName() {
            return "Diamond Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ダイヤモンド匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x9090a0;
        }

        @Override
        public int getSecondaryColor() {
            return 0x0000ff;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.KNOCKBACK_RESISTANCE, 1000000);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WATER;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.DIAMOND_BLOCK;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1.0F, 1.0F);
        }
    }
}
