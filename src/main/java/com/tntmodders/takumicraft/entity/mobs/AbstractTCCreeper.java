package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.provider.ITCEntities;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import com.tntmodders.takumicraft.utils.client.TCClientUtils;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.world.ExplosionEvent;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTCCreeper extends Creeper implements ITCEntities {
    public AbstractTCCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean checkTakumiSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (type.create(levelAccessor.getLevel()) instanceof AbstractTCCreeper creeper) {
            TCCreeperContext.EnumTakumiRank rank = creeper.getContext().getRank();
            if (rank.level > 2 || random.nextInt(50) > rank.getSpawnWeight()) {
                return false;
            }
        }
        return checkMonsterSpawnRules(type, levelAccessor, spawnType, pos, random);
    }

    public abstract TCCreeperContext<? extends AbstractTCCreeper> getContext();

    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isThundering()) {
            this.setPowered(true);
        }
    }

    public void setPowered(boolean flg) {
        this.entityData.set(Creeper.DATA_IS_POWERED, flg);
    }

    @Override
    public Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getEntityLoot() {
        return () -> new EntityLoot() {
            @Override
            protected Iterable<EntityType<?>> getKnownEntities() {
                return TCEntityCore.ENTITY_TYPES;
            }

            @Override
            protected boolean isNonLiving(EntityType<?> entitytype) {
                return false;
            }

            @Override
            protected void addTables() {
                this.add(AbstractTCCreeper.this.getType(), LootTable.lootTable().withPool(
                                LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.GUNPOWDER)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
                        .withPool(LootPool.lootPool().add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS)))));
            }
        };
    }

    public interface TCCreeperContext<T extends AbstractTCCreeper> extends ITCTranslator {
        default boolean showRead() {
            return false;
        }

        String getRegistryName();

        String getJaJPRead();

        String getEnUSDesc();

        String getJaJPDesc();

        default void registerSpawn(EntityType<AbstractTCCreeper> type) {
            SpawnPlacements.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractTCCreeper::checkTakumiSpawnRules);
        }

        @Nullable
        default Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getCreeperLoot(EntityType<?> type) {
            return () -> new EntityLoot() {
                @Override
                protected Iterable<EntityType<?>> getKnownEntities() {
                    return NonNullList.of(type);
                }

                @Override
                protected void addTables() {
                    this.add(type,
                            LootTable.lootTable()
                                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                                            .add(LootItem.lootTableItem(Items.GUNPOWDER)
                                                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))))
                                    .withPool(LootPool.lootPool().add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS))
                                            .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER,
                                                    EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS))))
                                    .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
                                            .add(LootItem.lootTableItem(TCBlockCore.CREEPER_BOMB))
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0f, TCCreeperContext.this.getRank().level > 1 ? 2f : 0f))))
                    );
                }
            };
        }

        @OnlyIn(Dist.CLIENT)
        default ResourceLocation getArmor() {
            return TCClientUtils.POWER_LOCATION;
        }


        EntityType<?> entityType();

        int getPrimaryColor();

        int getSecondaryColor();

        AttributeSupplier.Builder entityAttribute();

        @OnlyIn(Dist.CLIENT)
        void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type);

        EnumTakumiElement getElement();

        EnumTakumiRank getRank();

        enum EnumTakumiRank {
            LOW(1, 5, 10, 50),
            MID(2, 10, 50, 25),
            HIGH(3, 100, 250, 0),
            BOSS(4, 500, 500, 0),
            TAKUMI(0, 0, 0, 0);

            private final int level;
            private final int experiment;
            private final int point;
            private final int spawnWeight;

            EnumTakumiRank(int lv, int exp, int poi, int sw) {
                this.level = lv;
                this.experiment = exp;
                this.point = poi;
                this.spawnWeight = sw;
            }

            public int getSpawnWeight() {
                return spawnWeight;
            }

            public int getLevel() {
                return level;
            }

            public int getExperiment() {
                return experiment;
            }

            public int getPoint() {
                return point;
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
                    case 0 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/takumi.png");
                    case 1 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/fire.png");
                    case 2 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/grass.png");
                    case 3 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/water.png");
                    case 4 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/wind.png");
                    case 5 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/ground.png");
                    case 6 -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/normal.png");
                    default -> new ResourceLocation(TakumiCraftCore.MODID, "textures/book/underfound.png");
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
