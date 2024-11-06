package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.entity.mobs.TCBirdCreeper;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class TCBirdBomb extends ThrowableItemProjectile {
    public static final EntityType<TCBirdBomb> BIRD_BOMB = EntityType.Builder.<TCBirdBomb>of(TCBirdBomb::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(TCEntityUtils.TCEntityId("birdbomb"));

    public TCBirdBomb(EntityType type, Level p_37443_) {
        super(type, p_37443_);
    }

    public TCBirdBomb(double p_37433_, double p_37434_, double p_37435_, Level p_37436_) {
        super(BIRD_BOMB, p_37433_, p_37434_, p_37435_, p_37436_, Items.EGG.getDefaultInstance());
    }

    public TCBirdBomb(LivingEntity p_37439_, Level p_37440_) {
        super(BIRD_BOMB, p_37439_, p_37440_, Items.EGG.getDefaultInstance());
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
    }

    @Override
    protected void onHit(HitResult p_37260_) {
        if (!this.level().isClientSide) {
            float power = 2f;
            if (this.getOwner() != null && this.getOwner() instanceof TCBirdCreeper creeper && creeper.isPowered()) {
                power = 4f;
            }
            TCExplosionUtils.createExplosion(this.level(), this.getOwner() == null ? this : this.getOwner(), this.getOnPos(), power);
            this.discard();
        }
    }
}
