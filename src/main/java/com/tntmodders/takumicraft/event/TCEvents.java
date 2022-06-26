package com.tntmodders.takumicraft.event;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.TCPhantomCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = TakumiCraftCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCEvents {
    public static final TCEvents INSTANCE = new TCEvents();

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        //onExplosionInit
        //protection etc.
        List<BlockPos> removePosList = new ArrayList<>();
        event.getAffectedBlocks().forEach(pos -> {
            if (event.getWorld().getBlockState(pos).is(TCBlockCore.ANTI_EXPLOSION)
                    || (event.getWorld() instanceof ServerLevel level && this.isUnderProtection(level, pos))) {
                removePosList.add(pos);
            }
        });
        event.getAffectedBlocks().removeAll(removePosList);

        List<? extends Player> playerList = event.getWorld().players();
        if (!playerList.isEmpty()) {
            playerList.forEach(player -> {
                if (player instanceof ServerPlayer && player.distanceToSqr(event.getExplosion().getPosition()) < 100) {
                    ((ServerPlayer) player).getAdvancements()
                            .award(((ServerPlayer) player).server.getAdvancements().getAdvancement(new ResourceLocation(TakumiCraftCore.MODID, "root")), "impossible");
                }
            });
        }

        //onExplosion
        if (!event.getWorld().isClientSide) {
            if (event.getExplosion().getExploder() instanceof AbstractTCCreeper) {
                ((AbstractTCCreeper) event.getExplosion().getExploder()).explodeCreeperEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity() instanceof Phantom && event.getWorld().getRandom().nextInt(10) == 0 && event.getEntity().level instanceof ServerLevel level) {
            BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
            TCPhantomCreeper creeper = ((TCPhantomCreeper) TCEntityCore.PHANTOM.entityType().create(level));
            creeper.moveTo(pos, 0f, 0f);
            creeper.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null, null);
            if (level.tryAddFreshEntityWithPassengers(creeper)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof Creeper creeper && event.getEntity().level.isThundering()) {
            creeper.getEntityData().set(ObfuscationReflectionHelper.getPrivateValue(Creeper.class, creeper, "DATA_IS_POWERED"), true);
        }
    }

    private boolean isUnderProtection(ServerLevel level, BlockPos pos) {
        BlockPos blockpos = level.getSharedSpawnPos();
        int i = Mth.abs(pos.getX() - blockpos.getX());
        int j = Mth.abs(pos.getZ() - blockpos.getZ());
        int k = Math.max(i, j);
        return k <= level.getServer().getSpawnProtectionRadius();
    }
}
