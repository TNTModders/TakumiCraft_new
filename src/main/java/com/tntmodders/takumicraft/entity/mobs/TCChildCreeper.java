package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCChildCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class TCChildCreeper extends AbstractTCCreeper {
    protected static final EntityDataAccessor<Boolean> DATA_IS_CHILD = SynchedEntityData.defineId(TCChildCreeper.class, EntityDataSerializers.BOOLEAN);

    public TCChildCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.CHILD;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_CHILD, false);
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.isChild() && !this.level().isClientSide()) {
            TCChildCreeper creeper = new TCChildCreeper((EntityType<Creeper>) TCEntityCore.CHILD.entityType(), this.level());
            creeper.copyPosition(this);
            creeper.setHealth(this.getHealth());
            if (this.isPowered()) {
                creeper.setPowered(true);
            }
            creeper.getEntityData().set(DATA_IS_CHILD, true);
            creeper.explosionRadius = this.explosionRadius * 4;
            this.level().addFreshEntity(creeper);
        }
    }

    @Override
    protected float sanitizeScale(float p_330116_) {
        return this.isChild() ? 0.5f : 1f;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState, float f) {
        return blockState.getDestroySpeed(level, pos) < 0 ? super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState, f) : 1f;
    }

    public boolean isChild() {
        return this.getEntityData().get(DATA_IS_CHILD);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_32304_) {
        super.addAdditionalSaveData(p_32304_);
        if (this.isChild()) {
            p_32304_.putBoolean("child", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_32296_) {
        super.readAdditionalSaveData(p_32296_);
        this.getEntityData().set(DATA_IS_CHILD, p_32296_.getBoolean("child"));
    }

    public static class TCChildCreeperContext implements TCCreeperContext<TCChildCreeper> {
        private static final String NAME = "childcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCChildCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おやこたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Carring the love in their bodies, and not afraid to explode in order to protect their own children.";
        }

        @Override
        public String getJaJPDesc() {
            return "おやこあいをその身に宿して、我が子を守るためなら爆発さえ恐れないという。";
        }

        @Override
        public String getEnUSName() {
            return "Child Creeper";
        }

        @Override
        public String getJaJPName() {
            return "親子匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xccaaaa;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00aa00;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCChildCreeper>) type, TCChildCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
