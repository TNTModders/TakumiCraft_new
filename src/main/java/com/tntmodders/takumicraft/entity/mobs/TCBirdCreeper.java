package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCBirdCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.projectile.TCBirdBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public class TCBirdCreeper extends AbstractTCCreeper {

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    private float nextFlap = 1.0F;

    public TCBirdCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }
        this.flap += this.flapping * 2.0F;
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_28262_) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos p_28254_, BlockState p_28255_) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    protected void positionRider(Entity p_289537_, Entity.MoveFunction p_289541_) {
        super.positionRider(p_289537_, p_289541_);
        if (p_289537_ instanceof LivingEntity) {
            ((LivingEntity) p_289537_).yBodyRot = this.yBodyRot;
        }
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity p_298807_, EntityDimensions p_298266_, float p_300701_) {
        return new Vec3(0.0F, p_298266_.height(), -0.1F * p_300701_);
    }

    @Override
    protected float getJumpPower() {
        return super.getJumpPower() * 2;
    }

    @Override
    public boolean causeFallDamage(float p_149687_, float p_149688_, DamageSource p_149689_) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSwellDir() > 0 && this.onGround()) {
            this.jumpFromGround();
        }
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide) {
            TCBirdBomb birdBomb = new TCBirdBomb(this, this.level());
            birdBomb.setDeltaMovement(Vec3.ZERO);
            this.level().addFreshEntity(birdBomb);
        }
        this.setSwellDir(-1);
        this.swell = 0;
        this.entityData.set(DATA_IS_IGNITED, false);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BIRD;
    }

    public static class TCBirdCreeperContext implements TCCreeperContext<TCBirdCreeper> {
        private static final String NAME = "birdcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBirdCreeper::new, MobCategory.MONSTER).sized(0.4F, 0.7F).clientTrackingRange(10).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "とりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper looks like a bird. Fly, and drop a egg.";
        }

        @Override
        public String getJaJPDesc() {
            return "鳥型の匠。飛ぶ鳥も落とす、タマゴばくだんは怒りとともに。";
        }

        @Override
        public String getEnUSName() {
            return "Bird Creeper";
        }

        @Override
        public String getJaJPName() {
            return "鳥匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaaaa66;
        }

        @Override
        public int getSecondaryColor() {
            return 0xccffcc;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCBirdCreeper>) type, TCBirdCreeperRenderer::new);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractTCCreeper::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
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
            return this.getRank().getSpawnWeight();
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.COOKED_CHICKEN;
        }
    }
}
