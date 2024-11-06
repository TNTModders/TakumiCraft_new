package com.tntmodders.takumicraft.entity.decoration;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TCCreeperGlowingFrame extends TCCreeperFrame {

    public static final EntityType<TCCreeperGlowingFrame> GLOWING_FRAME = EntityType.Builder.<TCCreeperGlowingFrame>of(TCCreeperGlowingFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.0F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build(TCEntityUtils.TCEntityId("creeperframe_glowing"));

    public TCCreeperGlowingFrame(EntityType<? extends ItemFrame> p_31761_, Level p_31762_) {
        super(p_31761_, p_31762_);
    }

    public TCCreeperGlowingFrame(EntityType<? extends ItemFrame> p_149621_, Level p_149622_, BlockPos p_149623_, Direction p_149624_) {
        super(p_149621_, p_149622_, p_149623_, p_149624_);
    }

    @Override
    public SoundEvent getRemoveItemSound() {
        return SoundEvents.GLOW_ITEM_FRAME_REMOVE_ITEM;
    }

    @Override
    public SoundEvent getBreakSound() {
        return SoundEvents.GLOW_ITEM_FRAME_BREAK;
    }

    @Override
    public SoundEvent getPlaceSound() {
        return SoundEvents.GLOW_ITEM_FRAME_PLACE;
    }

    @Override
    public SoundEvent getAddItemSound() {
        return SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM;
    }

    @Override
    public SoundEvent getRotateItemSound() {
        return SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM;
    }

    @Override
    protected ItemStack getFrameItemStack() {
        return new ItemStack(TCItemCore.CREEPER_FRAME_GLOWING);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Glowing Frame";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質額縁[輝]";
    }
}
