package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCDripstoneCreeper extends AbstractTCCreeper {

    public TCDripstoneCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 8;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.DRIPSTONE;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<BlockPos> list = event.getAffectedBlocks();
        list.removeIf(pos -> !event.getLevel().getBlockState(pos).isAir() || event.getAffectedBlocks().contains(pos.above()));
        list.forEach(pos -> {
            event.getLevel().setBlockAndUpdate(pos, Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN));
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                event.getLevel().getBlockState(pos).tick(serverLevel, pos, this.getRandom());
            }

        });
        event.getAffectedBlocks().clear();
        event.getAffectedEntities().clear();
    }

    public static class TCDripstoneCreeperContext implements TCCreeperContext<TCDripstoneCreeper> {
        private static final String NAME = "dripstonecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCDripstoneCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "しょうにゅうせきたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Watch your head! A falling stone pierces your head.";
        }

        @Override
        public String getJaJPDesc() {
            return "頭上注意! 降る石が脳天を貫く。";
        }

        @Override
        public String getEnUSName() {
            return "Dripstone Creeper";
        }

        @Override
        public String getJaJPName() {
            return "鍾乳石匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xffdd88;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(TCEntityCore.NETHER_TAKUMIS);
            return list;
        }
    }
}
