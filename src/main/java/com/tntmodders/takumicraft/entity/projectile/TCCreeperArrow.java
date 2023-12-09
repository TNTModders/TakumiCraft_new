package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class TCCreeperArrow extends Arrow {
    public static final EntityType<Arrow> ARROW = EntityType.Builder.<Arrow>of(TCCreeperArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20).build(TakumiCraftCore.MODID + ":creeperarrow");

    public TCCreeperArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public TCCreeperArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_, ItemStack stack) {
        super(p_36861_, p_36862_, p_36863_, p_36864_, stack);
    }

    public TCCreeperArrow(Level p_36866_, LivingEntity p_36867_, ItemStack stack) {
        super(p_36866_, p_36867_, stack);
    }

    @Override
    protected void onHit(HitResult p_37260_) {
        super.onHit(p_37260_);
        if (!this.level().isClientSide) {
            TCExplosionUtils.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(), 1.5f, false);
        }
        this.discard();
    }

    @Override
    public boolean shouldBlockExplode(Explosion p_19987_, BlockGetter p_19988_, BlockPos p_19989_, BlockState p_19990_, float p_19991_) {
        return false;
    }
}
