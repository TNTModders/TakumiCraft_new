/*
package com.tntmodders.takumicraft.enchantment;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class TCAntiPoweredEnchantment extends AbstractTCEnchantment {
    public TCAntiPoweredEnchantment(BootstrapContext context) {
        super(Enchantment.definition(context.lookup(Registries.ITEM).getOrThrow(TCItemCore.ANTI_POWERED), 8, 1, Enchantment.dynamicCost(5, 9), Enchantment.dynamicCost(20, 9), 4, EquipmentSlotGroup.MAINHAND));
    }

    public void doPostAttack(LivingEntity player, Entity target, int lv) {
        if (target instanceof Creeper creeper && creeper.isPowered()
                && EnchantmentHelper.getEnchantmentLevel(player.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.ANTI_POWERED), player) > 0) {
            if (!creeper.level().isClientSide) {
                creeper.hurt(player.level().damageSources().mobAttack(player), 20f);
                creeper.getEntityData().set(ObfuscationReflectionHelper.getPrivateValue(Creeper.class, creeper, "DATA_IS_POWERED"), false);
                TCExplosionUtils.createExplosion(creeper.level(), player, creeper.blockPosition(), 0f);
            }
            player.playSound(SoundEvents.TRIDENT_THUNDER.get());
            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).getAdvancements()
                        .award(((ServerPlayer) player).server.getAdvancements().get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "disarmament")), "impossible");
            }
        }
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
*/
