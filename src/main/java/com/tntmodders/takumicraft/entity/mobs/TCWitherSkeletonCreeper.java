package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCWitherSkeletonCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.level.ExplosionEvent;

import javax.annotation.Nullable;
import java.util.List;

public class TCWitherSkeletonCreeper extends AbstractTCSkeletonCreeper {
    public TCWitherSkeletonCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
            }
        });
        event.getAffectedEntities().clear();
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
        super.registerGoals();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_34195_) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }


    @Override
    protected void dropCustomDeathLoot(ServerLevel p_342918_, DamageSource p_32292_, boolean p_32294_) {
        super.dropCustomDeathLoot(p_342918_, p_32292_, p_32294_);
        Entity entity = p_32292_.getEntity();
        if (entity instanceof Creeper creeper) {
            if (creeper.canDropMobsSkull()) {
                creeper.increaseDroppedSkulls();
                this.spawnAtLocation(p_342918_, Items.WITHER_SKELETON_SKULL);
            }
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource p_219154_, DifficultyInstance p_219155_) {
        ItemStack stack = new ItemStack(TCItemCore.CREEPER_SWORD);
        stack.enchant(this.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.ANTI_POWERED), 1);
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34178_, DifficultyInstance p_34179_, EntitySpawnReason p_34180_, @Nullable SpawnGroupData p_34181_) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34178_, p_34179_, p_34180_, p_34181_);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        this.reassessWeaponGoal();
        return spawngroupdata;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity entity) {
        if (!super.doHurtTarget(level, entity)) {
            return false;
        } else {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
            }

            return true;
        }
    }

    @Override
    protected AbstractArrow getArrow(ItemStack p_34189_, float p_34190_, ItemStack itemStack) {
        AbstractArrow abstractarrow = super.getArrow(p_34189_, p_34190_, itemStack);
        abstractarrow.igniteForSeconds(100);
        return abstractarrow;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return p_34192_.getEffect() != MobEffects.WITHER && super.canBeAffected(p_34192_);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.WITHER_SKELETON;
    }

    public static class TCWitherSkeletonCreeperContext implements TCCreeperContext<TCWitherSkeletonCreeper> {
        private static final String NAME = "witherskeletoncreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCWitherSkeletonCreeper::new, MobCategory.MONSTER).sized(0.7F, 2.4F).clientTrackingRange(8)
                .fireImmune().immuneTo(Blocks.WITHER_ROSE).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "うぃざーすけるとんたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Wither damaging explosion, this is a nightmare of nether.";
        }

        @Override
        public String getJaJPDesc() {
            return "ウィザー状態の爆発を起こす。ネザーは阿鼻驚嘆の悪夢に陥る。";
        }

        @Override
        public String getEnUSName() {
            return "Wither Skeleton Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ウィザースケルトン匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 7846775;
        }

        @Override
        public int getPrimaryColor() {
            return 26112;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return AbstractTCSkeletonCreeper.createAttributes();
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<AbstractTCCreeper>) type, p_174010_ -> new TCWitherSkeletonCreeperRenderer(p_174010_, this));
        }

        @Override
        public LootTable.Builder additionalBuilder(HolderLookup.Provider provider, LootTable.Builder lootTable) {
            return TCCreeperContext.super.additionalBuilder(provider, lootTable).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))).apply(EnchantedCountIncreaseFunction.lootingMultiplier(provider, UniformGenerator.between(0.0F, 1.0F)).setLimit(1))).when(LootItemKilledByPlayerCondition.killedByPlayer()));
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(TCEntityCore.NETHER_TAKUMIS);
            return list;
        }

        @Override
        public void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (biome.is(BiomeTags.IS_NETHER)) {
                TCCreeperContext.super.registerModifierSpawn(biome, builder);
            }
        }
    }
}
