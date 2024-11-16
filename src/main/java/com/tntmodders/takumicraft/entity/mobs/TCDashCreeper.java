package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCDashCreeper extends AbstractTCCreeper {
    private int dashTick;

    public TCDashCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 0;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.DASH;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN)) {
            return false;
        } else {
            return super.hurtServer(level, source, amount);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState state, FluidState fluidState, float f) {
        return state.getExplosionResistance(level, pos, explosion) < 0 ? super.getBlockExplosionResistance(explosion, level, pos, state, fluidState, f) : super.getBlockExplosionResistance(explosion, level, pos, state, fluidState, f) / 10;
    }

    @Override
    protected float getWaterSlowDown() {
        return 1f;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isPowered() && !this.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 3));
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5), this.getRandomY(1), this.getRandomZ(0.5), 0, 0, 0);
            }
        } else if (this.getSwellDir() > 0) {
            if (this.getTarget() != null) {
                this.getLookControl().setLookAt(this.getTarget(), 1f, 1f);
            }
            this.moveTo(this.position().add(this.getLookAngle().scale(3)), this.getXRot(), this.getYRot());
            TCExplosionUtils.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(), this.isPowered() ? 4f : 2f, false);
            this.dashTick++;
            if (this.dashTick > 60) {
                this.discard();
                TCExplosionUtils.createExplosion(this.level(), this, this.getX(), this.getY(), this.getZ(), 8f, false);
            }
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, EntitySpawnReason spawnType) {
        return super.checkSpawnRules(levelAccessor, spawnType) && levelAccessor.getRandom().nextInt(5) == 0;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        if (!this.isRemoved()) {
            event.getAffectedEntities().clear();
        }
    }

    public static class TCDashCreeperContext implements TCCreeperContext<TCDashCreeper> {
        private static final String NAME = "dashcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCDashCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "はしりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Dash! Faster than lightning!!";
        }

        @Override
        public String getJaJPDesc() {
            return "その匠、最速につき。";
        }

        @Override
        public String getEnUSName() {
            return "Dash Creeper";
        }

        @Override
        public String getJaJPName() {
            return "走匠";
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
            return 0xaa0000;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00ffff;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 1).add(Attributes.MOVEMENT_SPEED, 1.5).add(Attributes.KNOCKBACK_RESISTANCE, 1000000);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WATER_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCBlockCore.CREEPER_BOMB;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1.0F, 1.0F);
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(TCEntityCore.NETHER_TAKUMIS);
            return list;
        }

        @Override
        public int getSpawnWeight() {
            return TCCreeperContext.super.getSpawnWeight() / 4;
        }
    }
}
