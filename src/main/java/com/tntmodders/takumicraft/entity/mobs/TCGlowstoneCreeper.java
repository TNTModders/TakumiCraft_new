package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCGlowstoneCreeper extends AbstractTCCreeper {

    public TCGlowstoneCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.GLOWSTONE;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> this.level().setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3));
        event.getExplosion().clearToBlow();
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public static class TCGlowstoneCreeperContext implements TCCreeperContext<TCGlowstoneCreeper> {
        private static final String NAME = "glowstonecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCGlowstoneCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ぐろうすとーんたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Lighting up nether, this creeper make a light by the explosion.";
        }

        @Override
        public String getJaJPDesc() {
            return "爆発でグロウストーンを生み出す、ネザーの匠。明るい。";
        }

        @Override
        public String getEnUSName() {
            return "Glowstone Creeper";
        }

        @Override
        public String getJaJPName() {
            return "グロウストーン匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 16776960;
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

        @Override
        public ItemLike getMainDropItem() {
            return Items.GLOWSTONE_DUST;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1, 8);
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(TCEntityCore.NETHER_TAKUMIS);
            return list;
        }
    }
}
