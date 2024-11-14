package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCSquidCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCParticleTypeCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCGlowingSquidCreeper extends TCSquidCreeper {
    private static final EntityDataAccessor<Integer> DATA_DARK_TICKS_REMAINING = SynchedEntityData.defineId(TCGlowingSquidCreeper.class, EntityDataSerializers.INT);

    public TCGlowingSquidCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean checkGlowSquidSpawnRules(EntityType<? extends LivingEntity> p_300540_, ServerLevelAccessor p_297255_, EntitySpawnReason p_297489_, BlockPos p_299141_, RandomSource p_297395_) {
        return p_299141_.getY() <= p_297255_.getSeaLevel() - 33 && p_297255_.getRawBrightness(p_299141_, 0) == 0 && p_297255_.getBlockState(p_299141_).is(Blocks.WATER);
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300));
            }
        });
    }

    @Override
    protected ParticleOptions getInkParticle() {
        return ParticleTypes.GLOW_SQUID_INK;
    }

    @Override
    protected ParticleOptions getTCInkParticle() {
        return TCParticleTypeCore.GLOWING_SQUID_INK;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_332968_) {
        super.defineSynchedData(p_332968_);
        p_332968_.define(DATA_DARK_TICKS_REMAINING, 0);
    }

    @Override
    protected SoundEvent getSquirtSound() {
        return SoundEvents.GLOW_SQUID_SQUIRT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GLOW_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_147124_) {
        return SoundEvents.GLOW_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GLOW_SQUID_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_147122_) {
        super.addAdditionalSaveData(p_147122_);
        p_147122_.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_147117_) {
        super.readAdditionalSaveData(p_147117_);
        this.setDarkTicks(p_147117_.getInt("DarkTicksRemaining"));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        int i = this.getDarkTicksRemaining();
        if (i > 0) {
            this.setDarkTicks(i - 1);
        }

        this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource p_147114_, float p_147115_) {
        boolean flag = super.hurtServer(level, p_147114_, p_147115_);
        if (flag) {
            this.setDarkTicks(100);
        }

        return flag;
    }

    private void setDarkTicks(int p_147120_) {
        this.entityData.set(DATA_DARK_TICKS_REMAINING, p_147120_);
    }

    public int getDarkTicksRemaining() {
        return this.entityData.get(DATA_DARK_TICKS_REMAINING);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.GLOWING_SQUID;
    }

    public static class TCSquidCreeperContext implements TCCreeperContext<TCGlowingSquidCreeper> {
        private static final String NAME = "glowingsquidcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCGlowingSquidCreeper::new, MobCategory.MONSTER).sized(0.9F, 1.4F).clientTrackingRange(10).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ほたるいかたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Squid? No, this is the era of glowing explosive!";
        }

        @Override
        public String getJaJPDesc() {
            return "輝く烏賊も、もちろん爆発性。洞窟を照らす輝きに目を奪われぬよう。";
        }

        @Override
        public String getEnUSName() {
            return "Glowing Squid Creeper";
        }

        @Override
        public String getJaJPName() {
            return "蛍烏賊匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x00aaaa;
        }

        @Override
        public int getSecondaryColor() {
            return 0xccffcc;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 10.0D);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCSquidCreeper>) type, TCSquidCreeperRenderer::new);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCGlowingSquidCreeper::checkGlowSquidSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
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
        public int getSpawnWeight() {
            return this.getRank().getSpawnWeight() / 5;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.GLOW_INK_SAC;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1f, 16f);
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(EntityTypeTags.CAN_BREATHE_UNDER_WATER);
            return list;
        }
    }
}
