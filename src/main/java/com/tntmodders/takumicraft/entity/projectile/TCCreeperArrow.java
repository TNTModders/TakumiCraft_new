package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.level.ExplosionEvent;

import javax.annotation.Nullable;

public class TCCreeperArrow extends Arrow {
    public static final EntityType<Arrow> ARROW = EntityType.Builder.<Arrow>of(TCCreeperArrow::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(20).build(TCEntityUtils.TCEntityId("creeperarrow"));
    private float power = 1.5f;
    private boolean dest = false;

    public TCCreeperArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public TCCreeperArrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_, ItemStack stack, @Nullable ItemStack p_343588_) {
        super(p_36861_, p_36862_, p_36863_, p_36864_, stack, p_343588_);
    }

    public TCCreeperArrow(Level p_36866_, LivingEntity p_36867_, ItemStack stack, @Nullable ItemStack p_343588_) {
        super(p_36866_, p_36867_, stack, p_343588_);
    }

    @Override
    protected void onHit(HitResult p_37260_) {
        super.onHit(p_37260_);
        if (!this.level().isClientSide) {
            TCExplosionUtils.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(), this.power, false);
        }
        this.discard();
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }

    @Override
    public boolean shouldBlockExplode(Explosion p_19987_, BlockGetter p_19988_, BlockPos p_19989_, BlockState p_19990_, float p_19991_) {
        return this.dest;
    }

    private PotionContents getPotionContents() {
        return this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

/*    @Override
    public void setEffectsFromItem(ItemStack p_36879_) {
        if (p_36879_.is(TCItemCore.TIPPED_CREEPER_ARROW)) {
            Potion potion = PotionUtils.getPotion(p_36879_);
            Collection<MobEffectInstance> collection = PotionUtils.getCustomEffects(p_36879_);
            collection.addAll(potion.getEffects());
            if (!collection.isEmpty()) {
                for (MobEffectInstance mobeffectinstance : collection) {
                    this.effects.add(new MobEffectInstance(mobeffectinstance));
                }
            }
        }
    }*/

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Power", this.power);
        tag.putBoolean("Dest", this.dest);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.power = tag.getFloat("Power");
        this.dest = tag.getBoolean("Dest");
    }

    public void setPower(float power) {
        this.power = power;
    }

    public void setDest(boolean dest) {
        this.dest = dest;
    }

    public void arrowExplosionEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(entity -> entity.equals(this.getOwner()));
        if (this.getPotionContents().hasEffects()) {
            event.getAffectedEntities().forEach(entity -> {
                if (entity instanceof LivingEntity living) {
                    this.getPotionContents().getAllEffects().forEach(living::addEffect);
                }
            });
        }
    }

}
