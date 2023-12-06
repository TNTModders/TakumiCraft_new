package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.CherryTreeGrower;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
        event.getAffectedBlocks().removeIf(pos -> pos.getY() <= this.getBlockY());
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            this.level().setBlock(this.getOnPos().above(), Blocks.DIRT.defaultBlockState(), 3);
            new CherryTreeGrower().growTree(serverLevel, serverLevel.getChunkSource().getGenerator(), this.getOnPos().above().above(), serverLevel.getBlockState(this.getOnPos().above().above()), this.getRandom());
            this.level().setBlock(this.getOnPos().above(), Blocks.CHERRY_LOG.defaultBlockState(), 3);
        }
    }

    public static class TCCherryCreeperContext implements TCCreeperContext<TCCherryCreeper> {
        private static final String NAME = "cherrycreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCCherryCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

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
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCCreeperRenderer::new);
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
