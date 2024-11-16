package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.List;

public class TCSleeperCreeper extends AbstractTCCreeper {

    public TCSleeperCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SLEEPER;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().clear();
        List<Entity> entityList = new ArrayList<>();
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                BlockPos pos = serverPlayer.getRespawnPosition();
                if (pos != null && pos != BlockPos.ZERO) {
                    BlockState state = this.level().getBlockState(pos);
                    float res = state.getExplosionResistance(this.level(), pos, event.getExplosion());
                    TeleportTransition transition = serverPlayer.findRespawnPositionAndUseSpawnBlock(true, TeleportTransition.DO_NOTHING);
                    if (transition != null && !transition.missingRespawnBlock() && res <= 2000) {
                        for (int i = 0; i < (this.isPowered() ? 10 : 5); i++) {
                            TCCreeperContext context = TCEntityCore.ENTITY_CONTEXTS.get(this.level().getRandom().nextInt(TCEntityCore.ENTITY_CONTEXTS.size()));
                            if (context.getRank().getLevel() < TCCreeperContext.EnumTakumiRank.HIGH.getLevel()) {
                                Entity creeper = context.entityType().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                                creeper.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                                if (creeper instanceof AbstractTCCreeper) {
                                    ((AbstractTCCreeper) creeper).setPowered(true);
                                    this.level().addFreshEntity(creeper);
                                }
                            }
                        }
                        serverPlayer.sendSystemMessage(Component.translatable("entity.takumicraft.sleepercreeper.message", serverPlayer.getName()));
                        entityList.add(entity);
                    }
                }
            }
        });
    }

    public static class TCSleeperCreeperContext implements TCCreeperContext<TCSleeperCreeper> {
        private static final String NAME = "sleepercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCSleeperCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ねやたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Does your bed safe?";
        }

        @Override
        public String getJaJPDesc() {
            return "君のベッドは安全ですか……?";
        }

        @Override
        public String getEnUSName() {
            return "Sleeper Creeper";
        }

        @Override
        public String getJaJPName() {
            return "閨匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x88ff88;
        }

        @Override
        public int getPrimaryColor() {
            return 0x006600;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
