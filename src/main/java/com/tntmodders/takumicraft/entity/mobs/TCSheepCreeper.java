package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCSheepCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class TCSheepCreeper extends AbstractTCCreeper {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final String RAINBOW_NAME = "jeb_";
    private static final EntityDataAccessor<Byte> DATA_WOOL_ID = SynchedEntityData.defineId(TCSheepCreeper.class, EntityDataSerializers.BYTE);

    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;

    public TCSheepCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.23F);
    }

    public static DyeColor getRandomSheepColor(RandomSource rand) {
        return DyeColor.byId(rand.nextInt(16));
    }

    private static CraftingInput makeCraftInput(DyeColor p_344668_, DyeColor p_344678_) {
        return CraftingInput.of(2, 1, List.of(new ItemStack(DyeItem.byColor(p_344668_)), new ItemStack(DyeItem.byColor(p_344678_))));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT), false));
        this.goalSelector.addGoal(5, this.eatBlockGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep(level);
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }

        super.aiStep();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_330760_) {
        super.defineSynchedData(p_330760_);
        p_330760_.define(DATA_WOOL_ID, (byte) 0);
    }

    @Override
    public void handleEntityEvent(byte p_29814_) {
        if (p_29814_ == 10) {
            this.eatAnimationTick = 40;
        } else {
            super.handleEntityEvent(p_29814_);
        }

    }

    public float getHeadEatPositionScale(float p_29881_) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4 ? ((float) this.eatAnimationTick - p_29881_) / 4.0F : -((float) (this.eatAnimationTick - 40) - p_29881_) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float p_29883_) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((float) (this.eatAnimationTick - 4) - p_29883_) / 32.0F;
            return (float) Math.PI / 5F + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.eatAnimationTick > 0 ? (float) Math.PI / 5F : this.getXRot() * ((float) Math.PI / 180F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_29864_) {
        super.addAdditionalSaveData(p_29864_);
        p_29864_.putByte("Color", (byte) this.getColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_29845_) {
        super.readAdditionalSaveData(p_29845_);
        this.setColor(DyeColor.byId(p_29845_.getByte("Color")));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_29872_) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos p_29861_, BlockState p_29862_) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    public DyeColor getColor() {
        return DyeColor.byId(this.entityData.get(DATA_WOOL_ID) & 15);
    }

    public void setColor(DyeColor p_29856_) {
        byte b0 = this.entityData.get(DATA_WOOL_ID);
        this.entityData.set(DATA_WOOL_ID, (byte) (b0 & 240 | p_29856_.getId() & 15));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29835_, DifficultyInstance p_29836_, EntitySpawnReason p_29837_, @Nullable SpawnGroupData p_29838_) {
        this.setColor(getRandomSheepColor(p_29835_.getRandom()));
        if (this.random.nextInt(10000) == 0) {
            this.setRainbow();
            this.setPowered(true);
        }
        return super.finalizeSpawn(p_29835_, p_29836_, p_29837_, p_29838_);
    }

    @Override
    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        if (this.isRainbow() && p_21014_.is(DamageTypes.PLAYER_ATTACK) && p_21014_.getEntity() instanceof ServerPlayer player) {
            if (player instanceof ServerPlayer && player.distanceToSqr(this) < 100) {
                player.getAdvancements()
                        .award(Objects.requireNonNull(player.server.getAdvancements().get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "rainbowsheep"))), "impossible");
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel p_342918_, DamageSource p_32292_, boolean p_32294_) {
        super.dropCustomDeathLoot(p_342918_, p_32292_, p_32294_);
        this.spawnAtLocation(p_342918_, TCBlockCore.CREEPER_WOOL_MAP.get(this.getColor()));
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.SHEEP;
    }

    public boolean isRainbow() {
        return this.hasCustomName() && RAINBOW_NAME.equals(this.getName().getString());
    }

    public void setRainbow() {
        this.setCustomName(Component.literal(RAINBOW_NAME));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRainbow() && !this.isPowered()) {
            this.setPowered(true);
        }
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            if (this.isRainbow()) {
                f = 25;
            }
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, Level.ExplosionInteraction.MOB);
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    public static class TCSheepCreeperContext implements TCCreeperContext<TCSheepCreeper> {
        private static final String NAME = "sheepcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCSheepCreeper::new, MobCategory.MONSTER).sized(0.9F, 1.3F).clientTrackingRange(10).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ひつじたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper looks like a sheep. Don't you like colorful explosion?";
        }

        @Override
        public String getJaJPDesc() {
            return "羊型の匠。カラフルに爆発しましょう！全十六色、シークレット有り。";
        }

        @Override
        public String getEnUSName() {
            return "Sheep Creeper";
        }

        @Override
        public String getJaJPName() {
            return "羊匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaaccaa;
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
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCSheepCreeper>) type, TCSheepCreeperRenderer::new);
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
            return Items.COOKED_MUTTON;
        }
    }
}
