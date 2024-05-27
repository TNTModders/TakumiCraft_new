package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.block.TCAcidBlock;
import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCAcidCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TCAcidBlockEntity extends BlockEntity {

    private final int stage;
    private int tick;

    public TCAcidBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.ACID, pos, state);
        tick = 0;
        stage = state.getValue(TCAcidBlock.STAGE);
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T blockentity) {
        if (blockentity instanceof TCAcidBlockEntity acid) {
            acid.tick++;
            if (acid.tick == 1 && (acid.stage == 0 || level.getRandom().nextInt(10) == 0)) {
                level.playSound(null, pos, SoundEvents.CREEPER_PRIMED, SoundSource.BLOCKS, 0.1f, 0.1f);
            } else if (acid.tick > 60 + level.getRandom().nextInt(120)) {
                level.getProfiler().push("acidblock");
                TCAcidCreeper acidCreeper = new TCAcidCreeper((EntityType<? extends Creeper>) TCEntityCore.ACID.entityType(), level);
                acidCreeper.setInvulnerable(true);
                acidCreeper.setInvisible(true);
                acidCreeper.setStage(acid.stage + 1);
                level.addFreshEntity(acidCreeper);
                TCExplosionUtils.createExplosion(level, acid.stage == 15 ? null : acidCreeper, pos, acid.stage == 15 ? 6f : 3f);
                acidCreeper.discard();
                level.getProfiler().pop();
            }
        }
    }

    public static <T extends BlockEntity> void clientTick(Level level, BlockPos pos, BlockState state, T blockentity) {
        if (blockentity instanceof TCAcidBlockEntity acid) {
            if (acid.stage < 15 && acid.tick % 20 == 0) {
                for (int i = 0; i < (acid.stage + 3) / 3; i++) {
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, pos.getX() + level.getRandom().nextDouble(), pos.getY() + 0.95 + level.getRandom().nextDouble(), pos.getZ() + level.getRandom().nextDouble(), (level.getRandom().nextDouble() - 0.5) / 10, 0.05 + level.getRandom().nextDouble() * Math.sqrt(acid.stage + 1) / 40, (level.getRandom().nextDouble() - 0.5) / 10);
                }
            }
            acid.tick++;
        }
    }

    public int getTick() {
        return tick;
    }
}
