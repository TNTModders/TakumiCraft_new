package com.tntmodders.takumicraft.entity.mobs.boss;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCKingCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.ai.TCKingCreeperSwellGoal;
import com.tntmodders.takumicraft.entity.ai.boss.king.*;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TCKingCreeper extends AbstractTCCreeper {

    private static final EntityDataAccessor<Integer> ATTACK_ID = SynchedEntityData.defineId(TCKingCreeper.class, EntityDataSerializers.INT);
    public static final ResourceLocation KING_LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/kingcreeper_armor.png");
    private int lastAttackID;
    private boolean ULTCasted;

    private static final ProjectileDeflection PROJECTILE_DEFLECTION = (projectile, entity, randomSource) -> {
        entity.level().playLocalSound(entity, SoundEvents.BREEZE_DEFLECT, entity.getSoundSource(), 1.0F, 1.0F);
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.05));
        float f = 170.0F + randomSource.nextFloat() * 20.0F;
        projectile.setYRot(projectile.getYRot() + f);
        projectile.yRotO += f;
        projectile.hurtMarked = true;
    };

    public TCKingCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 0;
        this.maxSwell = 60;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new TCKingCreeperSwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_ID, 0);
    }

    @Override
    public void ignite() {
        if (!this.isIgnited()) {
            this.setRandomAttackID();
        }
        this.entityData.set(DATA_IS_IGNITED, !isIgnited());
    }

    public int getSwell() {
        return this.swell;
    }

    public EnumTCKingCreeperAttackID getAttackID() {
        return EnumTCKingCreeperAttackID.getID(this.getEntityData().get(ATTACK_ID));
    }

    protected void setAttackID(EnumTCKingCreeperAttackID id) {
        this.lastAttackID = this.getAttackID().getID();
        this.getEntityData().set(ATTACK_ID, id.getID());
        id.getAttack().serverInit(this);
    }

    public void setRandomAttackID() {
        if (!this.getAttackID().isULTATK()) {
            //this.setAttackID(EnumTCKingCreeperAttackID.getRandomID(this.getRandom()));
        }
        this.setAttackID(EnumTCKingCreeperAttackID.ULT_EXP);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAttackID(EnumTCKingCreeperAttackID.getID(tag.getInt("AttackID")));
        if (tag.getBoolean("ULTCasted")) {
            this.ULTCasted = true;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AttackID", this.getAttackID().getID());
        if (this.ULTCasted) {
            tag.putBoolean("ULTCasted", true);
        }
    }

    public void setUseItem(ItemStack stack) {
        this.useItem = stack;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSwell() > 0 && (this.getSwellDir() > 0 || this.isIgnited()) && !this.level().isClientSide()) {
            this.getAttackID().getAttack().serverTick(this, this.getSwell());
        }
    }

    @Override
    public void explodeCreeper() {
        TCLoggingUtils.info(this.getAttackID());
        if (!this.level().isClientSide) {
            this.getAttackID().getAttack().serverExp(this);
            if (this.getAttackID().isULTATK()) {
                this.ULTCasted = true;
            }
            if (this.isIgnited()) {
                this.ignite();
            }
        }
        this.setSwellDir(-2);
        this.setAttackID(EnumTCKingCreeperAttackID.NONE);
        this.swell = this.maxSwell / 2;
        if (this.getHealth() < this.getMaxHealth() / 2) {
            if (!this.ULTCasted) {
                this.setAttackID(EnumTCKingCreeperAttackID.ULT_EXP);
            }
            this.setPowered(true);
        }
        this.setHealth(10);
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
    public float getBlockExplosionResistance(Explosion p_19992_, BlockGetter p_19993_, BlockPos p_19994_, BlockState p_19995_, FluidState p_19996_, float p_19997_) {
        return super.getBlockExplosionResistance(p_19992_, p_19993_, p_19994_, p_19995_, p_19996_, p_19997_) / 10f;
    }

    @Override
    public void thunderHit(ServerLevel p_32286_, LightningBolt p_32287_) {
        this.setPowered(true);
    }

    @Override
    public ProjectileDeflection deflection(Projectile projectile) {
        if (this.isPowered() && !this.hasEffect(MobEffects.BLINDNESS)) {
            TCExplosionUtils.createExplosion(level(), this, projectile.getX(), projectile.getY(), projectile.getZ(), 0f, false);
            this.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
            return PROJECTILE_DEFLECTION;
        }
        return super.deflection(projectile);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        boolean damageImmune = false;
        if (damage > 20) {
            damage = 20;
        }
        if (source.isIndirect()) {
            damageImmune = true;
            if (this.isPowered() && source.getDirectEntity() instanceof Projectile projectile && !this.hasEffect(MobEffects.BLINDNESS)) {
                TCExplosionUtils.createExplosion(level(), this, projectile.getX(), projectile.getY(), projectile.getZ(), 0f, false);
                Entity counter = projectile.getType().create(this.level());
                counter.copyPosition(projectile);
                ((Projectile) counter).setOwner(this);
                counter.setDeltaMovement(counter.getDeltaMovement().scale(-1.05));
                float f = 175 + (float) this.getRandom().nextGaussian() * 10;
                counter.setYRot(projectile.getYRot() + f);
                counter.yRotO += f;
                counter.hurtMarked = true;
                if (projectile.getOwner() != null) {
                    double d0 = projectile.getOwner().getX() - this.getX();
                    double d1 = projectile.getOwner().getY(1.0 / 3.0) - this.getY(1);
                    double d2 = projectile.getZ() - this.getZ();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    ((Projectile) counter).shoot(d0, d1 + d3 * (double) 0.2F, d2, 2F, (float) (14 - this.level().getDifficulty().getId() * 4));
                }
                this.level().addFreshEntity(counter);
                this.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                projectile.remove(RemovalReason.DISCARDED);
                return false;
            }
        }
        if (damageImmune || !(source.getEntity() instanceof Player || source.getDirectEntity() instanceof Player)) {
            damage = damage / 10;
        }

        return !source.is(DamageTypes.LIGHTNING_BOLT) && !source.is(DamageTypes.IN_FIRE) && !source.is(DamageTypes.EXPLOSION) && (source.is(DamageTypes.PLAYER_ATTACK) || source.getDirectEntity() instanceof Player || source.getEntity() instanceof Player) && super.hurt(source, damage);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.KING;
    }

    public static class TCKingCreeperContext implements TCCreeperContext<TCKingCreeper> {
        private static final String NAME = "kingcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCKingCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おうしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "The king has come, it's time to bomb.";
        }

        @Override
        public String getJaJPDesc() {
            return "跪き、命乞いをしろ。王は凱旋する、匠たちの勝利の時。";
        }

        @Override
        public String getEnUSName() {
            return "King Creeper";
        }

        @Override
        public String getJaJPName() {
            return "王匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x88ff00;
        }

        @Override
        public boolean alterSpawn() {
            return true;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.TAKUMI_MD;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.BOSS;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCItemCore.BOLTSTONE;
        }

        @Override
        public ResourceLocation getArmor() {
            return KING_LOCATION;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 250).add(Attributes.KNOCKBACK_RESISTANCE, 1000000).add(Attributes.FOLLOW_RANGE, 100).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ARMOR, 4);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCKingCreeper>) type, p_173958_ -> new TCKingCreeperRenderer<>(p_173958_, TCEntityCore.KING));
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            return false;
        }

        @Override
        public void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1f, 1f);
        }
    }

    public enum EnumTCKingCreeperAttackID {
        NONE(0, true, new NoneKingCreeperAttack()),
        RANDOM_EXP(1, new ExplosionKingCreeperAttack(false, true)),
        BALL_EXP(2, new ExplosionKingCreeperAttack(false, false)),
        RANDOM_FIRE_EXP(3, new ExplosionKingCreeperAttack(true, true)),
        BALL_FIRE_EXP(4, new ExplosionKingCreeperAttack(true, false)),
        RANDOM_THUNDER(5, new ThunderKingCreeperAttack(true)),
        FOLLOW_THUNDER(6, new ThunderKingCreeperAttack(false)),
        FIREBALL(7, new FireballKingCreeperAttack()),
        TP_EXP(8, new TPKingCreeperAttack()),
        STORM(9, new StormKingCreeperAttack()),
        SWORD(10, new SwordKingCreeperAttack()),
        MACE(11, new MaceKingCreeperAttack()),
        ARROWRAIN(12, new ArrowrainKingCreeperAttack()),
        KINGBLOCK(13, new KingblockKingCreeperAttack()),
        ULT_EXP(90, true, new SPExplosionKingCreeperAttack());

        private final int id;
        private final boolean isULT;
        private final AbstractKingCreeperAttack attack;

        EnumTCKingCreeperAttackID(int id, AbstractKingCreeperAttack atk) {
            this(id, false, atk);
        }

        EnumTCKingCreeperAttackID(int id, boolean ult, AbstractKingCreeperAttack atk) {
            this.id = id;
            this.isULT = ult;
            this.attack = atk;
        }

        public AbstractKingCreeperAttack getAttack() {
            return attack;
        }

        public int getID() {
            return id;
        }

        public boolean isULTATK() {
            return isULT && this != NONE;
        }

        public static Stream<EnumTCKingCreeperAttackID> getIDs() {
            return Arrays.stream(EnumTCKingCreeperAttackID.values()).sorted(Comparator.comparingInt(EnumTCKingCreeperAttackID::getID));
        }

        public static EnumTCKingCreeperAttackID getID(int i) {
            Optional<EnumTCKingCreeperAttackID> optional = getIDs().filter(id -> id.getID() == i).findAny();
            return optional.orElse(NONE);
        }

        public static EnumTCKingCreeperAttackID getRandomID(RandomSource random) {
            List<EnumTCKingCreeperAttackID> list = Arrays.stream(EnumTCKingCreeperAttackID.values()).filter(id -> !id.isULT).toList();
            return list.get(random.nextInt(list.size()));
        }
    }
}
