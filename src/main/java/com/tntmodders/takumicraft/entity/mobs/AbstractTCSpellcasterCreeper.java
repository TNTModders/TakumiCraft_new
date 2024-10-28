package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.utils.TCEntityUtils;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.IntFunction;

public abstract class AbstractTCSpellcasterCreeper extends AbstractTCIllagerCreeper {
    private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(AbstractTCSpellcasterCreeper.class, EntityDataSerializers.BYTE);
    protected int spellCastingTickCount;
    private AbstractTCSpellcasterCreeper.IllagerSpell currentSpell = AbstractTCSpellcasterCreeper.IllagerSpell.NONE;


    public AbstractTCSpellcasterCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335149_) {
        super.defineSynchedData(p_335149_);
        p_335149_.define(DATA_SPELL_CASTING_ID, (byte) 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_33732_) {
        super.readAdditionalSaveData(p_33732_);
        this.spellCastingTickCount = p_33732_.getInt("SpellTicks");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_33734_) {
        super.addAdditionalSaveData(p_33734_);
        p_33734_.putInt("SpellTicks", this.spellCastingTickCount);
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isCastingSpell()) {
            return AbstractIllager.IllagerArmPose.SPELLCASTING;
        } else {
            return AbstractIllager.IllagerArmPose.CROSSED;
        }
    }

    public boolean isCastingSpell() {
        return this.level().isClientSide ? this.entityData.get(DATA_SPELL_CASTING_ID) > 0 : this.spellCastingTickCount > 0;
    }

    public void setIsCastingSpell(AbstractTCSpellcasterCreeper.IllagerSpell p_33728_) {
        this.currentSpell = p_33728_;
        this.entityData.set(DATA_SPELL_CASTING_ID, (byte) p_33728_.id);
    }

    protected AbstractTCSpellcasterCreeper.IllagerSpell getCurrentSpell() {
        return !this.level().isClientSide ? this.currentSpell : AbstractTCSpellcasterCreeper.IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
    }
    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (this.spellCastingTickCount > 0) {
            this.spellCastingTickCount--;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && this.isCastingSpell()) {
            AbstractTCSpellcasterCreeper.IllagerSpell spellcasterillager$illagerspell = this.getCurrentSpell();
            float f = (float) spellcasterillager$illagerspell.spellColor[0];
            float f1 = (float) spellcasterillager$illagerspell.spellColor[1];
            float f2 = (float) spellcasterillager$illagerspell.spellColor[2];
            float f3 = this.yBodyRot * (float) (Math.PI / 180.0) + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
            float f4 = Mth.cos(f3);
            float f5 = Mth.sin(f3);
            double d0 = 0.6 * (double) this.getScale();
            double d1 = 1.8 * (double) this.getScale();
            this.level()
                    .addParticle(
                            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, f, f1, f2),
                            this.getX() + (double) f4 * d0,
                            this.getY() + d1,
                            this.getZ() + (double) f5 * d0,
                            0.0,
                            0.0,
                            0.0
                    );
            this.level()
                    .addParticle(
                            ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, f, f1, f2),
                            this.getX() - (double) f4 * d0,
                            this.getY() + d1,
                            this.getZ() - (double) f5 * d0,
                            0.0,
                            0.0,
                            0.0
                    );
        }
    }

    protected int getSpellCastingTime() {
        return this.spellCastingTickCount;
    }

    protected abstract SoundEvent getCastingSoundEvent();

    protected enum IllagerSpell {
        NONE(0, 0.0, 0.0, 0.0),
        SUMMON_VEX(1, 0.7, 0.7, 0.8),
        FANGS(2, 0.4, 0.3, 0.35),
        WOLOLO(3, 0.7, 0.5, 0.2),
        DISAPPEAR(4, 0.3, 0.3, 0.8),
        BLINDNESS(5, 0.1, 0.1, 0.2);

        private static final IntFunction<AbstractTCSpellcasterCreeper.IllagerSpell> BY_ID = ByIdMap.continuous(
                p_263091_ -> p_263091_.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
        final int id;
        final double[] spellColor;

        IllagerSpell(final int p_33754_, final double p_33755_, final double p_33756_, final double p_33757_) {
            this.id = p_33754_;
            this.spellColor = new double[]{p_33755_, p_33756_, p_33757_};
        }

        public static AbstractTCSpellcasterCreeper.IllagerSpell byId(int p_33759_) {
            return BY_ID.apply(p_33759_);
        }
    }

    protected class SpellcasterCastingSpellGoal extends Goal {
        public SpellcasterCastingSpellGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return AbstractTCSpellcasterCreeper.this.getSpellCastingTime() > 0;
        }

        @Override
        public void start() {
            super.start();
            AbstractTCSpellcasterCreeper.this.navigation.stop();
        }

        @Override
        public void stop() {
            super.stop();
            AbstractTCSpellcasterCreeper.this.setIsCastingSpell(AbstractTCSpellcasterCreeper.IllagerSpell.NONE);
        }

        @Override
        public void tick() {
            if (AbstractTCSpellcasterCreeper.this.getTarget() != null) {
                AbstractTCSpellcasterCreeper.this.getLookControl()
                        .setLookAt(AbstractTCSpellcasterCreeper.this.getTarget(), (float) AbstractTCSpellcasterCreeper.this.getMaxHeadYRot(), (float) AbstractTCSpellcasterCreeper.this.getMaxHeadXRot());
            }
        }
    }

    protected abstract class SpellcasterUseSpellGoal extends Goal {
        protected int attackWarmupDelay;
        protected int nextAttackTickCount;

        @Override
        public boolean canUse() {
            LivingEntity livingentity = AbstractTCSpellcasterCreeper.this.getTarget();
            if (livingentity == null || !livingentity.isAlive()) {
                return false;
            } else {
                return !AbstractTCSpellcasterCreeper.this.isCastingSpell() && AbstractTCSpellcasterCreeper.this.tickCount >= this.nextAttackTickCount;
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = AbstractTCSpellcasterCreeper.this.getTarget();
            return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            AbstractTCSpellcasterCreeper.this.spellCastingTickCount = this.getCastingTime();
            this.nextAttackTickCount = AbstractTCSpellcasterCreeper.this.tickCount + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                AbstractTCSpellcasterCreeper.this.playSound(soundevent, 1.0F, 1.0F);
            }

            AbstractTCSpellcasterCreeper.this.setIsCastingSpell(this.getSpell());
        }

        @Override
        public void tick() {
            this.attackWarmupDelay--;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                AbstractTCSpellcasterCreeper.this.playSound(AbstractTCSpellcasterCreeper.this.getCastingSoundEvent(), 1.0F, 1.0F);
            }
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract AbstractTCSpellcasterCreeper.IllagerSpell getSpell();
    }

}
