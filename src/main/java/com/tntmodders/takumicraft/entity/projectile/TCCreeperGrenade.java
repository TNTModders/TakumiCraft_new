package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class TCCreeperGrenade extends AbstractTCGrenade {
    private int power = 10;

    public static final EntityType<TCCreeperGrenade> GRENADE = EntityType.Builder.<TCCreeperGrenade>of(TCCreeperGrenade::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(TCEntityUtils.TCEntityId("takumithrowgrenade"));

    public TCCreeperGrenade(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
        super(p_37442_, p_37443_);
    }

    public TCCreeperGrenade(double p_37433_, double p_37434_, double p_37435_, Level p_37436_) {
        super(GRENADE, p_37433_, p_37434_, p_37435_, p_37436_, TCItemCore.TAKUMI_GRENADE.getDefaultInstance());
    }

    public TCCreeperGrenade(EntityType<? extends ThrowableItemProjectile> p_37438_, LivingEntity p_37439_, Level p_37440_) {
        super(GRENADE, p_37439_, p_37440_, TCItemCore.TAKUMI_GRENADE.getDefaultInstance());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("power", this.power);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.power = tag.getInt("power");
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public boolean getDestroy() {
        return false;
    }

    @Override
    protected Item getDefaultItem() {
        return TCItemCore.TAKUMI_GRENADE;
    }
}
