package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.List;

public class TCBangCreeper extends AbstractTCCreeper {

    public TCBangCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BANG;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<BlockPos> posList = new ArrayList<>();
        event.getAffectedBlocks().forEach(blockPos -> {
            float f = this.level().getBlockState(blockPos).getExplosionResistance(this.level(), blockPos, event.getExplosion());
            if (f > 0.2 && event.getExplosion().radius() > 1) {
                this.level().destroyBlock(blockPos, true, this);
                TCExplosionUtils.createExplosion(this.level(), this, blockPos, Math.max(0, event.getExplosion().radius() - 0.2f - 2 / f));
                posList.add(blockPos);

            }
        });
        if (!posList.isEmpty()) {
            event.getAffectedBlocks().removeAll(posList);
        }
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState, float f) {
        return blockState.getDestroySpeed(level, pos) < 0 ? super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState, f) : 0.5f;
    }

    public static class TCBangCreeperContext implements TCCreeperContext<TCBangCreeper> {
        private static final String NAME = "bangcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBangCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ばくしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "This explosion can even destroy obsidian. There's no way to protect you.";
        }

        @Override
        public String getJaJPDesc() {
            return "強力な爆発は黒曜石をも破壊する。身を守る術なく、高密度破壊をもたらす。";
        }

        @Override
        public String getEnUSName() {
            return "Bang Creeper";
        }

        @Override
        public String getJaJPName() {
            return "爆匠";
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
            return 4682022;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
