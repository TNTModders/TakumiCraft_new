package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCFallingBombCreeper extends AbstractTCCreeper {

    public TCFallingBombCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 15;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.FALLING_BOMB;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> {
            if (!event.getLevel().getBlockState(pos).isAir()) {
                event.getLevel().setBlock(pos, TCBlockCore.FALLING_BOMB.defaultBlockState(), 3);
            }
        });
        event.getExplosion().clearToBlow();
        event.getAffectedEntities().clear();
    }

    @Override
    public float getBlockExplosionResistance(Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_) {
        return super.getBlockExplosionResistance(p_19992_, p_19993_, p_19994_, p_19995_, p_19996_, p_19997_) / 10;
    }

    public static class TCFallingBombCreeperContext implements TCCreeperContext<TCFallingBombCreeper> {
        private static final String NAME = "fallingbombcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCFallingBombCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "あじゃりたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Not a gravel, but tons of falling bomb.";
        }

        @Override
        public String getJaJPDesc() {
            return "阿闍梨の修行は落ち来る災厄、大地を埋めよ、全てを崩せ。";
        }

        @Override
        public String getEnUSName() {
            return "Falling Bomb Creeper";
        }

        @Override
        public String getJaJPName() {
            return "阿闍梨匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x000055;
        }

        @Override
        public int getSecondaryColor() {
            return 0x002200;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.GRAVEL;
        }
    }
}
