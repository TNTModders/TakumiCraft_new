package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.provider.ITCEntities;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTCCreeper extends Creeper implements ITCEntities {
    public AbstractTCCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

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

    public abstract static class TCCreeperContext<T extends AbstractTCCreeper> implements ITCTranslator {
        public abstract EntityType<?> entityType();

        public abstract int getPrimaryColor();

        public abstract int getSecondaryColor();

        public abstract AttributeSupplier.Builder entityAttribute();

        @OnlyIn(Dist.CLIENT)
        public abstract void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type);
    }
}
