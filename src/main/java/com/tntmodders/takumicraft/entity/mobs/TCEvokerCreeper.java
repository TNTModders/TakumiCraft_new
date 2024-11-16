package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCEvokerCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import javax.annotation.Nullable;
import java.util.List;

public class TCEvokerCreeper extends AbstractTCSpellcasterCreeper {
    @Nullable
    private Sheep wololoTarget;
    @Nullable
    private TCSheepCreeper wololoCreeperTarget;

    public TCEvokerCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 12.0).add(Attributes.MAX_HEALTH, 24.0);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.EVOKER;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().clear();
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof TCSheepCreeper creeper) {
                creeper.ignite();
            }
        });
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TCEvokerCreeper.EvokerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6, 1.0));
        this.goalSelector.addGoal(4, new TCEvokerCreeper.EvokerSummonSpellGoal());
        this.goalSelector.addGoal(5, new TCEvokerCreeper.EvokerAttackSpellGoal());
        this.goalSelector.addGoal(6, new TCEvokerCreeper.EvokerWololoSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_333469_) {
        super.defineSynchedData(p_333469_);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_32642_) {
        super.readAdditionalSaveData(p_32642_);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_32646_) {
        super.addAdditionalSaveData(p_32646_);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
    }

    @Override
    public boolean considersEntityAsAlly(Entity p_32665_) {
        if (p_32665_ == null) {
            return false;
        } else if (p_32665_ == this) {
            return true;
        } else if (super.considersEntityAsAlly(p_32665_)) {
            return true;
        } else {
            return p_32665_ instanceof TCVexCreeper vex && this.considersEntityAsAlly(vex.getOwner());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_32654_) {
        return SoundEvents.EVOKER_HURT;
    }

    @Nullable
    Sheep getWololoTarget() {
        return this.wololoTarget;
    }

    void setWololoTarget(@Nullable Sheep p_32635_) {
        this.wololoTarget = p_32635_;
    }

    @Nullable
    TCSheepCreeper getWololoCreeperTarget() {
        return this.wololoCreeperTarget;
    }

    void setWololoCreeperTarget(@Nullable TCSheepCreeper p_32635_) {
        this.wololoCreeperTarget = p_32635_;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    public static class TCEvokerCreeperContext implements TCCreeperContext<TCEvokerCreeper> {
        private static final String NAME = "evokercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCEvokerCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "えゔぉーかーたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Magician of illager, explosive spell and strong servant.";
        }

        @Override
        public String getJaJPDesc() {
            return "魔道士は館で目覚め、蠢き始める。闇夜疾風、黒を呼ぶ。";
        }

        @Override
        public String getEnUSName() {
            return "Evoker Creeper";
        }

        @Override
        public String getJaJPName() {
            return "エヴォーカー匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x336633;
        }

        @Override
        public int getSecondaryColor() {
            return 0xbb99bb;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCEvokerCreeper>) type, TCEvokerCreeperRenderer::new);
        }
    }

    class EvokerAttackSpellGoal extends AbstractTCSpellcasterCreeper.SpellcasterUseSpellGoal {
        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity livingentity = TCEvokerCreeper.this.getTarget();
            double d0 = Math.min(livingentity.getY(), TCEvokerCreeper.this.getY());
            double d1 = Math.max(livingentity.getY(), TCEvokerCreeper.this.getY()) + 1.0;
            float f = (float) Mth.atan2(livingentity.getZ() - TCEvokerCreeper.this.getZ(), livingentity.getX() - TCEvokerCreeper.this.getX());
            if (TCEvokerCreeper.this.distanceToSqr(livingentity) < 9.0) {
                for (int i = 0; i < 5; i++) {
                    float f1 = f + (float) i * (float) Math.PI * 0.4F;
                    this.createSpellEntity(
                            TCEvokerCreeper.this.getX() + (double) Mth.cos(f1) * 1.5, TCEvokerCreeper.this.getZ() + (double) Mth.sin(f1) * 1.5, d0, d1, f1, 0
                    );
                }

                for (int k = 0; k < 8; k++) {
                    float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + (float) (Math.PI * 2.0 / 5.0);
                    this.createSpellEntity(
                            TCEvokerCreeper.this.getX() + (double) Mth.cos(f2) * 2.5, TCEvokerCreeper.this.getZ() + (double) Mth.sin(f2) * 2.5, d0, d1, f2, 3
                    );
                }
            } else {
                for (int l = 0; l < 16; l++) {
                    double d2 = 1.25 * (double) (l + 1);
                    int j = l;
                    this.createSpellEntity(TCEvokerCreeper.this.getX() + (double) Mth.cos(f) * d2, TCEvokerCreeper.this.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, j);
                }
            }
        }

        private void createSpellEntity(double p_32673_, double p_32674_, double p_32675_, double p_32676_, float p_32677_, int p_32678_) {
            BlockPos blockpos = BlockPos.containing(p_32673_, p_32676_, p_32674_);
            boolean flag = false;
            double d0 = 0.0;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = TCEvokerCreeper.this.level().getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(TCEvokerCreeper.this.level(), blockpos1, Direction.UP)) {
                    if (!TCEvokerCreeper.this.level().isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = TCEvokerCreeper.this.level().getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(TCEvokerCreeper.this.level(), blockpos);
                        if (!voxelshape.isEmpty()) {
                            d0 = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(p_32675_) - 1);

            if (flag) {
                EvokerFangs fangs = new EvokerFangs(TCEvokerCreeper.this.level(), p_32673_, (double) blockpos.getY() + d0, p_32674_, p_32677_, p_32678_, TCEvokerCreeper.this);
                TCEvokerCreeper.this.level().addFreshEntity(fangs);
                TCEvokerCreeper.this.level().gameEvent(GameEvent.ENTITY_PLACE, new Vec3(p_32673_, (double) blockpos.getY() + d0, p_32674_), GameEvent.Context.of(TCEvokerCreeper.this));
                TCExplosionUtils.createExplosion(level(), TCEvokerCreeper.this, fangs.getX(), fangs.getY(), fangs.getZ(), 1f, false);
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected AbstractTCSpellcasterCreeper.IllagerSpell getSpell() {
            return AbstractTCSpellcasterCreeper.IllagerSpell.FANGS;
        }
    }

    class EvokerCastingSpellGoal extends AbstractTCSpellcasterCreeper.SpellcasterCastingSpellGoal {
        @Override
        public void tick() {
            if (TCEvokerCreeper.this.getTarget() != null) {
                TCEvokerCreeper.this.getLookControl().setLookAt(TCEvokerCreeper.this.getTarget(), (float) TCEvokerCreeper.this.getMaxHeadYRot(), (float) TCEvokerCreeper.this.getMaxHeadXRot());
            } else if (TCEvokerCreeper.this.getWololoTarget() != null) {
                TCEvokerCreeper.this.getLookControl().setLookAt(TCEvokerCreeper.this.getWololoTarget(), (float) TCEvokerCreeper.this.getMaxHeadYRot(), (float) TCEvokerCreeper.this.getMaxHeadXRot());
            } else if (TCEvokerCreeper.this.getWololoCreeperTarget() != null) {
                TCEvokerCreeper.this.getLookControl().setLookAt(TCEvokerCreeper.this.getWololoCreeperTarget(), (float) TCEvokerCreeper.this.getMaxHeadYRot(), (float) TCEvokerCreeper.this.getMaxHeadXRot());
            }
        }
    }

    class EvokerSummonSpellGoal extends AbstractTCSpellcasterCreeper.SpellcasterUseSpellGoal {
        private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting();

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = getServerLevel(TCEvokerCreeper.this).getNearbyEntities(Vex.class, this.vexCountTargeting, TCEvokerCreeper.this, TCEvokerCreeper.this.getBoundingBox().inflate(16.0)).size();
                return TCEvokerCreeper.this.random.nextInt(8) + 1 > i;
            }
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Override
        protected void performSpellCasting() {
            ServerLevel serverlevel = (ServerLevel) TCEvokerCreeper.this.level();
            PlayerTeam playerteam = TCEvokerCreeper.this.getTeam();

            for (int i = 0; i < 3; i++) {
                BlockPos blockpos = TCEvokerCreeper.this.blockPosition().offset(-2 + TCEvokerCreeper.this.random.nextInt(5), 1, -2 + TCEvokerCreeper.this.random.nextInt(5));
                Entity entity = TCEntityCore.VEX.entityType().create(TCEvokerCreeper.this.level(), EntitySpawnReason.MOB_SUMMONED);
                if (entity instanceof TCVexCreeper vex) {
                    vex.moveTo(blockpos, 0.0F, 0.0F);
                    vex.finalizeSpawn(serverlevel, TCEvokerCreeper.this.level().getCurrentDifficultyAt(blockpos), EntitySpawnReason.MOB_SUMMONED, null);
                    vex.setOwner(TCEvokerCreeper.this);
                    vex.setBoundOrigin(blockpos);
                    vex.setLimitedLife(20 * (30 + TCEvokerCreeper.this.random.nextInt(90)));
                    if (playerteam != null) {
                        serverlevel.getScoreboard().addPlayerToTeam(vex.getScoreboardName(), playerteam);
                    }

                    serverlevel.addFreshEntityWithPassengers(vex);
                    serverlevel.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(TCEvokerCreeper.this));
                }
            }
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected AbstractTCSpellcasterCreeper.IllagerSpell getSpell() {
            return AbstractTCSpellcasterCreeper.IllagerSpell.SUMMON_VEX;
        }
    }

    public class EvokerWololoSpellGoal extends AbstractTCSpellcasterCreeper.SpellcasterUseSpellGoal {
        private final TargetingConditions wololoTargeting = TargetingConditions.forNonCombat()
                .range(16.0)
                .selector((entity, level) -> ((Sheep) entity).getColor() == DyeColor.BLUE);
        private final TargetingConditions wololoCreeperTargeting = TargetingConditions.forNonCombat()
                .range(16.0)
                .selector((entity, level) -> ((TCSheepCreeper) entity).getColor() == DyeColor.BLUE && !((TCSheepCreeper) entity).isRainbow());

        @Override
        public boolean canUse() {
            if (TCEvokerCreeper.this.getTarget() != null) {
                return false;
            } else if (TCEvokerCreeper.this.isCastingSpell()) {
                return false;
            } else if (TCEvokerCreeper.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(getServerLevel(TCEvokerCreeper.this), TCEvokerCreeper.this)) {
                return false;
            } else {
                List<Sheep> list = getServerLevel(TCEvokerCreeper.this).getNearbyEntities(Sheep.class, this.wololoTargeting, TCEvokerCreeper.this, TCEvokerCreeper.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
                List<TCSheepCreeper> list1 = getServerLevel(TCEvokerCreeper.this).getNearbyEntities(TCSheepCreeper.class, this.wololoCreeperTargeting, TCEvokerCreeper.this, TCEvokerCreeper.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
                if (list.isEmpty() && list1.isEmpty()) {
                    return false;
                } else if (!list1.isEmpty()) {
                    TCEvokerCreeper.this.setWololoCreeperTarget(list1.get(TCEvokerCreeper.this.random.nextInt(list1.size())));
                    return true;
                } else {
                    TCEvokerCreeper.this.setWololoTarget(list.get(TCEvokerCreeper.this.random.nextInt(list.size())));
                    return true;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return (TCEvokerCreeper.this.getWololoTarget() != null || TCEvokerCreeper.this.getWololoCreeperTarget() != null) && this.attackWarmupDelay > 0;
        }

        @Override
        public void stop() {
            super.stop();
            TCEvokerCreeper.this.setWololoTarget(null);
            TCEvokerCreeper.this.setWololoCreeperTarget(null);
        }

        @Override
        protected void performSpellCasting() {
            Sheep sheep = TCEvokerCreeper.this.getWololoTarget();
            if (sheep != null && sheep.isAlive()) {
                sheep.setColor(DyeColor.RED);
            }
            TCSheepCreeper sheepCreeper = TCEvokerCreeper.this.getWololoCreeperTarget();
            if (sheepCreeper != null && sheepCreeper.isAlive()) {
                sheepCreeper.setRainbow();
            }
        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 140;
        }

        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected AbstractTCSpellcasterCreeper.IllagerSpell getSpell() {
            return AbstractTCSpellcasterCreeper.IllagerSpell.WOLOLO;
        }
    }
}
