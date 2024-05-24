package com.tntmodders.takumicraft.entity.misc;

import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TCKingStorm extends PathfinderMob {

    public static final EntityType<TCKingStorm> KING_STORM = EntityType.Builder.of(TCKingStorm::new, MobCategory.MISC)
            .sized(1f, 2f).clientTrackingRange(4).updateInterval(20).build("king_storm");

    protected TCKingStorm(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1).add(Attributes.FOLLOW_RANGE, 100).add(Attributes.KNOCKBACK_RESISTANCE, 1000).add(Attributes.MAX_HEALTH, 1).add(Attributes.ATTACK_DAMAGE, 0);
    }

    @Override
    protected float getWaterSlowDown() {
        return 1f;
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.setInvisible(true);
        if (!this.level().isClientSide()) {
            for (int i = 0; i <= 5; i++) {
                for (int d = 0; d < 12; d++) {
                    if (this.getRandom().nextInt((6 - i) * 2) == 0) {
                        double x = Math.cos(Math.toRadians(d * 30)) * i / 3;
                        double z = Math.sin(Math.toRadians(d * 30)) * i / 3;
                        TCExplosionUtils.createExplosion(this.level(), this, this.getX(x * Math.abs(x) * i / 4.5), this.getY(i / 1.5), this.getZ(z * Math.abs(z) * i / 4.5), 0f, false);
                    }
                }
            }
        }
        if (this.tickCount > 100) {
            this.discard();
        }
    }

    @Override
    public void playerTouch(Player entityIn) {
        super.playerTouch(entityIn);
        TCExplosionUtils.createExplosion(this.level(), this, entityIn.getX(), entityIn.getY(), entityIn.getZ(), 0f, false);
        entityIn.setDeltaMovement(0, 5f, 0);
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }
}
