package com.tntmodders.takumicraft.entity.decoration;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class TCCreeperFrame extends ItemFrame implements ITCTranslator {

    public static final EntityType<TCCreeperFrame> ITEM_FRAME = EntityType.Builder.<TCCreeperFrame>of(TCCreeperFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.0F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build(TCEntityCore.TCEntityId("creeperframe"));

    public TCCreeperFrame(EntityType<? extends ItemFrame> p_31761_, Level p_31762_) {
        super(p_31761_, p_31762_);
    }

    public TCCreeperFrame(EntityType<? extends ItemFrame> p_149621_, Level p_149622_, BlockPos p_149623_, Direction p_149624_) {
        super(p_149621_, p_149622_, p_149623_, p_149624_);
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Frame";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質額縁";
    }

    @Override
    protected ItemStack getFrameItemStack() {
        return new ItemStack(TCItemCore.CREEPER_FRAME);
    }
}
