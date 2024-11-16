package com.tntmodders.takumicraft.entity.mobs;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCAllayCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.ai.allay.TCAllayCreeperAi;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;

public class TCAllayCreeper extends AbstractTCCreeper implements InventoryCarrier, VibrationSystem {
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(
            0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, 2.5F, 3.0F, 3.75F, 4.0F
    );
    protected static final ImmutableList<SensorType<? extends Sensor<? super TCAllayCreeper>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.LIKED_PLAYER,
            MemoryModuleType.LIKED_NOTEBLOCK_POSITION,
            MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
            MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
            MemoryModuleType.IS_PANICKING
    );
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
    private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
    private static final float DANCING_LOOP_DURATION = 55.0F;
    private static final float SPINNING_ANIMATION_DURATION = 15.0F;
    private static final int NUM_OF_DUPLICATION_HEARTS = 3;
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(TCAllayCreeper.class, EntityDataSerializers.BOOLEAN);
    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicVibrationListener;
    private final VibrationSystem.User vibrationUser;
    private final DynamicGameEventListener<TCAllayCreeper.JukeboxListener> dynamicJukeboxListener;
    private final SimpleContainer inventory = new SimpleContainer(1);
    private VibrationSystem.Data vibrationData;
    @Nullable
    private BlockPos jukeboxPos;
    private long duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;

    public TCAllayCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        this.vibrationUser = new TCAllayCreeper.VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.dynamicVibrationListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        this.dynamicJukeboxListener = new DynamicGameEventListener<>(new TCAllayCreeper.JukeboxListener(this.vibrationUser.getPositionSource(), GameEvent.JUKEBOX_PLAY.value().notificationRadius()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FLYING_SPEED, 0.1F)
                .add(Attributes.MOVEMENT_SPEED, 0.1F)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected Brain.Provider<TCAllayCreeper> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> p_218344_) {
        return TCAllayCreeperAi.makeBrain(this.brainProvider().makeBrain(p_218344_));
    }

    @Override
    public Brain<TCAllayCreeper> getBrain() {
        return (Brain<TCAllayCreeper>) super.getBrain();
    }

    @Override
    protected PathNavigation createNavigation(Level p_218342_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_218342_);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_332593_) {
        super.defineSynchedData(p_332593_);
        p_332593_.define(DATA_DANCING, false);
    }

    @Override
    public void travel(Vec3 p_218382_) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                this.moveRelative(this.getSpeed(), p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    protected void playStepSound(BlockPos p_218364_, BlockState p_218365_) {
    }

    @Override
    protected void checkFallDamage(double p_218316_, boolean p_218317_, BlockState p_218318_, BlockPos p_218319_) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_218369_) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void customServerAiStep(ServerLevel p_367271_) {
        ProfilerFiller profilerfiller = Profiler.get();
        profilerfiller.push("allayBrain");
        this.getBrain().tick(p_367271_, this);
        profilerfiller.pop();
        profilerfiller.push("allayActivityUpdate");
        TCAllayCreeperAi.updateActivity(this);
        profilerfiller.pop();
        super.customServerAiStep(p_367271_);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }

        if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
            this.setDancing(false);
            this.jukeboxPos = null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getMainHandItem().is(Items.TNT)) {
            this.setItemInHand(InteractionHand.MAIN_HAND, Items.TNT.getDefaultInstance());
        }
        if (this.level().isClientSide) {
            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
            if (this.hasItemInHand()) {
                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
            }

            if (this.isDancing()) {
                this.dancingAnimationTicks++;
                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
                if (this.isSpinning()) {
                    this.spinningAnimationTicks++;
                } else {
                    this.spinningAnimationTicks--;
                }

                this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
            } else {
                this.dancingAnimationTicks = 0.0F;
                this.spinningAnimationTicks = 0.0F;
                this.spinningAnimationTicks0 = 0.0F;
            }
        } else {
            VibrationSystem.Ticker.tick(this.level(), this.vibrationData, this.vibrationUser);
            if (this.isPanicking()) {
                this.setDancing(false);
            }
            if (this.isPowered() && !this.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 30));
            }
            if (this.getTarget() != null) {
                this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, this.getTarget().getUUID());
            }
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return !this.isOnPickupCooldown() && this.hasItemInHand();
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    @Override
    protected boolean canDispenserEquipIntoSlot(EquipmentSlot p_360851_) {
        return false;
    }

    private boolean isOnPickupCooldown() {
        return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
    }

    public void setJukeboxPlaying(BlockPos p_240102_, boolean p_240103_) {
        if (p_240103_) {
            if (!this.isDancing()) {
                this.jukeboxPos = p_240102_;
                this.setDancing(true);
            }
        } else if (p_240102_.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    @Override
    public boolean wantsToPickUp(ServerLevel p_363882_, ItemStack p_218387_) {
        ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
        return !itemstack.isEmpty() && this.inventory.canAddItem(p_218387_) && this.allayConsidersItemEqual(itemstack, p_218387_) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(p_363882_, this);
    }

    private boolean allayConsidersItemEqual(ItemStack p_252278_, ItemStack p_250405_) {
        return true;
    }

    @Override
    protected void pickUpItem(ServerLevel p_363025_, ItemEntity p_218359_) {
        InventoryCarrier.pickUpItem(p_363025_, this, this, p_218359_);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public boolean isFlapping() {
        return !this.onGround();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> p_218348_) {
        if (this.level() instanceof ServerLevel serverlevel) {
            p_218348_.accept(this.dynamicVibrationListener, serverlevel);
            p_218348_.accept(this.dynamicJukeboxListener, serverlevel);
        }
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public void setDancing(boolean p_240178_) {
        if (!this.level().isClientSide && this.isEffectiveAi() && (!p_240178_ || !this.isPanicking())) {
            this.entityData.set(DATA_DANCING, p_240178_);
        }
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null
                || !this.jukeboxPos.closerToCenterThan(this.position(), GameEvent.JUKEBOX_PLAY.value().notificationRadius())
                || !this.level().getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
    }

    public float getHoldingItemAnimationProgress(float p_218395_) {
        return Mth.lerp(p_218395_, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
    }

    public boolean isSpinning() {
        float f = this.dancingAnimationTicks % 55.0F;
        return f < 15.0F;
    }

    public float getSpinningProgress(float p_240057_) {
        return Mth.lerp(p_240057_, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
    }

    @Override
    public boolean equipmentHasChanged(ItemStack p_249825_, ItemStack p_251595_) {
        return !this.allayConsidersItemEqual(p_249825_, p_251595_);
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        this.inventory.removeAllItems().forEach(stack -> this.spawnAtLocation(level, stack));
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!itemstack.isEmpty() && !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            this.spawnAtLocation(level, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean removeWhenFarAway(double p_218384_) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_218367_) {
        super.addAdditionalSaveData(p_218367_);
        this.writeInventoryToTag(p_218367_, this.registryAccess());
        RegistryOps<Tag> registryops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        VibrationSystem.Data.CODEC
                .encodeStart(registryops, this.vibrationData)
                .resultOrPartial(p_341427_ -> TakumiCraftCore.TC_LOGGER.error("Failed to encode vibration listener for TCAllayCreeper: '{}'", p_341427_))
                .ifPresent(p_218353_ -> p_218367_.put("listener", p_218353_));
        p_218367_.putLong("DuplicationCooldown", this.duplicationCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_218350_) {
        super.readAdditionalSaveData(p_218350_);
        this.readInventoryFromTag(p_218350_, this.registryAccess());
        RegistryOps<Tag> registryops = this.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        if (p_218350_.contains("listener", 10)) {
            VibrationSystem.Data.CODEC
                    .parse(registryops, p_218350_.getCompound("listener"))
                    .resultOrPartial(p_341428_ -> TakumiCraftCore.TC_LOGGER.error("Failed to parse vibration listener for TCAllayCreeper: '{}'", p_341428_))
                    .ifPresent(p_281082_ -> this.vibrationData = p_281082_);
        }

        this.duplicationCooldown = p_218350_.getInt("DuplicationCooldown");
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    private void removeInteractionItem(Player p_239359_, ItemStack p_239360_) {
        p_239360_.consume(1, p_239359_);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double) this.getEyeHeight() * 0.6, (double) this.getBbWidth() * 0.1);
    }

    @Override
    public void handleEntityEvent(byte p_239347_) {
        if (p_239347_ == 18) {
            for (int i = 0; i < 3; i++) {
                this.spawnHeartParticle();
            }
        } else {
            super.handleEntityEvent(p_239347_);
        }
    }

    private void spawnHeartParticle() {
        double d0 = this.random.nextGaussian() * 0.02;
        double d1 = this.random.nextGaussian() * 0.02;
        double d2 = this.random.nextGaussian() * 0.02;
        this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    @Override
    public boolean ignoreExplosion(Explosion p_309517_) {
        return true;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ALLAY;
    }

    public static class TCAllayCreeperContext implements TCCreeperContext<TCAllayCreeper> {
        private static final String NAME = "allaycreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCAllayCreeper::new, MobCategory.MONSTER).sized(0.35F, 0.6F).eyeHeight(0.36F).ridingOffset(0.04F).clientTrackingRange(8).updateInterval(2).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "あれいたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Not an ally, nor a fairy, just sighly tiny ghost.";
        }

        @Override
        public String getJaJPDesc() {
            return "妖精にも、友人にもなれなかったそれは悲しき霊。";
        }

        @Override
        public String getEnUSName() {
            return "Art Creeper";
        }

        @Override
        public String getJaJPName() {
            return "亜霊匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x006699;
        }

        @Override
        public int getSecondaryColor() {
            return 0x003322;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCAllayCreeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCAllayCreeper>) type, TCAllayCreeperRenderer::new);
        }
    }

    class JukeboxListener implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;

        public JukeboxListener(final PositionSource p_239448_, final int p_239449_) {
            this.listenerSource = p_239448_;
            this.listenerRadius = p_239449_;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }

        @Override
        public boolean handleGameEvent(ServerLevel p_250009_, Holder<GameEvent> p_333132_, GameEvent.Context p_249478_, Vec3 p_250852_) {
            if (p_333132_.is(GameEvent.JUKEBOX_PLAY)) {
                TCAllayCreeper.this.setJukeboxPlaying(BlockPos.containing(p_250852_), true);
                return true;
            } else if (p_333132_.is(GameEvent.JUKEBOX_STOP_PLAY)) {
                TCAllayCreeper.this.setJukeboxPlaying(BlockPos.containing(p_250852_), false);
                return true;
            } else {
                return false;
            }
        }
    }

    class VibrationUser implements VibrationSystem.User {
        private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(TCAllayCreeper.this, TCAllayCreeper.this.getEyeHeight());

        @Override
        public int getListenerRadius() {
            return 16;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel p_282038_, BlockPos p_283385_, Holder<GameEvent> p_334911_, GameEvent.Context p_282208_) {
            if (TCAllayCreeper.this.isNoAi()) {
                return false;
            } else {
                Optional<GlobalPos> optional = TCAllayCreeper.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
                if (optional.isEmpty()) {
                    return true;
                } else {
                    GlobalPos globalpos = optional.get();
                    return globalpos.dimension().equals(p_282038_.dimension()) && globalpos.pos().equals(p_283385_);
                }
            }
        }

        @Override
        public void onReceiveVibration(
                ServerLevel p_281422_, BlockPos p_281449_, Holder<GameEvent> p_333452_, @Nullable Entity p_281794_, @Nullable Entity p_281864_, float p_281642_
        ) {
            if (p_333452_.is(GameEvent.NOTE_BLOCK_PLAY)) {
                TCAllayCreeperAi.hearNoteblock(TCAllayCreeper.this, new BlockPos(p_281449_));
            }
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.ALLAY_CAN_LISTEN;
        }
    }
}
