package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class TCBlastPoweredEnchantment extends AbstractTCEnchantment {
    public TCBlastPoweredEnchantment() {
        super(Enchantment.definition(TCItemCore.BLAST_POWERED, 8, 1, Enchantment.dynamicCost(15, 9), Enchantment.dynamicCost(65, 9), 4, EquipmentSlot.MAINHAND));
    }

    @Override
    public void doPostItemStackHurt(LivingEntity p_336187_, Entity p_332860_, int p_328211_) {
        float f = 0.25F + 0.25F * (float) p_328211_;
        if (p_336187_.fallDistance > 5) {
            f += p_336187_.fallDistance / 10;
        }
        p_336187_.level().explode(null, null, new WindBurstEnchantmentDamageCalculator(f), p_336187_.getX(), p_336187_.getY(), p_336187_.getZ(), 3.5F, false, Level.ExplosionInteraction.BLOW, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);

        for (int i = 1; i < 5; i++) {
            p_336187_.level().explode(null, null, new WindBurstEnchantmentDamageCalculator(0f), p_336187_.getX(), p_336187_.getY() + i, p_336187_.getZ(), 0f, false, Level.ExplosionInteraction.NONE, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
        }
    }

    static final class WindBurstEnchantmentDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
        private final float knockBackPower;

        public WindBurstEnchantmentDamageCalculator(float p_331438_) {
            this.knockBackPower = p_331438_;
        }

        @Override
        public float getKnockbackMultiplier(Entity p_335974_) {
            boolean flag1;
            label17:
            {
                if (p_335974_ instanceof Player player && player.getAbilities().flying) {
                    flag1 = true;
                    break label17;
                }

                flag1 = false;
            }

            boolean flag = flag1;
            return !flag ? this.knockBackPower : 0.0F;
        }
    }

    @Override
    public String getRegistryName() {
        return "blast_powered";
    }

    @Override
    public String getEnUSName() {
        return "Blast Powered";
    }

    @Override
    public String getJaJPName() {
        return "巨匠跳躍";
    }
}
