package com.tntmodders.takumicraft.world.level.material;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCFluidCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.fluids.FluidType;

import javax.annotation.Nullable;

public abstract class TCHotspringFluid extends WaterFluid {
    @Override
    public Fluid getFlowing() {
        return TCFluidCore.FLOWING_HOTSPRING.get();
    }

    @Override
    public Fluid getSource() {
        return TCFluidCore.HOTSPRING.get();
    }

    @Override
    public Item getBucket() {
        return TCItemCore.TAKUMI_BUCKET_HOTSPRING;
    }

    @Override
    public FluidType getFluidType() {
        return TCFluidCore.HOTSPRING_TYPE.get();
    }

    @Override
    public void animateTick(Level level, BlockPos pos, FluidState fluidState, RandomSource rand) {
        if (!fluidState.isSource() && !fluidState.getValue(FALLING)) {
            if (rand.nextInt(64) == 0) {
                level.playLocalSound((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() + 0.5F, false);
            }
        }
        if (rand.nextBoolean()) {
            level.addParticle(ParticleTypes.WHITE_SMOKE, (double) pos.getX() + rand.nextDouble(), (double) pos.getY() + rand.nextDouble(), (double) pos.getZ() + rand.nextDouble(), 0.0, 0.1, 0.0);
        }
    }


    @Nullable
    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.WHITE_SMOKE;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return level.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION);
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return TCBlockCore.HOTSPRING.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == TCFluidCore.HOTSPRING.get() || fluid == TCFluidCore.FLOWING_HOTSPRING.get();
    }

    @Override
    public int getDropOff(LevelReader levelReader) {
        return 2;
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 10;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(TCFluidCore.HOTSPRINGS);
    }

    @Override
    protected float getExplosionResistance() {
        return 1000000F;
    }


    public static class Flowing extends TCHotspringFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76476_) {
            super.createFluidStateDefinition(p_76476_);
            p_76476_.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends TCHotspringFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
