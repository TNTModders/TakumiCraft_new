package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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

public class TCWoodCreeper extends AbstractTCCreeper {

    public TCWoodCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 3;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.WOOD;
    }


    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter wolrdIn, BlockPos pos, BlockState blockStateIn, FluidState p_19996_, float p_19997_) {
        return blockStateIn.isAir() || blockStateIn.is(BlockTags.LEAVES) || blockStateIn.is(BlockTags.MINEABLE_WITH_AXE) ? 0.05f : super.getBlockExplosionResistance(explosion, wolrdIn, pos, blockStateIn, p_19996_, p_19997_) * 10f;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        float power = event.getExplosion().radius();
        if (power > 0.1) {
            List<BlockPos> posList = new ArrayList<>();
            event.getAffectedBlocks().stream().filter(pos -> this.level().getBlockState(pos).is(BlockTags.MINEABLE_WITH_AXE)).forEach(pos -> {
                this.level().destroyBlock(pos, true, this);
                TCExplosionUtils.createExplosion(this.level(), this, pos, Math.max(0, event.getExplosion().radius() - 0.3f));
                posList.add(pos);
            });
            if (!posList.isEmpty()) {
                event.getAffectedBlocks().removeAll(posList);
            }
        }
    }

    public static class TCWoodCreeperContext implements TCCreeperContext<TCWoodCreeper> {
        private static final String NAME = "woodcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCWoodCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "きたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Destroy woods. The forest will be filled with this explosion like a burning.";
        }

        @Override
        public String getJaJPDesc() {
            return "木に指向性を示す爆発を起こす。森は忽ち爆発の噴火に沈む。";
        }

        @Override
        public String getEnUSName() {
            return "Wood Creeper";
        }

        @Override
        public String getJaJPName() {
            return "木匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaaffaa;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
