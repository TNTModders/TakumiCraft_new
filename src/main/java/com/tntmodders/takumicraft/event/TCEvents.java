package com.tntmodders.takumicraft.event;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCConfigCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCPhantomCreeper;
import com.tntmodders.takumicraft.entity.projectile.TCCreeperArrow;
import com.tntmodders.takumicraft.item.TCTakumiSpecialMeatItem;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCEvents {
    public static final TCEvents INSTANCE = new TCEvents();

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        //onExplosionInit
        //protection etc.
        List<BlockPos> removePosList = new ArrayList<>();
        event.getAffectedBlocks().forEach(pos -> {
            if (event.getLevel().getBlockState(pos).is(TCBlockCore.ANTI_EXPLOSION)
                    || event.getLevel() instanceof ServerLevel level && this.isUnderProtection(level, pos)) {
                removePosList.add(pos);
            }
        });
        event.getAffectedBlocks().removeAll(removePosList);

        event.getAffectedEntities().removeIf(entity -> {
            if (entity instanceof ItemEntity item) {
                if (TCTakumiSpecialMeatItem.canConvertToSpecialMeat(item.getItem())) {
                    item.setItem(TCTakumiSpecialMeatItem.getSpecialMeat(item.getItem()));
                    return true;
                } else {
                    return item.getItem().is(TCItemCore.SPECIAL_MEATS);
                }
            }
            return false;
        });

        List<? extends Player> playerList = event.getLevel().players();
        if (!playerList.isEmpty()) {
            playerList.forEach(player -> {
                if (player instanceof ServerPlayer && player.distanceToSqr(event.getExplosion().center()) < 100) {
                    ((ServerPlayer) player).getAdvancements()
                            .award(Objects.requireNonNull(((ServerPlayer) player).server.getAdvancements().get(new ResourceLocation(TakumiCraftCore.MODID, "root"))), "impossible");
                }
            });
        }

        //onExplosion
        if (!event.getLevel().isClientSide) {
            if (event.getExplosion().getDirectSourceEntity() instanceof AbstractTCCreeper creeper) {
                creeper.explodeCreeperEvent(event);
            } else if (event.getExplosion().getDirectSourceEntity() instanceof TCCreeperArrow arrow) {
                arrow.arrowExplosionEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(MobSpawnEvent event) {
        if (event.getEntity() instanceof Phantom && event.getLevel().getRandom().nextInt(10) == 0 && event.getEntity().level() instanceof ServerLevel level) {
            BlockPos pos = new BlockPos((int) event.getX(), (int) event.getY(), (int) event.getZ());
            TCPhantomCreeper creeper = (TCPhantomCreeper) TCEntityCore.PHANTOM.entityType().create(level);
            creeper.moveTo(pos, 0f, 0f);
            creeper.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null);
            if (level.tryAddFreshEntityWithPassengers(creeper)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof Creeper creeper && event.getEntity().level().isThundering()) {
            if (creeper instanceof AbstractTCCreeper tcCreeper) {
                tcCreeper.weatherSetPowered();
            } else {
                creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
            }
        }
    }

    @SubscribeEvent
    public void doMobGriefing(EntityMobGriefingEvent event) {
        if (event.getEntity() instanceof AbstractTCCreeper creeper) {
            event.setResult(creeper.getContext().doCreeperGriefing(creeper) ? Event.Result.DEFAULT : Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onShield(ShieldBlockEvent event) {
        if (event.getDamageSource().is(DamageTypes.EXPLOSION) || event.getDamageSource().is(DamageTypes.PLAYER_EXPLOSION)) {
            ItemStack stack = event.getEntity().getUseItem();
            if (event.getEntity().isBlocking() && !stack.isEmpty() && !stack.is(TCItemCore.EXPLOSIVE_SHIELDS)) {
                stack.hurtAndBreak(10, event.getEntity(), null);
                event.getEntity().stopUsingItem();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDestroyItem(PlayerDestroyItemEvent event) {
        if (!event.getEntity().level().isClientSide && event.getOriginal().is(TCItemCore.MINESWEEPER_TOOLS)) {
            TCExplosionUtils.createExplosion(event.getEntity().level(), null, event.getEntity().getOnPos().above(1), 3f);
        }
    }

    private boolean isUnderProtection(ServerLevel level, BlockPos pos) {
        BlockPos blockpos = level.getSharedSpawnPos();
        int i = Mth.abs(pos.getX() - blockpos.getX());
        int j = Mth.abs(pos.getZ() - blockpos.getZ());
        int k = Math.max(i, j);
        return k <= level.getServer().getSpawnProtectionRadius() * TCConfigCore.TCCommonConfig.COMMON.spawnProtectionRadius.get();
    }
}
