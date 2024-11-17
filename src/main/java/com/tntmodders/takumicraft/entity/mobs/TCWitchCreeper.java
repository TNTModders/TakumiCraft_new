package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCWitchCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCWitchCreeper extends AbstractTCCreeper implements RangedAttackMob {
    private static final ResourceLocation SPEED_MODIFIER_DRINKING_ID = ResourceLocation.withDefaultNamespace("drinking");
    private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_ID, -0.25, AttributeModifier.Operation.ADD_VALUE);
    private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(TCWitchCreeper.class, EntityDataSerializers.BOOLEAN);
    private int usingTime;

    public TCWitchCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 26.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.WITCH;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 60, 10.0F));
        this.goalSelector.addGoal(2, new SwellGoal(this));
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
    protected void defineSynchedData(SynchedEntityData.Builder p_330124_) {
        super.defineSynchedData(p_330124_);
        p_330124_.define(DATA_USING_ITEM, false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_34154_) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    public void setUsingItem(boolean p_34164_) {
        this.getEntityData().set(DATA_USING_ITEM, p_34164_);
    }

    public boolean isDrinkingPotion() {
        return this.getEntityData().get(DATA_USING_ITEM);
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide && this.isAlive()) {
            if (this.isDrinkingPotion()) {
                if (this.usingTime-- <= 0) {
                    this.setUsingItem(false);
                    ItemStack itemstack = this.getMainHandItem();
                    this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    PotionContents potioncontents = itemstack.get(DataComponents.POTION_CONTENTS);
                    if (itemstack.is(Items.POTION) && potioncontents != null) {
                        potioncontents.forEachEffect(this::addEffect);
                    }

                    this.gameEvent(GameEvent.DRINK);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING.id());
                }
            } else {
                Holder<Potion> holder = null;
                if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    holder = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F
                        && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
                        && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    holder = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    holder = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F
                        && this.getTarget() != null
                        && !this.hasEffect(MobEffects.MOVEMENT_SPEED)
                        && this.getTarget().distanceToSqr(this) > 121.0) {
                    holder = Potions.SWIFTNESS;
                }

                if (holder != null) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, PotionContents.createItemStack(Items.POTION, holder));
                    this.usingTime = this.getMainHandItem().getUseDuration(this);
                    this.setUsingItem(true);
                    if (!this.isSilent()) {
                        this.level()
                                .playSound(
                                        null,
                                        this.getX(),
                                        this.getY(),
                                        this.getZ(),
                                        SoundEvents.WITCH_DRINK,
                                        this.getSoundSource(),
                                        1.0F,
                                        0.8F + this.random.nextFloat() * 0.4F
                                );
                    }

                    AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                    attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING_ID);
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.level().broadcastEntityEvent(this, (byte) 15);
            }
        }

        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte p_34138_) {
        if (p_34138_ == 15) {
            for (int i = 0; i < this.random.nextInt(35) + 10; i++) {
                this.level()
                        .addParticle(
                                ParticleTypes.WITCH,
                                this.getX() + this.random.nextGaussian() * 0.13F,
                                this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.13F,
                                this.getZ() + this.random.nextGaussian() * 0.13F,
                                0.0,
                                0.0,
                                0.0
                        );
            }
        } else {
            super.handleEntityEvent(p_34138_);
        }
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
        p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
        if (p_34149_.getEntity() == this) {
            p_34150_ = 0.0F;
        }

        if (p_34149_.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            p_34150_ *= 0.15F;
        }

        return p_34150_;
    }

    @Override
    public void performRangedAttack(LivingEntity p_34143_, float p_34144_) {
        if (!this.isDrinkingPotion()) {
            Vec3 vec3 = p_34143_.getDeltaMovement();
            double d0 = p_34143_.getX() + vec3.x - this.getX();
            double d1 = p_34143_.getEyeY() - 1.1F - this.getY();
            double d2 = p_34143_.getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            Holder<Potion> holder = Potions.STRONG_HARMING;
            if (p_34143_ instanceof Raider) {
                if (p_34143_.getHealth() <= 4.0F) {
                    holder = Potions.HEALING;
                } else {
                    holder = Potions.REGENERATION;
                }

                this.setTarget(null);
            } else if (d3 >= 8.0 && !p_34143_.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                holder = Potions.STRONG_SLOWNESS;
            } else if (p_34143_.getHealth() >= 8.0F && !p_34143_.hasEffect(MobEffects.POISON)) {
                holder = Potions.STRONG_POISON;
            } else if (d3 <= 3.0 && !p_34143_.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                holder = Potions.LONG_WEAKNESS;
            }
            ItemStack stack = PotionContents.createItemStack(Items.LINGERING_POTION, holder);
            ThrownPotion thrownpotion = new ThrownPotion(this.level(), this, stack) {
                @Override
                protected void onHit(HitResult hitResult) {
                    super.onHit(hitResult);
                    if (!this.level().isClientSide()) {
                        TCExplosionUtils.createExplosion(this.level(), null, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), 1f, false);
                    }
                }
            };
            thrownpotion.setItem(stack);
            thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
            thrownpotion.shoot(d0, d1 + d3 * 0.2, d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level()
                        .playSound(
                                null,
                                this.getX(),
                                this.getY(),
                                this.getZ(),
                                SoundEvents.WITCH_THROW,
                                this.getSoundSource(),
                                1.0F,
                                0.8F + this.random.nextFloat() * 0.4F
                        );
            }

            this.level().addFreshEntity(thrownpotion);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource p_21016_, float p_21017_) {
        return !p_21016_.is(DamageTypeTags.IS_EXPLOSION) && !p_21016_.is(DamageTypeTags.WITCH_RESISTANT_TO) && super.hurtServer(level, p_21016_, p_21017_);
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        PotionContents potioncontents = null;
        if (this.isDrinkingPotion() && !this.getMainHandItem().isEmpty()) {
            ItemStack itemstack = this.getMainHandItem();
            if (itemstack.is(Items.POTION) && itemstack.get(DataComponents.POTION_CONTENTS) != null) {
                potioncontents = itemstack.get(DataComponents.POTION_CONTENTS);
            }
        }

        PotionContents contents = potioncontents;
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                if (contents != null) {
                    contents.forEachEffect(living::addEffect);
                } else {
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200));
                }
            }
        });
    }

    public static class TCWitchCreeperContext implements TCCreeperContext<TCWitchCreeper> {
        private static final String NAME = "witchcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCWitchCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.62F).passengerAttachments(2.2625F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "うぃっちたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Magic and explosion! Tricky attack and toughness.";
        }

        @Override
        public String getJaJPDesc() {
            return "ポーション魔も爆風とともに、プレイヤーを惑わし追い詰める。";
        }

        @Override
        public String getEnUSName() {
            return "Witch Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ウィッチ匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00ff00;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaa00aa;
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
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCWitchCreeper>) type, TCWitchCreeperRenderer::new);
        }
    }
}
