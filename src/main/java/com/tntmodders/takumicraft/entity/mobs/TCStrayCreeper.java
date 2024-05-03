package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCStrayCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public class TCStrayCreeper extends AbstractTCSkeletonCreeper {
    public TCStrayCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean checkStraySpawnRules(EntityType<AbstractTCCreeper> p_219121_, ServerLevelAccessor p_219122_, MobSpawnType p_219123_, BlockPos p_219124_, RandomSource p_219125_) {
        BlockPos blockpos = p_219124_;

        do {
            blockpos = blockpos.above();
        } while (p_219122_.getBlockState(blockpos).is(Blocks.POWDER_SNOW));

        return checkMonsterSpawnRules(p_219121_, p_219122_, p_219123_, p_219124_, p_219125_) && (MobSpawnType.isSpawner(p_219123_) || p_219122_.canSeeSky(blockpos.below()));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33850_) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.STRAY_STEP;
    }

    @Override
    protected AbstractArrow getArrow(ItemStack p_33846_, float p_33847_) {
        AbstractArrow abstractarrow = super.getArrow(p_33846_, p_33847_);
        if (abstractarrow instanceof Arrow) {
            ((Arrow) abstractarrow).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 4));
        }
        return abstractarrow;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.STRAY;
    }

    public static class TCStrayCreeperContext implements TCCreeperContext<TCStrayCreeper> {
        private static final String NAME = "straycreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCStrayCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8)
                .immuneTo(Blocks.POWDER_SNOW).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "すとれいたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "With the potion, the player will be a portion.";
        }

        @Override
        public String getJaJPDesc() {
            return "ポーションとその弓矢で、プレイヤーは粉々に。";
        }

        @Override
        public String getEnUSName() {
            return "Stray Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ストレイ匠";
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
            return 0xaaffaa;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return AbstractTCSkeletonCreeper.createAttributes();
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
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<AbstractTCCreeper>) type, p_174010_ -> new TCStrayCreeperRenderer(p_174010_, this));
        }

        @Override
        public LootTable.Builder additionalBuilder(LootTable.Builder lootTable) {
            return TCCreeperContext.super.additionalBuilder(lootTable).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(TCItemCore.TIPPED_CREEPER_ARROW).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F))).apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)).setLimit(1)).apply(SetPotionFunction.setPotion(Potions.SLOWNESS))).when(LootItemKilledByPlayerCondition.killedByPlayer()));
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCItemCore.CREEPER_ARROW;
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCStrayCreeper::checkStraySpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }
    }
}
