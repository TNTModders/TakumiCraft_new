package com.tntmodders.takumicraft.entity.mobs;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCZombieVillagerCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TCZombieVillagerCreeper extends TCZombieCreeper implements VillagerDataHolder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<Boolean> DATA_CONVERTING_ID = SynchedEntityData.defineId(TCZombieVillagerCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(TCZombieVillagerCreeper.class, EntityDataSerializers.VILLAGER_DATA);
    private static final int VILLAGER_CONVERSION_WAIT_MIN = 3600;
    private static final int VILLAGER_CONVERSION_WAIT_MAX = 6000;
    private static final int MAX_SPECIAL_BLOCKS_COUNT = 14;
    private static final int SPECIAL_BLOCK_RADIUS = 4;
    private int villagerConversionTime;
    @Nullable
    private UUID conversionStarter;
    @Nullable
    private Tag gossips;
    @Nullable
    private CompoundTag tradeOffers;
    private int villagerXp;

    public TCZombieVillagerCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        BuiltInRegistries.VILLAGER_PROFESSION.getRandom(this.random).ifPresent(p_255550_ -> this.setVillagerData(this.getVillagerData().setProfession(p_255550_.value())));
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ZOMBIE_VILLAGER;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CONVERTING_ID, false);
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_34397_) {
        super.addAdditionalSaveData(p_34397_);
        VillagerData.CODEC.encodeStart(NbtOps.INSTANCE, this.getVillagerData()).resultOrPartial(LOGGER::error).ifPresent(p_34390_ -> p_34397_.put("VillagerData", p_34390_));
        if (this.tradeOffers != null) {
            p_34397_.put("Offers", this.tradeOffers);
        }

        if (this.gossips != null) {
            p_34397_.put("Gossips", this.gossips);
        }

        p_34397_.putInt("ConversionTime", this.isConverting() ? this.villagerConversionTime : -1);
        if (this.conversionStarter != null) {
            p_34397_.putUUID("ConversionPlayer", this.conversionStarter);
        }

        p_34397_.putInt("Xp", this.villagerXp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_34387_) {
        super.readAdditionalSaveData(p_34387_);
        if (p_34387_.contains("VillagerData", 10)) {
            DataResult<VillagerData> dataresult = VillagerData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, p_34387_.get("VillagerData")));
            dataresult.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
        }

        if (p_34387_.contains("Offers", 10)) {
            this.tradeOffers = p_34387_.getCompound("Offers");
        }

        if (p_34387_.contains("Gossips", 10)) {
            this.gossips = p_34387_.getList("Gossips", 10);
        }

        if (p_34387_.contains("ConversionTime", 99) && p_34387_.getInt("ConversionTime") > -1) {
            this.startConverting(p_34387_.hasUUID("ConversionPlayer") ? p_34387_.getUUID("ConversionPlayer") : null, p_34387_.getInt("ConversionTime"));
        }

        if (p_34387_.contains("Xp", 3)) {
            this.villagerXp = p_34387_.getInt("Xp");
        }

    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.isAlive() && this.isConverting()) {
            int i = this.getConversionProgress();
            this.villagerConversionTime -= i;
            if (this.villagerConversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, timer -> this.villagerConversionTime = timer)) {
                this.finishConversion((ServerLevel) this.level());
            }
        }

        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player p_34394_, InteractionHand p_34395_) {
        ItemStack itemstack = p_34394_.getItemInHand(p_34395_);
        if (itemstack.is(Items.GOLDEN_APPLE)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!p_34394_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (!this.level().isClientSide) {
                    this.startConverting(p_34394_.getUUID(), this.random.nextInt(2401) + 3600);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(p_34394_, p_34395_);
        }
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double p_34414_) {
        return !this.isConverting() && this.villagerXp == 0;
    }

    public boolean isConverting() {
        return this.getEntityData().get(DATA_CONVERTING_ID);
    }

    private void startConverting(@Nullable UUID p_34384_, int p_34385_) {
        this.conversionStarter = p_34384_;
        this.villagerConversionTime = p_34385_;
        this.getEntityData().set(DATA_CONVERTING_ID, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, p_34385_, Math.min(this.level().getDifficulty().getId() - 1, 0)));
        this.level().broadcastEntityEvent(this, (byte) 16);
    }

    @Override
    public void handleEntityEvent(byte p_34372_) {
        if (p_34372_ == 16) {
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

        } else {
            super.handleEntityEvent(p_34372_);
        }
    }

    private void finishConversion(ServerLevel p_34399_) {
        Villager villager = this.convertTo(EntityType.VILLAGER, false);

        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            ItemStack itemstack = this.getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
                if (EnchantmentHelper.hasBindingCurse(itemstack)) {
                    villager.getSlot(equipmentslot.getIndex() + 300).set(itemstack);
                } else {
                    double d0 = this.getEquipmentDropChance(equipmentslot);
                    if (d0 > 1.0D) {
                        this.spawnAtLocation(itemstack);
                    }
                }
            }
        }

        villager.setVillagerData(this.getVillagerData());
        if (this.gossips != null) {
            villager.setGossips(this.gossips);
        }

        if (this.tradeOffers != null) {
            villager.setOffers(new MerchantOffers(this.tradeOffers));
        }

        villager.setVillagerXp(this.villagerXp);
        villager.finalizeSpawn(p_34399_, p_34399_.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.CONVERSION, null, null);
        if (this.conversionStarter != null) {
            Player player = p_34399_.getPlayerByUUID(this.conversionStarter);
            if (player instanceof ServerPlayer) {
                //CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayer) player, this, villager);
                p_34399_.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, player, villager);
            }
        }

        villager.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            p_34399_.levelEvent(null, 1027, this.blockPosition(), 0);
        }
        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, villager);
    }

    private int getConversionProgress() {
        int i = 1;
        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k = (int) this.getX() - 4; k < (int) this.getX() + 4 && j < 14; ++k) {
                for (int l = (int) this.getY() - 4; l < (int) this.getY() + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.getZ() - 4; i1 < (int) this.getZ() + 4 && j < 14; ++i1) {
                        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos.set(k, l, i1));
                        if (blockstate.is(Blocks.IRON_BARS) || blockstate.getBlock() instanceof BedBlock) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_VILLAGER_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource p_34404_) {
        return SoundEvents.ZOMBIE_VILLAGER_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    public SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_VILLAGER_STEP;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    public void setTradeOffers(CompoundTag p_34412_) {
        this.tradeOffers = p_34412_;
    }

    public void setGossips(Tag p_34392_) {
        this.gossips = p_34392_;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34378_, DifficultyInstance p_34379_, MobSpawnType p_34380_, @Nullable SpawnGroupData p_34381_, @Nullable CompoundTag p_34382_) {
        this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(p_34378_.getBiome(this.blockPosition()))));
        return super.finalizeSpawn(p_34378_, p_34379_, p_34380_, p_34381_, p_34382_);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    @Override
    public void setVillagerData(VillagerData p_34376_) {
        VillagerData villagerdata = this.getVillagerData();
        if (villagerdata.getProfession() != p_34376_.getProfession()) {
            this.tradeOffers = null;
        }

        this.entityData.set(DATA_VILLAGER_DATA, p_34376_);
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int p_34374_) {
        this.villagerXp = p_34374_;
    }

    public static class TCZombieVillagerCreeperContext implements TCCreeperContext<TCZombieVillagerCreeper> {
        private static final String NAME = "zombievillagercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCZombieVillagerCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "むらびとぞんびたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Villager changed zombie, attacks friends, not knowing who they are.";
        }

        @Override
        public String getJaJPDesc() {
            return "匠化してしまった村人。治癒の魔法もたちまち爆風に飲み込まれる。";
        }

        @Override
        public String getEnUSName() {
            return "Zombie Villager Creeper";
        }

        @Override
        public String getJaJPName() {
            return "村人ゾンビ匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 39168;
        }

        @Override
        public int getSecondaryColor() {
            return 112211;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Monster.createMonsterAttributes()
                    .add(Attributes.FOLLOW_RANGE, 35.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.23F)
                    .add(Attributes.ATTACK_DAMAGE, 3.0D)
                    .add(Attributes.ARMOR, 2.0D)
                    .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCZombieVillagerCreeper>) type, TCZombieVillagerCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Nullable
        @Override
        public Supplier<LootTableSubProvider> getCreeperLoot(EntityType<?> type) {
            return () -> new EntityLootSubProvider(FeatureFlags.REGISTRY.allFlags()) {
                @Override
                public Stream<EntityType<?>> getKnownEntityTypes() {
                    return Stream.of(type);
                }

                @Override
                public void generate() {
                    this.add(CREEPER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.IRON_INGOT)).add(LootItem.lootTableItem(Items.CARROT)).add(LootItem.lootTableItem(Items.POTATO).apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025F, 0.01F))));
                }
            };
        }
    }

}
