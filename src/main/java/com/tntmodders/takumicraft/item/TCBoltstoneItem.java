package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TCBoltstoneItem extends Item implements ITCItems, ITCTranslator {

    public TCBoltstoneItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public boolean isFoil(ItemStack p_41172_) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 20);
        if (level.isClientSide) {
            Vec3 vec3 = player.getLookAngle();
            vec3 = vec3.normalize().scale(10);
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            bolt.moveTo(player.position().add(vec3));
            bolt.setCause(player instanceof ServerPlayer ? (ServerPlayer) player : null);
            level.addFreshEntity(bolt);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild && level.isClientSide) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity entity, LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, attacker.level());
            bolt.moveTo(entity.position());
            bolt.setCause(attacker instanceof ServerPlayer ? (ServerPlayer) attacker : null);
            attacker.level().addFreshEntity(bolt);
        }
        if (attacker instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild && !player.level().isClientSide) {
                itemStack.shrink(1);
            }
        }
        return true;
    }

    @Override
    public String getEnUSName() {
        return "Bolt Stone";
    }

    @Override
    public String getJaJPName() {
        return "かみなりのいし";
    }

    @Override
    public String getRegistryName() {
        return "boltstone";
    }
}
