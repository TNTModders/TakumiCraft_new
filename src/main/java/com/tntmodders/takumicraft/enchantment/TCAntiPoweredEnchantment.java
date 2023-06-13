package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class TCAntiPoweredEnchantment extends AbstractTCEnchantment {
    public TCAntiPoweredEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public void doPostAttack(LivingEntity player, Entity target, int lv) {
        if (target instanceof Creeper creeper && creeper.isPowered()
                && player.getMainHandItem().getEnchantmentLevel(TCEnchantmentCore.ANTI_POWERED) > 0) {
            if (!creeper.level().isClientSide) {
                creeper.hurt(player.level().damageSources().mobAttack(player), 20f);
                creeper.getEntityData().set(ObfuscationReflectionHelper.getPrivateValue(Creeper.class, creeper, "DATA_IS_POWERED"), false);
                TCExplosionUtils.createExplosion(creeper.level(), player, creeper.blockPosition(), 0f);
            }
            player.playSound(SoundEvents.TRIDENT_THUNDER);
            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).getAdvancements()
                        .award(((ServerPlayer) player).server.getAdvancements().getAdvancement(new ResourceLocation(TakumiCraftCore.MODID, "disarmament")), "impossible");
            }
        }
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public String getEnUSName() {
        return "Anti Powered";
    }

    @Override
    public String getJaJPName() {
        return "巨匠特攻";
    }

    @Override
    public String getRegistryName() {
        return "anti_powered";
    }
}
