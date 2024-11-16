package com.tntmodders.takumicraft.entity.projectile;

import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TCBreezeCreeperWindCharge extends AbstractWindCharge {
    public static final EntityType<TCBreezeCreeperWindCharge> BREEZE_WIND_CHARGE = EntityType.Builder.<TCBreezeCreeperWindCharge>of(TCBreezeCreeperWindCharge::new, MobCategory.MISC).sized(0.3125F, 0.3125F).eyeHeight(0.0F).clientTrackingRange(4).updateInterval(10).build(TCEntityCore.TCEntityId("breezecreeper_windcharge"));

    public TCBreezeCreeperWindCharge(EntityType<? extends AbstractWindCharge> p_328102_, Level p_329873_) {
        super(p_328102_, p_329873_);
    }

    public TCBreezeCreeperWindCharge(TCBreezeCreeper p_330729_, Level p_329490_) {
        super(BREEZE_WIND_CHARGE, p_329490_, p_330729_, p_330729_.getX(), p_330729_.getSnoutYPosition(), p_330729_.getZ());
    }

    @Override
    protected void explode(Vec3 p_343355_) {
        for (int i = 0; i < 5; i++) {
            double x = p_343355_.x() + this.level().getRandom().nextDouble() * 6 - 3;
            double y = p_343355_.y() + this.level().getRandom().nextDouble() * 2;
            double z = p_343355_.z() + this.level().getRandom().nextDouble() * 6 - 3;
            this.level().explode(this, null, EXPLOSION_DAMAGE_CALCULATOR, x, y, z, 2.0F, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
        }
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }
}
