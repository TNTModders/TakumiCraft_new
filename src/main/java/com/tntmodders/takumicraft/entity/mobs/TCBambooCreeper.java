package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.Comparator;
import java.util.List;

public class TCBambooCreeper extends AbstractTCCreeper {

    public TCBambooCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BAMBOO;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<BlockPos> posList = event.getAffectedBlocks();
        posList.sort(Comparator.comparingInt(Vec3i::getY));
        posList.forEach(pos -> {
            if (this.level().getBlockState(pos).canBeReplaced() && this.level().getBlockState(pos.below()).isCollisionShapeFullBlock(this.level(), pos.below())) {
                this.level().setBlockAndUpdate(pos, TCBlockCore.TAKENOKO.defaultBlockState());
                this.level().scheduleTick(pos, TCBlockCore.TAKENOKO, 1, TickPriority.EXTREMELY_HIGH);
            }
        });
        event.getAffectedBlocks().clear();
    }

    public static class TCBambooCreeperContext implements TCCreeperContext<TCBambooCreeper> {
        private static final String NAME = "bamboocreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBambooCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ちくしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "A spectacular view appears, bamboo forest spreading on the ground.";
        }

        @Override
        public String getJaJPDesc() {
            return "竹生い茂る絶景が現れる。";
        }

        @Override
        public String getEnUSName() {
            return "Bamboo Creeper";
        }

        @Override
        public String getJaJPName() {
            return "竹匠";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xccffcc;
        }

        @Override
        public int getSecondaryColor() {
            return 0x999933;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.BAMBOO;
        }
    }
}
