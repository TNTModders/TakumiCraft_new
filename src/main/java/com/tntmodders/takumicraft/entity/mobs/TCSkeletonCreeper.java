package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCSkeletonCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class TCSkeletonCreeper extends AbstractTCSkeletonCreeper {
    private static final int TOTAL_CONVERSION_TIME = 300;
    private static final EntityDataAccessor<Boolean> DATA_STRAY_CONVERSION_ID = SynchedEntityData.defineId(TCSkeletonCreeper.class, EntityDataSerializers.BOOLEAN);
    public static final String CONVERSION_TAG = "StrayConversionTime";
    private int inPowderSnowTime;
    private int conversionTime;

    public TCSkeletonCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_STRAY_CONVERSION_ID, false);
    }

    public boolean isFreezeConverting() {
        return this.getEntityData().get(DATA_STRAY_CONVERSION_ID);
    }

    public void setFreezeConverting(boolean p_149843_) {
        this.entityData.set(DATA_STRAY_CONVERSION_ID, p_149843_);
    }

    @Override
    public boolean isShaking() {
        return this.isFreezeConverting();
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.isAlive() && !this.isNoAi()) {
            if (this.isInPowderSnow) {
                if (this.isFreezeConverting()) {
                    --this.conversionTime;
                    if (this.conversionTime < 0) {
                        this.doFreezeConversion();
                    }
                } else {
                    ++this.inPowderSnowTime;
                    if (this.inPowderSnowTime >= 140) {
                        this.startFreezeConversion(300);
                    }
                }
            } else {
                this.inPowderSnowTime = -1;
                this.setFreezeConverting(false);
            }
        }

        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_149836_) {
        super.addAdditionalSaveData(p_149836_);
        p_149836_.putInt("StrayConversionTime", this.isFreezeConverting() ? this.conversionTime : -1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_149833_) {
        super.readAdditionalSaveData(p_149833_);
        if (p_149833_.contains("StrayConversionTime", 99) && p_149833_.getInt("StrayConversionTime") > -1) {
            this.startFreezeConversion(p_149833_.getInt("StrayConversionTime"));
        }

    }

    private void startFreezeConversion(int p_149831_) {
        this.conversionTime = p_149831_;
        this.setFreezeConverting(true);
    }

    protected void doFreezeConversion() {
        if (!net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.STRAY, timer -> this.conversionTime = timer))
            return;
        var result = this.convertTo(EntityType.STRAY, true);
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1048, this.blockPosition(), 0);
        }
        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, result);

    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33579_) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SKELETON;
    }

    public static class TCSkeletonCreeperContext implements TCCreeperContext<TCSkeletonCreeper> {
        private static final String NAME = "skeletoncreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCSkeletonCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "すけるとんたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Skeleton shooting explosive arrow, the world is going to be war.";
        }

        @Override
        public String getJaJPDesc() {
            return "爆発性の矢を放つ。ワールドは爆風巻き起こる戦場と化す。";
        }

        @Override
        public String getEnUSName() {
            return "Skeleton Creeper";
        }

        @Override
        public String getJaJPName() {
            return "スケルトン匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 7846775;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return AbstractTCSkeletonCreeper.createAttributes();
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<AbstractTCCreeper>) type, p_174010_ -> new TCSkeletonCreeperRenderer(p_174010_, this));
        }
    }
}
