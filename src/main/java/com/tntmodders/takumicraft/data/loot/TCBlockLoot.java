package com.tntmodders.takumicraft.data.loot;

import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TCBlockLoot extends BlockLootSubProvider {

    private final Block block;

    public TCBlockLoot(HolderLookup.Provider provider, Block blockIn, boolean isExplosionResistance) {
        super(isExplosionResistance ? Stream.of(blockIn).map(ItemLike::asItem).collect(Collectors.toSet()) : Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        this.block = blockIn;
    }

    //@TODO BINDING_CURSE->MINESWEEPER
    protected LootItemCondition.Builder hasMinesweeper() {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return MatchTool.toolMatches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(registrylookup.getOrThrow(Enchantments.BINDING_CURSE), MinMaxBounds.Ints.atLeast(1))))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(block);
    }

    @Override
    protected void generate() {
        if (block instanceof ITCBlocks) {
            ((ITCBlocks) block).drop(block, this);
        } else {
            this.dropSelf(block);
        }
    }

    @Override
    public void add(Block p_250610_, LootTable.Builder p_249817_) {
        super.add(p_250610_, p_249817_);
    }

    @Override
    public void add(Block p_251966_, Function<Block, LootTable.Builder> p_251699_) {
        super.add(p_251966_, p_251699_);
    }

    @Override
    public void dropSelf(Block p_249181_) {
        super.dropSelf(p_249181_);
    }

    @Override
    public LootTable.Builder createSingleItemTable(ItemLike p_251912_) {
        return super.createSingleItemTable(p_251912_);
    }

    public LootTable.Builder createSingleItemTableWithMinesweeper(ItemLike p_251912_) {
        return LootTable.lootTable().withPool(LootPool.lootPool().when(this.hasMinesweeper().or(hasSilkTouch())).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_251912_)));
    }

    @Override
    public void dropWhenSilkTouch(Block p_250855_) {
        super.dropWhenSilkTouch(p_250855_);
    }

    @Override
    public LootTable.Builder createOreDrop(Block p_250450_, Item p_249745_) {
        return super.createOreDrop(p_250450_, p_249745_);
    }


    @Override
    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block p_252154_, Property<T> p_250272_, T p_250292_) {
        return super.createSinglePropConditionTable(p_252154_, p_250272_, p_250292_);
    }

    @Override
    public LootTable.Builder createDoorTable(Block p_252166_) {
        return super.createDoorTable(p_252166_);
    }

    public LootTable.Builder createOreDropWithMinesweeper(Block block, Item item, UniformGenerator generator) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f)).add(LootItem.lootTableItem(block)).when(hasSilkTouch())).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f)).add(LootItem.lootTableItem(item)).apply(SetItemCountFunction.setCount(generator)).apply(ApplyBonusCount.addUniformBonusCount(this.registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE))).when(hasMinesweeper().and(doesNotHaveSilkTouch())));
    }
}
