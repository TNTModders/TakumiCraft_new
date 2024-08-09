package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import com.tntmodders.takumicraft.core.TCConfigCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TCCreeperSuperBlockEntity extends BlockEntity {
    private LockCode lockKey = LockCode.NO_LOCK;
    private BlockState state;
    private boolean hideOverlay;

    public TCCreeperSuperBlockEntity(BlockPos pos, BlockState state) {
        super(TCBlockEntityCore.SUPER_BLOCK, pos, state);
    }

    @Nullable
    public BlockState getState() {
        if (this.state == null || this.state.getBlock() == Blocks.AIR || this.state.getBlock() == TCBlockCore.SUPER_BLOCK) {
            return null;
        }
        return state;
    }

    public boolean isHideOverlay() {
        return hideOverlay;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setHideOverlay(boolean hideOverlay) {
        this.hideOverlay = hideOverlay;
    }

    public void setLock(Player player) {
        this.setLockCode(player.getStringUUID());
    }

    public void setLockCode(String lock) {
        this.lockKey = new LockCode(lock);
    }

    public boolean canChange(Player player) {
        if (TCConfigCore.TCServerConfig.SERVER.useTakumiBlockLock.get() && !player.isSpectator() && !unlocksWithPlayer(this.lockKey, player)) {
            player.sendSystemMessage(Component.translatable("takumicraft.container.takumiblock.locked"));
            player.playNotifySound(SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    private static boolean unlocksWithPlayer(LockCode code, Player player) {
        if (code.key().isEmpty()) {
            return true;
        } else {
            String uuid = player.getStringUUID();
            return player.hasPermissions(1) || code.key().equals(uuid) || code.unlocksWith(player.getMainHandItem());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.lockKey = LockCode.fromTag(tag);
        if (tag.contains("state")) {
            HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
            BlockState newState = NbtUtils.readBlockState(holdergetter, tag.getCompound("state"));
            if (this.state == null || !newState.getBlock().equals(this.state.getBlock())) {
                this.setState(newState);
            }
        }
        if (tag.contains("hideOverlay")) {
            this.hideOverlay = tag.getBoolean("hideOverlay");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.lockKey.addToTag(tag);
        if (this.state != null) {
            tag.put("state", NbtUtils.writeBlockState(this.state));
        }
        tag.putBoolean("hideOverlay", this.hideOverlay);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider compoundTag) {
        CompoundTag tag = this.saveWithoutMetadata(compoundTag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput p_329127_) {
        super.applyImplicitComponents(p_329127_);
        this.lockKey = p_329127_.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder p_336292_) {
        super.collectImplicitComponents(p_336292_);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            p_336292_.set(DataComponents.LOCK, this.lockKey);
        }
    }

    @Override
    public BlockEntityType<?> getType() {
        return TCBlockEntityCore.SUPER_BLOCK;
    }
}
