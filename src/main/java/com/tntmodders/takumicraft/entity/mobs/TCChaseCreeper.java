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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TCChaseCreeper extends AbstractTCCreeper {

    public TCChaseCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 6;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.CHASE;
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, Level.ExplosionInteraction.MOB);
            this.spawnLingeringCloud();

            if (this.getHealth() > 1) {
                TCChaseCreeper chaseCreeper = (TCChaseCreeper) TCEntityCore.CHASE.entityType().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                chaseCreeper.copyPosition(this);
                chaseCreeper.setTarget(this.getTarget());
                chaseCreeper.setHealth(this.getHealth() - 1);
                if (this.isPowered()) {
                    chaseCreeper.setPowered(true);
                }
                this.level().addFreshEntity(chaseCreeper);
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                this.triggerOnDeathMobEffects(serverLevel, RemovalReason.KILLED);
            }
            this.discard();
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        Stream<Entity> stream = event.getAffectedEntities().stream().filter(entity -> entity instanceof LivingEntity).sorted(Comparator.comparingDouble(value -> value.getRandom().nextDouble()));
        Optional<Entity> target = stream.findAny();
        target.ifPresent(entity -> this.setTarget((LivingEntity) entity));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.FALL) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN)) {
            return false;
        } else {
            if (source.getEntity() instanceof LivingEntity living && this.getTarget() != living) {
                this.setTarget(living);
            }
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
        } else if (this.getTarget() != null) {
            this.getMoveControl().setWantedPosition(this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ(), 1.5f);
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, EntitySpawnReason spawnType) {
        return super.checkSpawnRules(levelAccessor, spawnType) && levelAccessor.getRandom().nextInt(5) == 0;
    }

    public static class TCChaseCreeperContext implements TCCreeperContext<TCChaseCreeper> {
        private static final String NAME = "chasecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCChaseCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ついしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "Chase!! Faster than sound!!";
        }

        @Override
        public String getJaJPDesc() {
            return "その匠、最俊につき。";
        }

        @Override
        public String getEnUSName() {
            return "Chase Creeper";
        }

        @Override
        public String getJaJPName() {
            return "追匠";
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
            return 0x00ffaa;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaa0000;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 10).add(Attributes.MOVEMENT_SPEED, 1.5).add(Attributes.KNOCKBACK_RESISTANCE, 1000000);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS_D;
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
