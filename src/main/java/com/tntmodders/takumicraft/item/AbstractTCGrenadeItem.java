package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractTCGrenadeItem extends Item implements ProjectileItem, ITCItems {
    public AbstractTCGrenadeItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            Projectile projectile = this.asProjectile(level, player.position().add(0, player.getEyeHeight(), 0), itemstack, player.getDirection());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        itemstack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.EXPLOSIVES);
    }
}
