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

public class TCRedstoneCreeper extends AbstractTCCreeper {

    public TCRedstoneCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.REDSTONE;
    }

    public static class TCRedstoneCreeperContext implements TCCreeperContext<TCRedstoneCreeper> {
        private static final String NAME = "redstonecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCRedstoneCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "れっどすとーんたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with redstone speed. Too fast, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "素早い動きと高速な爆発、レッドストーンの匠は止まらない。";
        }

        @Override
        public String getEnUSName() {
            return "RedStone Creeper";
        }

        @Override
        public String getJaJPName() {
            return "RS匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xa09090;
        }

        @Override
        public int getSecondaryColor() {
            return 0xff0000;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 10).add(Attributes.MOVEMENT_SPEED, 0.65);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.FIRE;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.REDSTONE_BLOCK;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1.0F, 1.0F);
        }
    }
}
