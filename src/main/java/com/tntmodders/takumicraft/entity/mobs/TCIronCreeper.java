package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
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

public class TCIronCreeper extends AbstractTCCreeper {

    public TCIronCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.IRON;
    }

    public static class TCIronCreeperContext implements TCCreeperContext<TCIronCreeper> {
        private static final String NAME = "ironcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCIronCreeper::new, MobCategory.MONSTER).sized(0.6F / 2, 1.7F / 2).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "はがねたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with iron compactness. Too small, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "小型な体躯と俊敏な移動、鋼の匠は当たらない。";
        }

        @Override
        public String getEnUSName() {
            return "Iron Creeper";
        }

        @Override
        public String getJaJPName() {
            return "鋼匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x909090;
        }

        @Override
        public int getSecondaryColor() {
            return 0xffffff;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.KNOCKBACK_RESISTANCE, 0.01).add(Attributes.MAX_HEALTH, 10)
                    .add(Attributes.MOVEMENT_SPEED, 0.75);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.IRON_BLOCK;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1.0F, 1.0F);
        }

        @Override
        public float getSizeFactor() {
            return 0.5f;
        }
    }
}
