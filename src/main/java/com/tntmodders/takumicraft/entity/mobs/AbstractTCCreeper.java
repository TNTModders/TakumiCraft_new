package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCConfigCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.item.TCElementCoreItem;
import com.tntmodders.takumicraft.provider.ITCEntities;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractTCCreeper extends Creeper implements ITCEntities, IEntityAdditionalSpawnData {
    private boolean onBook = false;

    public AbstractTCCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean checkTakumiSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor levelAccessor, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        if (type.create(levelAccessor.getLevel(), spawnType) instanceof AbstractTCCreeper creeper) {
            TCCreeperContext.EnumTakumiRank rank = creeper.getContext().getRank();
            if (rank.getLevel() > 2 || random.nextInt(50) > rank.getSpawnWeight()) {
                return false;
            }
        }
        return checkMonsterSpawnRules(type, levelAccessor, spawnType, pos, random);
    }

    public static boolean checkAnimalSpawnRules(EntityType<? extends AbstractTCCreeper> p_218105_, LevelAccessor p_218106_, EntitySpawnReason p_218107_, BlockPos p_218108_, RandomSource p_218109_) {
        return p_218106_.getBlockState(p_218108_.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_218106_, p_218108_);
    }

    protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter p_186210_, BlockPos p_186211_) {
        return p_186210_.getRawBrightness(p_186211_, 0) > 8;
    }

    public double getRandomY(double dy) {
        return this.getY((2.0D * this.random.nextDouble() - 1.0D) * dy);
    }

    public boolean isOnBook() {
        return this.onBook;
    }

    public void setOnBook(boolean book) {
        this.onBook = book;
    }

    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void weatherSetPowered() {
        this.setPowered(true);
    }

    public void setPowered(boolean flg) {
        this.entityData.set(Creeper.DATA_IS_POWERED, flg);
    }

    public abstract TCCreeperContext<? extends AbstractTCCreeper> getContext();

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getEntityLoot() {
        return provider -> new EntityLootSubProvider(FeatureFlags.REGISTRY.allFlags(), provider) {
            @Override
            protected Stream<EntityType<?>> getKnownEntityTypes() {
                return TCEntityCore.ENTITY_TYPES.stream();
            }

            @Override
            public void generate() {
                TCCreeperContext context = AbstractTCCreeper.this.getContext();
                context.getCreeperLoot(context.entityType());
            }
        };
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
    }

    public interface TCCreeperContext<T extends AbstractTCCreeper> extends ITCTranslator {

        String getRegistryName();

        String getJaJPRead();

        String getEnUSDesc();

        String getJaJPDesc();

        default boolean showRead() {
            return false;
        }

        default boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractTCCreeper::checkTakumiSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        default void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (!biome.is(BiomeTags.IS_END) || this.getEntityTypeTags().contains(TCEntityCore.END_TAKUMIS)) {
                double weight = this.getSpawnWeight() * TCConfigCore.TCSpawnConfig.SPAWN.generalSpawnFactor.get();
                if (TCConfigCore.TCSpawnConfig.SPAWN.creeperSpawnFactors.containsKey(this.entityType())) {
                    weight = weight * TCConfigCore.TCSpawnConfig.SPAWN.creeperSpawnFactors.get(this.entityType()).get();
                }
                if (biome.is(BiomeTags.IS_NETHER) && this.getElement().getMainElement() != EnumTakumiElement.FIRE && !this.getEntityTypeTags().contains(TCEntityCore.NETHER_TAKUMIS)) {
                    weight = weight / 10;
                }
                builder.getMobSpawnSettings().getSpawner(this.entityType().getCategory())
                        .add(new MobSpawnSettings.SpawnerData(this.entityType(), (int) weight, 1, this.getMaxSpawn()));
            }
        }

        default boolean alterSpawn() {
            return false;
        }

        default int getSpawnWeight() {
            return this.getRank().getSpawnWeight();
        }

        default int getMaxSpawn() {
            return this.getRank().getMaxSpawn();
        }

        default ItemLike getMainDropItem() {
            return Items.GUNPOWDER;
        }

        default UniformGenerator getDropRange() {
            return UniformGenerator.between(0.0F, 2.0F);
        }

        default float getSizeFactor() {
            return 1f;
        }

        default boolean doCreeperGriefing(AbstractTCCreeper creeper) {
            return true;
        }

        @Nullable
        default Function<HolderLookup.Provider, LootTableSubProvider> getCreeperLoot(EntityType<?> type) {
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
                            HolderGetter<EntityType<?>> holdergetter = this.registries.lookupOrThrow(Registries.ENTITY_TYPE);
                            LootTable.Builder lootTable =
                                    LootTable.lootTable()
                                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                                    .add(LootItem.lootTableItem(TCCreeperContext.this.getMainDropItem())
                                                            .apply(SetItemCountFunction.setCount(TCCreeperContext.this.getDropRange()))
                                                            .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
                                            .withPool(LootPool.lootPool().add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                                                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER,
                                                            EntityPredicate.Builder.entity().of(holdergetter, EntityTypeTags.SKELETONS))));
                            this.add(type, TCCreeperContext.this.additionalBuilder(this.registries, lootTable));
                        }
                    };
                }
            };
        }

        default LootTable.Builder additionalBuilder(HolderLookup.Provider provider, LootTable.Builder lootTable) {
            lootTable = lootTable.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
                    .add(LootItem.lootTableItem(TCBlockCore.CREEPER_BOMB))
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, TCCreeperContext.this.getRank() != EnumTakumiRank.BOSS && TCCreeperContext.this.getRank().getLevel() > 1 ? 2f : 0f))));
            EnumTakumiElement element = TCCreeperContext.this.getElement();
            if (!element.isSPElement()) {
                Item item = TCElementCoreItem.getElementCoreFormElement(element.getMainElement());
                if (item != null) {
                    lootTable = lootTable.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
                            .add(LootItem.lootTableItem(item))
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, 1f)))
                            .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(provider, 0.025F, 0.01F)));
                }
            }
            return lootTable;
        }

        @OnlyIn(Dist.CLIENT)
        default ResourceLocation getArmor() {
            return TCClientUtils.POWER_LOCATION;
        }


        EntityType<?> entityType();

        default int getPrimaryColor() {
            return 894731;
        }

        default int getSecondaryColor() {
            return 0;
        }

        default AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes();
        }

        @OnlyIn(Dist.CLIENT)
        default void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<AbstractTCCreeper>) type, TCCreeperRenderer::new);
        }

        default List<TagKey<EntityType<?>>> getEntityTypeTags() {
            ArrayList list = new ArrayList();
            list.add(TCEntityCore.TAKUMIS);
            return list;
        }

        EnumTakumiElement getElement();

        EnumTakumiRank getRank();

        enum EnumTakumiRank {
            LOW(1, 5, 10, 150, 32),
            MID(2, 10, 50, 75, 16),
            HIGH(3, 100, 250, 0, 0),
            BOSS(4, 500, 500, 0, 0),
            TAKUMI(0, 0, 0, 0, 0);

            private final int level;
            private final int experiment;
            private final int point;
            private final int spawnWeight;
            private final int maxSpawn;

            EnumTakumiRank(int lv, int exp, int poi, int sw, int ms) {
                this.level = lv;
                this.experiment = exp;
                this.point = poi;
                this.spawnWeight = sw;
                this.maxSpawn = ms;
            }

            public int getSpawnWeight() {
                return spawnWeight;
            }

            public int getLevel() {
                return this.level;
            }

            public int getExperiment() {
                return experiment;
            }

            public int getPoint() {
                return point;
            }

            public int getMaxSpawn() {
                return maxSpawn;
            }

            public Component getRankName() {
                return switch (this.level) {
                    case 1 -> Component.translatable("takumicraft.rank.low");
                    case 2 -> Component.translatable("takumicraft.rank.mid");
                    case 3 -> Component.translatable("takumicraft.rank.high");
                    case 4 -> Component.translatable("takumicraft.rank.boss");
                    default -> Component.translatable("takumicraft.elem.underfound");
                };
            }
        }

        enum EnumTakumiElement {
            TAKUMI(0), FIRE(1), GRASS(2), GROUND(5), WIND(4), WATER(3), DRAGON(7), NORMAL(6), TAKUMI_D(0, true, false),
            FIRE_D(1, true, false), GRASS_D(2, true, false), GROUND_D(5, true, false), WIND_D(4, true, false),
            WATER_D(3, true, false), DRAGON_D(7, true, false), NORMAL_D(6, true, false), TAKUMI_M(0, false, true),
            FIRE_M(1, false, true), GRASS_M(2, false, true), GROUND_M(5, false, true), WIND_M(4, false, true),
            WATER_M(3, false, true), DRAGON_M(7, false, true), NORMAL_M(6, false, true), TAKUMI_MD(0, true, true),
            FIRE_MD(1, true, true), GRASS_MD(2, true, true), GROUND_MD(5, true, true), WIND_MD(4, true, true),
            WATER_MD(3, true, true), DRAGON_MD(7, true, true), NORMAL_MD(6, true, true), YUKARI(8, false, false);

            private final int id;
            private boolean isMagic;
            private boolean isDest;

            EnumTakumiElement(int i, boolean dest, boolean magic) {
                this(i);
                this.isDest = dest;
                this.isMagic = magic;
            }

            EnumTakumiElement(int i) {
                this.id = i;
            }

            public ResourceLocation getIcon() {
                return switch (this.id) {
                    case 0 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/takumi.png");
                    case 1 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/fire.png");
                    case 2 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/grass.png");
                    case 3 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/water.png");
                    case 4 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/wind.png");
                    case 5 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/ground.png");
                    case 6 -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/normal.png");
                    default -> ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/book/underfound.png");
                };
            }

            public Component getElementName() {
                return switch (this.id) {
                    case 0 -> Component.translatable("takumicraft.elem.takumi");
                    case 1 -> Component.translatable("takumicraft.elem.fire");
                    case 2 -> Component.translatable("takumicraft.elem.grass");
                    case 3 -> Component.translatable("takumicraft.elem.water");
                    case 4 -> Component.translatable("takumicraft.elem.wind");
                    case 5 -> Component.translatable("takumicraft.elem.ground");
                    case 6 -> Component.translatable("takumicraft.elem.normal");
                    default -> Component.translatable("takumicraft.elem.underfound");
                };
            }

            public String getElementID() {
                return switch (this.id) {
                    case 0 -> "takumi";
                    case 1 -> "fire";
                    case 2 -> "grass";
                    case 3 -> "water";
                    case 4 -> "wind";
                    case 5 -> "ground";
                    case 6 -> "normal";
                    default -> "undefined";
                };
            }

            public Component getSubElementName() {
                if (this.isDest && this.isMagic) {
                    return Component.translatable("takumicraft.attr.MD");
                } else if (this.isMagic) {
                    return Component.translatable("takumicraft.attr.M");
                } else if (this.isDest) {
                    return Component.translatable("takumicraft.attr.D");
                } else {
                    return CommonComponents.EMPTY;
                }
            }

            public int getElementColor() {
                return switch (this.id) {
                    case 1 -> 0xaa0000;
                    case 2 -> 0x00aa00;
                    case 3 -> 0x0000aa;
                    case 4 -> 0xdddddd;
                    case 5 -> 0xaaaa00;
                    default -> 0xffffff;
                };
            }

            public int getId() {
                return id;
            }

            public boolean isMagic() {
                return isMagic;
            }

            public boolean isDest() {
                return isDest;
            }

            public String getName() {
                String s = this.name().toLowerCase();
                if (s.contains("_")) {
                    s = s.split("_")[0];
                }
                return s;
            }

            public boolean isSPElement() {
                return this.id == 0 || this.id == 7 || this.id == 8;
            }

            public EnumTakumiElement getMainElement() {
                return switch (this.id) {
                    case 1 -> EnumTakumiElement.FIRE;
                    case 2 -> EnumTakumiElement.GRASS;
                    case 3 -> EnumTakumiElement.WATER;
                    case 4 -> EnumTakumiElement.WIND;
                    case 5 -> EnumTakumiElement.GROUND;
                    default -> EnumTakumiElement.NORMAL;
                };
            }

            public boolean getStrong(EnumTakumiElement enemyType) {
                if (this.id == 7 && enemyType.id == 7) {
                    return this.getStrongMD(enemyType);
                } else if (this.id != 0 && this.id != 6 && enemyType.id != 0 && enemyType.id != 6) {
                    if (enemyType.id - this.id == 1 || enemyType.id - this.id == -4) {
                        return this.getStrongMD(enemyType);
                    }
                } else if (this.id == 6 && enemyType.id != 0) {
                    return this.getStrongMD(enemyType);
                } else if (this.id == 0 && enemyType.id == 6) {
                    return this.getStrongMD(enemyType);
                }
                return false;
            }

            private boolean getStrongMD(EnumTakumiElement enemyType) {
                return !this.isDest && !this.isMagic && !enemyType.isDest && !enemyType.isMagic ||
                        this.isDest && enemyType.isMagic || this.isMagic && enemyType.isMagic;
            }
        }
    }
}
