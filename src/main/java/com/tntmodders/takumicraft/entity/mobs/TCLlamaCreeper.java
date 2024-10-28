package com.tntmodders.takumicraft.entity.mobs;

import com.mojang.serialization.Codec;
import com.tntmodders.takumicraft.client.renderer.entity.TCLlamaCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.projectile.TCLlamaCreeperSpit;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;
import java.util.function.IntFunction;

public class TCLlamaCreeper extends AbstractTCCreeper implements VariantHolder<TCLlamaCreeper.Variant>, RangedAttackMob {
    private static final int MAX_STRENGTH = 5;
    private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID = SynchedEntityData.defineId(TCLlamaCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(TCLlamaCreeper.class, EntityDataSerializers.INT);
    private static final EntityDimensions BABY_DIMENSIONS = EntityType.LLAMA.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityType.LLAMA.getHeight() - 0.8125F, -0.3F)).scale(0.5F);
    boolean didSpit;

    public TCLlamaCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.LLAMA;
    }

    private void setStrength(int p_30841_) {
        this.entityData.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, p_30841_)));
    }

    private void setRandomStrength(RandomSource p_218818_) {
        int i = p_218818_.nextFloat() < 0.04F ? 5 : 3;
        this.setStrength(1 + p_218818_.nextInt(i));
    }

    public int getStrength() {
        return this.entityData.get(DATA_STRENGTH_ID);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_30793_) {
        super.addAdditionalSaveData(p_30793_);
        p_30793_.putInt("Variant", this.getVariant().id);
        p_30793_.putInt("Strength", this.getStrength());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_30780_) {
        this.setStrength(p_30780_.getInt("Strength"));
        super.readAdditionalSaveData(p_30780_);
        this.setVariant(TCLlamaCreeper.Variant.byId(p_30780_.getInt("Variant")));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0F));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.25, p_331564_ -> p_331564_.is(ItemTags.LLAMA_TEMPT_ITEMS), false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new TCLlamaCreeper.LlamaHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new TCLlamaCreeper.LlamaAttackWolfGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.JUMP_STRENGTH, 0.9).add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.225F).add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.SAFE_FALL_DISTANCE, 6.0).add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.5).add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_327895_) {
        super.defineSynchedData(p_327895_);
        p_327895_.define(DATA_STRENGTH_ID, 0);
        p_327895_.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public TCLlamaCreeper.Variant getVariant() {
        return TCLlamaCreeper.Variant.byId(this.entityData.get(DATA_VARIANT_ID));
    }

    @Override
    public void setVariant(TCLlamaCreeper.Variant p_262628_) {
        this.entityData.set(DATA_VARIANT_ID, p_262628_.id);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_30774_, DifficultyInstance p_30775_, EntitySpawnReason p_30776_, @Nullable SpawnGroupData p_30777_) {
        RandomSource randomsource = p_30774_.getRandom();
        this.setRandomStrength(randomsource);
        TCLlamaCreeper.Variant llama$variant;
        if (p_30777_ instanceof TCLlamaCreeper.LlamaGroupData) {
            llama$variant = ((TCLlamaCreeper.LlamaGroupData) p_30777_).variant;
        } else {
            llama$variant = Util.getRandom(TCLlamaCreeper.Variant.values(), randomsource);
            p_30777_ = new TCLlamaCreeper.LlamaGroupData(llama$variant);
        }

        this.setVariant(llama$variant);
        return super.finalizeSpawn(p_30774_, p_30775_, p_30776_, p_30777_);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_30803_) {
        return SoundEvents.LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos p_30790_, BlockState p_30791_) {
        this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean canUseSlot(EquipmentSlot p_344756_) {
        return true;
    }

    @Override
    public boolean isBodyArmorItem(ItemStack p_30834_) {
        return p_30834_.is(ItemTags.WOOL_CARPETS);
    }

    @Nullable
    private static DyeColor getDyeColor(ItemStack p_30836_) {
        Block block = Block.byItem(p_30836_.getItem());
        return block instanceof WoolCarpetBlock ? ((WoolCarpetBlock) block).getColor() : null;
    }

    @Nullable
    public DyeColor getSwag() {
        return getDyeColor(this.getItemBySlot(EquipmentSlot.BODY));
    }

    private void spit(LivingEntity p_30828_) {
        LlamaSpit llamaspit = new TCLlamaCreeperSpit(this.level(), this);
        double d0 = p_30828_.getX() - this.getX();
        double d1 = p_30828_.getY(0.3333333333333333) - llamaspit.getY();
        double d2 = p_30828_.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2) * 0.2F;
        llamaspit.shoot(d0, d1 + d3, d2, 1.5F, 10.0F);
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        this.level().addFreshEntity(llamaspit);
        this.didSpit = true;
    }

    void setDidSpit(boolean p_30753_) {
        this.didSpit = p_30753_;
    }

    @Override
    public boolean causeFallDamage(float p_149538_, float p_149539_, DamageSource p_149540_) {
        var event = net.minecraftforge.event.ForgeEventFactory.onLivingFall(this, p_149538_, p_149539_);
        if (event.isCanceled()) return false;
        p_149538_ = event.getDistance();
        p_149539_ = event.getDamageMultiplier();
        int i = this.calculateFallDamage(p_149538_, p_149539_);
        if (i <= 0) {
            return false;
        } else {
            if (p_149538_ >= 6.0F) {
                this.hurt(p_149540_, (float) i);
                if (this.isVehicle()) {
                    for (Entity entity : this.getIndirectPassengers()) {
                        entity.hurt(p_149540_, (float) i);
                    }
                }
            }

            this.playBlockFallSound();
            return true;
        }
    }

    @Override
    protected double followLeashSpeed() {
        return 2.0;
    }

    @Override
    public void performRangedAttack(LivingEntity p_30762_, float p_30763_) {
        this.spit(p_30762_);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.75 * (double) this.getEyeHeight(), (double) this.getBbWidth() * 0.5);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose p_332334_) {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(p_332334_);
    }

    static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal<Wolf> {
        public LlamaAttackWolfGoal(TCLlamaCreeper p_30843_) {
            super(p_30843_, Wolf.class, 16, false, true, p_326997_ -> !((Wolf) p_326997_).isTame());
        }

        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.25;
        }
    }

    static class LlamaGroupData extends AgeableMob.AgeableMobGroupData {
        public final TCLlamaCreeper.Variant variant;

        LlamaGroupData(TCLlamaCreeper.Variant p_262658_) {
            super(true);
            this.variant = p_262658_;
        }
    }

    static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
        public LlamaHurtByTargetGoal(TCLlamaCreeper p_30854_) {
            super(p_30854_);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.mob instanceof TCLlamaCreeper llama && llama.didSpit) {
                llama.setDidSpit(false);
                return false;
            }

            return super.canContinueToUse();
        }
    }

    public enum Variant implements StringRepresentable {
        CREAMY(0, "creamy"), WHITE(1, "white"), BROWN(2, "brown"), GRAY(3, "gray");

        public static final Codec<TCLlamaCreeper.Variant> CODEC = StringRepresentable.fromEnum(TCLlamaCreeper.Variant::values);
        private static final IntFunction<TCLlamaCreeper.Variant> BY_ID = ByIdMap.continuous(TCLlamaCreeper.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        final int id;
        private final String name;

        Variant(final int p_262677_, final String p_262641_) {
            this.id = p_262677_;
            this.name = p_262641_;
        }

        public int getId() {
            return this.id;
        }

        public static TCLlamaCreeper.Variant byId(int p_262608_) {
            return BY_ID.apply(p_262608_);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static class TCLlamaCreeperContext implements TCCreeperContext<TCLlamaCreeper> {
        private static final String NAME = "llamacreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCLlamaCreeper::new, MobCategory.MONSTER).sized(0.9F, 1.87F).eyeHeight(1.7765F).passengerAttachments(new Vec3(0.0, 1.37, -0.3)).clientTrackingRange(10).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "りゃまたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper looks like a llama. Explosive spit.";
        }

        @Override
        public String getJaJPDesc() {
            return "リャマ型の匠。爆発性の唾を飛ばし、プレイヤーを蹴散らす。";
        }

        @Override
        public String getEnUSName() {
            return "Llama Creeper";
        }

        @Override
        public String getJaJPName() {
            return "駱馬匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x55ff55;
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
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractTCCreeper::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCLlamaCreeper>) type, TCLlamaCreeperRenderer::new);
        }
    }
}
