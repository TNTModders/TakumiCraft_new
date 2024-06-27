package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.List;

public class TCReturnCreeper extends AbstractTCCreeper {

    public TCReturnCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.RETURN;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<Entity> entityList = new ArrayList<>();
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                BlockPos pos = serverPlayer.getRespawnPosition();
                if (pos != null && pos != BlockPos.ZERO) {
                    DimensionTransition transition = serverPlayer.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);
                    if (transition != null && !transition.missingRespawnBlock()) {
                        serverPlayer.teleportTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                        serverPlayer.sendSystemMessage(Component.translatable("entity.takumicraft.returncreeper.message", serverPlayer.getName()));
                        TCExplosionUtils.createExplosion(this.level(), null, pos, 0f);
                        entityList.add(serverPlayer);
                    }
                }
            }
        });
        event.getAffectedEntities().removeAll(entityList);
    }

    public static class TCReturnCreeperContext implements TCCreeperContext<TCReturnCreeper> {
        private static final String NAME = "returncreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCReturnCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "きしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "This is the turn when you return your town.";
        }

        @Override
        public String getJaJPDesc() {
            return "害悪な匠。ベッドを持って行く先々で眠れば地獄を見ずに済む。とにかく面倒くさい。";
        }

        @Override
        public String getEnUSName() {
            return "Return Creeper";
        }

        @Override
        public String getJaJPName() {
            return "帰匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x003300;
        }

        @Override
        public int getSecondaryColor() {
            return 0xff8888;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.ENDER_PEARL;
        }
    }
}
