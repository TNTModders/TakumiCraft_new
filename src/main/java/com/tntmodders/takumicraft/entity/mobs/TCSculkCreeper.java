package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCSculkCreeper extends AbstractTCCreeper {

    public TCSculkCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public float getBlockExplosionResistance(Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_) {
        return super.getBlockExplosionResistance(p_19992_, p_19993_, p_19994_, p_19995_, p_19996_, p_19997_) / 10;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> {
            if (this.level().getBlockState(pos).isAir()) {
                this.level().setBlock(pos, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
            }
        });
        event.getAffectedBlocks().clear();
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SCULK;
    }

    public static class TCSclukCreeperContext implements TCCreeperContext<TCSculkCreeper> {
        private static final String NAME = "sculkcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCSculkCreeper::new, MobCategory.MONSTER).sized(0.6f * 0.75f, 1.7f * 0.75f).clientTrackingRange(8).fireImmune().build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "すかるくたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with sculk erosion. Too deep, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "太古の力と侵食の闇、地底の匠は滅びない。";
        }

        @Override
        public String getEnUSName() {
            return "Sculk Creeper";
        }

        @Override
        public String getJaJPName() {
            return "スカルク匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x8800dd;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00ffff;
        }

        @Override
        public float getSizeFactor() {
            return 1.25f;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 50).add(Attributes.KNOCKBACK_RESISTANCE, 1000000).add(Attributes.JUMP_STRENGTH, 0.5);
        }

        @Override
        public void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (biome.is(Biomes.DEEP_DARK)) {
                TCCreeperContext.super.registerModifierSpawn(biome, builder);
            }
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.SCULK_CATALYST;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(0.0F, 1.0F);
        }
    }
}
