package com.tntmodders.takumicraft.event;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.*;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCBreezeCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCPhantomCreeper;
import com.tntmodders.takumicraft.entity.projectile.TCCreeperArrow;
import com.tntmodders.takumicraft.entity.projectile.TCCreeperGrenade;
import com.tntmodders.takumicraft.item.TCTakumiSpecialMeatItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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
                if (event.getLevel().getBlockState(pos).getBlock() instanceof ITCBlocks block) {
                    block.onRemovedfromExplosionList(event.getLevel(), pos);
                }
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
                            .award(Objects.requireNonNull(((ServerPlayer) player).server.getAdvancements().get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "root"))), "impossible");
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
        if (event.getEntity() instanceof Breeze) {
            if (event.getLevel().getRandom().nextInt(10) == 0 && event.getEntity().level() instanceof ServerLevel level) {
                BlockPos pos = new BlockPos((int) event.getX(), (int) event.getY(), (int) event.getZ());
                TCBreezeCreeper creeper = (TCBreezeCreeper) TCEntityCore.BREEZE.entityType().create(level);
                creeper.moveTo(pos, 0f, 0f);
                creeper.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), event.getEntity().getSpawnType(), null);
                if (level.tryAddFreshEntityWithPassengers(creeper)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
        if (event.getEntity() instanceof TCBreezeCreeper creeper && !creeper.isPowered()) {
            if (!event.getLevel().getLevel().isThundering()) {
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
        if (event.getEntity().isInFluidType(TCFluidCore.HOTSPRING_TYPE.get())) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.REGENERATION, 4, 0, true, false, false));
            if (event.getEntity().tickCount % 200 == 0) {
                event.getEntity().addEffect(new MobEffectInstance(MobEffects.SATURATION, 4, 0, true, false, false));
            }
        }
        if (event.getEntity() instanceof Parrot parrot && !parrot.getTags().isEmpty() && parrot.getTags().contains("parrotcreeper")) {
            TCExplosionUtils.createExplosion(parrot.level(), parrot, parrot.getOnPos(), 2f);
            parrot.discard();
        }
        if (event.getEntity() instanceof ServerPlayer player) {
            //parrotcreeper
            if (player.tickCount % 400 == 0) {
                ListTag list = null;
                if (player.getShoulderEntityLeft().contains("Tags")) {
                    list = player.getShoulderEntityLeft().getList("Tags", 8);
                } else if (player.getShoulderEntityRight().contains("id")) {
                    list = player.getShoulderEntityLeft().getList("Tags", 8);
                }
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        String tag = list.getString(i);
                        if (tag.equals("parrotcreeper")) {
                            player.playSound(SoundEvents.PARROT_IMITATE_CREEPER);
                        }
                    }
                }
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

    @SubscribeEvent
    public void onDamage(LivingDamageEvent event) {
        if (event.getSource().is(DamageTypes.EXPLOSION) && event.getSource().getDirectEntity() instanceof TCCreeperGrenade) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Creeper creeper && event.getSource().getEntity() instanceof LivingEntity living && EnchantmentHelper.getEnchantmentLevel(living.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.ANTI_POWERED), living) > 0 && creeper.isPowered()) {
            if (!creeper.level().isClientSide) {
                creeper.getEntityData().set(ObfuscationReflectionHelper.getPrivateValue(Creeper.class, creeper, "DATA_IS_POWERED"), false);
                TCExplosionUtils.createExplosion(creeper.level(), living, creeper.blockPosition(), 0f);
                creeper.hurt(living.level().damageSources().mobAttack(living), 20f);
            }
            living.playSound(SoundEvents.TRIDENT_THUNDER.get());
            if (living instanceof ServerPlayer player) {
                player.getAdvancements().award(player.server.getAdvancements().get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "disarmament")), "impossible");
            }
        }
    }

    @SubscribeEvent
    public void onBucketUse(FillBucketEvent event) {
        if (event.getEmptyBucket().is(Items.BUCKET) && event.getTarget().getType() == HitResult.Type.BLOCK && event.getLevel().getBlockState(((BlockHitResult) event.getTarget()).getBlockPos()).getBlock() instanceof LiquidBlock liquidBlock && liquidBlock.getFluid().getBucket().getDefaultInstance().is(TCItemCore.TAKUMI_BUCKETS)) {
            event.setCanceled(true);
        }
    }

    private boolean isUnderProtection(ServerLevel level, BlockPos pos) {
        BlockPos blockpos = level.getSharedSpawnPos();
        int i = Mth.abs(pos.getX() - blockpos.getX());
        int j = Mth.abs(pos.getZ() - blockpos.getZ());
        int k = Math.max(i, j);
        return k <= level.getServer().getSpawnProtectionRadius() * TCConfigCore.TCServerConfig.SERVER.spawnProtectionRadius.get();
    }
}
