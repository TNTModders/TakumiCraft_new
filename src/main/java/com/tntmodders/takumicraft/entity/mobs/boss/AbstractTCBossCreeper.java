package com.tntmodders.takumicraft.entity.mobs.boss;

import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class AbstractTCBossCreeper extends AbstractTCCreeper {
    protected final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true).setCreateWorldFog(true).setPlayBossMusic(true);

    public AbstractTCBossCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public ServerBossEvent getBossEvent() {
        return this.bossEvent;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_31474_) {
        super.readAdditionalSaveData(p_31474_);
        if (this.hasCustomName()) {
            this.getBossEvent().setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Component p_31476_) {
        super.setCustomName(p_31476_);
        this.getBossEvent().setName(this.getDisplayName());
    }

    @Override
    public void tick() {
        super.tick();
        this.getBossEvent().setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer p_31483_) {
        super.startSeenByPlayer(p_31483_);
        this.getBossEvent().addPlayer(p_31483_);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
        this.getBossEvent().removePlayer(p_31488_);
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }
}
