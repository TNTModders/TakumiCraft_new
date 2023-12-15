package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCAcidBlock;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCAcidCreeper extends AbstractTCCreeper {
    private int stage;

    public TCAcidCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
        this.stage = 0;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ACID;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> {
            if (!event.getLevel().getBlockState(pos).isAir()) {
                if (event.getLevel().getBlockState(pos).is(TCBlockCore.ACID)) {
                    this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                } else {
                    this.level().setBlock(pos, TCBlockCore.ACID.defaultBlockState().setValue(TCAcidBlock.STAGE, this.stage), 3);
                }
            }
        });
        event.getExplosion().clearToBlow();
    }

    @Override
    public float getBlockExplosionResistance(Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_) {
        return super.getBlockExplosionResistance(p_19992_, p_19993_, p_19994_, p_19995_, p_19996_, p_19997_) / 25f;
    }

    public static class TCAcidCreeperContext implements TCCreeperContext<TCAcidCreeper> {
        private static final String NAME = "acidcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCAcidCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ふしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "All blocks go into acid...";
        }

        @Override
        public String getJaJPDesc() {
            return "沈め、沈め、腐蝕に沈め。";
        }

        @Override
        public String getEnUSName() {
            return "Acid Creeper";
        }

        @Override
        public String getJaJPName() {
            return "腐匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x111111;
        }

        @Override
        public int getSecondaryColor() {
            return 0x006600;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCBlockCore.ACID;
        }
    }
}
