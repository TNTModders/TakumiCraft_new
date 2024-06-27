package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCZombieCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.stream.Stream;

public class TCHuskCreeper extends TCZombieCreeper {

    public TCHuskCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 3;
    }

    public static boolean checkHuskCreeperSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getBiome(pos).containsTag(BiomeTags.HAS_DESERT_PYRAMID) && checkTakumiSpawnRules(type, levelAccessor, spawnType, pos, random);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.HUSK;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 0));
            }
        });
    }

    public static class TCHuskCreeperContext implements TCCreeperContext<TCHuskCreeper> {
        private static final String NAME = "huskcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCHuskCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "はすくたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Desert zombie creeper. You will suffer of  hunger";
        }

        @Override
        public String getJaJPDesc() {
            return "砂漠のゾンビ匠。爆発を喰らえば、空腹に苦しむこと必至。";
        }

        @Override
        public String getEnUSName() {
            return "Husk Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ハスク匠";
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
            return 0x666600;
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
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCZombieCreeper>) type, TCZombieCreeperRenderer::new);
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
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCHuskCreeper::checkHuskCreeperSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Nullable
        @Override
        public Function<HolderLookup.Provider, LootTableSubProvider> getCreeperLoot(EntityType<?> type) {
            return new Function<>() {
                @Override
                public LootTableSubProvider apply(HolderLookup.Provider provider) {
                    return new EntityLootSubProvider(FeatureFlags.REGISTRY.allFlags(), provider) {
                        @Override
                        public Stream<EntityType<?>> getKnownEntityTypes() {
                            return Stream.of(type);
                        }

                        @Override
                        public void generate() {
                            LootTable.Builder builder = LootTable.lootTable()
                                    .withPool(
                                            LootPool.lootPool()
                                                    .setRolls(ConstantValue.exactly(1.0F))
                                                    .add(
                                                            LootItem.lootTableItem(Items.ROTTEN_FLESH)
                                                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                                    )
                                    )
                                    .withPool(
                                            LootPool.lootPool()
                                                    .setRolls(ConstantValue.exactly(1.0F))
                                                    .add(LootItem.lootTableItem(Items.IRON_INGOT))
                                                    .add(LootItem.lootTableItem(Items.CARROT))
                                                    .add(LootItem.lootTableItem(Items.POTATO).apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot())))
                                                    .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                                    .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.025F, 0.01F))
                                    );
                            this.add(CREEPER, TCHuskCreeperContext.this.additionalBuilder(this.registries, builder));
                        }
                    };
                }
            };
        }
    }
}
