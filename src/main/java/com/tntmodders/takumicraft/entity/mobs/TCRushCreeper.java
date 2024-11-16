package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
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

import java.util.List;

public class TCRushCreeper extends AbstractTCCreeper {

    public TCRushCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 6;
    }

    @Override
    public void explodeCreeper() {
        if (this.level() instanceof ServerLevel serverLevel) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            for (int i = 0; i < (this.isPowered() ? 5 : 3); i++) {
                this.level().explode(this, this.getX(), this.getY() - 0.25f, this.getZ(), (float) this.explosionRadius * f, Level.ExplosionInteraction.MOB);
            }
            this.spawnLingeringCloud();
            this.triggerOnDeathMobEffects(serverLevel, Entity.RemovalReason.KILLED);
            this.discard();
        }
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.RUSH;
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
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, EntitySpawnReason spawnType) {
        return super.checkSpawnRules(levelAccessor, spawnType) && levelAccessor.getRandom().nextInt(5) == 0;
    }

    public static class TCRushCreeperContext implements TCCreeperContext<TCRushCreeper> {
        private static final String NAME = "rushcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCRushCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "とつたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Rush!! Faster than wind!!";
        }

        @Override
        public String getJaJPDesc() {
            return "その匠、最強につき。";
        }

        @Override
        public String getEnUSName() {
            return "Rush Creeper";
        }

        @Override
        public String getJaJPName() {
            return "突匠";
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
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 25).add(Attributes.MOVEMENT_SPEED, 1.5).add(Attributes.KNOCKBACK_RESISTANCE, 1000000);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.FIRE_D;
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
