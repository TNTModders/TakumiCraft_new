package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class TCCreeperBedBlockEntity extends BlockEntity {
    private DyeColor color;
    private boolean isSuper;
    private String texturePath;
    private String textureNamespace;
    private Block block;

    private int tick;

    public TCCreeperBedBlockEntity(BlockPos p_155115_, BlockState p_155116_) {
        super(TCBlockEntityCore.CREEPER_BED, p_155115_, p_155116_);
        this.color = ((BedBlock) p_155116_.getBlock()).getColor();
    }

    public TCCreeperBedBlockEntity(BlockPos p_155118_, BlockState p_155119_, DyeColor p_155120_, boolean isSuper) {
        super(TCBlockEntityCore.CREEPER_BED, p_155118_, p_155119_);
        this.color = p_155120_;
        this.isSuper = isSuper;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = this.saveWithoutMetadata();
        return tag;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor p_58730_) {
        this.color = p_58730_;
    }

    public boolean isSuper() {
        return this.isSuper;
    }

    public void setSuper(boolean flg) {
        this.isSuper = flg;
    }

    public ResourceLocation getTexture() {
        if (this.textureNamespace == null || this.texturePath == null || this.textureNamespace.isEmpty() || this.texturePath.isEmpty() || this.texturePath.equals("air")) {
            return null;
        }
        return new ResourceLocation(this.textureNamespace, this.texturePath);
    }

    public void setTexture(ResourceLocation location) {
        this.textureNamespace = location.getNamespace();
        this.texturePath = location.getPath();
        this.block = ForgeRegistries.BLOCKS.getValue(location);
    }

    public Block getBlock() {
        if (this.block == null) {
            try {
                this.block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.textureNamespace, this.texturePath));
            } catch (Exception e) {
                this.block = Blocks.AIR;
            }
        }
        return block;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("namespace") && tag.contains("path")) {
            this.setTexture(new ResourceLocation(tag.getString("namespace"), tag.getString("path")));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.textureNamespace != null && !this.textureNamespace.isEmpty()) {
            tag.putString("namespace", this.textureNamespace);
        }
        if (this.textureNamespace != null && !this.texturePath.isEmpty()) {
            tag.putString("path", this.texturePath);
        }
    }
}
