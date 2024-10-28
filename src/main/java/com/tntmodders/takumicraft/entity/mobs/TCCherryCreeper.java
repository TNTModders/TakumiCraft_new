package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCCherryCreeper extends AbstractTCCreeper {

    public TCCherryCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.CHERRY;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> {

        });
        event.getAffectedBlocks().removeIf(pos -> {
            if (pos.getY() <= this.getBlockY()) {
                if (this.level().getBlockState(pos).isAir() && !this.level().getBlockState(pos.below()).isAir()) {
                    this.level().setBlock(pos, Blocks.PINK_PETALS.defaultBlockState().setValue(BlockStateProperties.FLOWER_AMOUNT, 4), 3);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            BlockState state = this.level().getBlockState(this.getOnPos().above());
            this.level().setBlock(this.getOnPos().above(), Blocks.DIRT.defaultBlockState(), 3);
            boolean flg = TreeGrower.CHERRY.growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), this.getOnPos().above().above(), serverLevel.getBlockState(this.getOnPos().above().above()), this.getRandom());
            if (flg) {
                this.level().setBlock(this.getOnPos().above(), Blocks.CHERRY_LOG.defaultBlockState(), 3);
            } else {
                this.level().setBlock(this.getOnPos().above(), state, 3);
            }
        }
    }

    public static class TCCherryCreeperContext implements TCCreeperContext<TCCherryCreeper> {
        private static final String NAME = "cherrycreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCCherryCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "さくらたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A spectacular view appears, cherry flower dancing in the sky.";
        }

        @Override
        public String getJaJPDesc() {
            return "桜舞い散る絶景が現れる。";
        }

        @Override
        public String getEnUSName() {
            return "Cherry Creeper";
        }

        @Override
        public String getJaJPName() {
            return "桜匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xffcccc;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.CHERRY_SAPLING;
        }
    }
}
