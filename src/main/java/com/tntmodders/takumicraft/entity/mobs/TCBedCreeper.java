package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCBedCreeper extends AbstractTCCreeper {

    public TCBedCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BED;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().clearToBlow();
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                BlockPos pos = serverPlayer.getRespawnPosition();
                BlockState state = this.level().getBlockState(pos);
                float res = state.getExplosionResistance(this.level(), pos, event.getExplosion());
                if (state.isBed(this.level(), pos, serverPlayer) && res < 2000) {
                    this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    TCExplosionUtils.createExplosion(this.level(), null, pos, this.isPowered() ? 20 : 12);
                    serverPlayer.sendSystemMessage(Component.translatable("entity.takumicraft.bedcreeper.message", serverPlayer.getName()));
                }
            }
        });
    }

    public static class TCBedCreeperContext implements TCCreeperContext<TCBedCreeper> {
        private static final String NAME = "bedcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBedCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "しんだいたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Bed always detonates, doesn't it?";
        }

        @Override
        public String getJaJPDesc() {
            return "ベッドって爆発物だろ？ネザーみたいにさ！";
        }

        @Override
        public String getEnUSName() {
            return "Bed Creeper";
        }

        @Override
        public String getJaJPName() {
            return "寝台匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x33ff33;
        }

        @Override
        public int getSecondaryColor() {
            return 0xff8888;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.RED_BED;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1, 1);
        }
    }
}
