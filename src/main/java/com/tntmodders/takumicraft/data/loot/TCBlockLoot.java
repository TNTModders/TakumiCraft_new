package com.tntmodders.takumicraft.data.loot;

import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TCBlockLoot extends BlockLootSubProvider {

    public static final LootItemCondition.Builder HAS_MINESWEEPER = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(TCEnchantmentCore.MINESWEEPER, MinMaxBounds.Ints.atLeast(1))));
    public static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    private final Block block;

    public TCBlockLoot(Block blockIn, boolean isExplosionResistance) {
        super(isExplosionResistance ? Stream.of(blockIn).map(ItemLike::asItem).collect(Collectors.toSet()) : Set.of(),
                FeatureFlags.REGISTRY.allFlags());
        this.block = blockIn;
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
        return LootTable.lootTable().withPool(LootPool.lootPool().when(TCBlockLoot.HAS_MINESWEEPER).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(p_251912_)));
    }

    @Override
    public void dropWhenSilkTouch(Block p_250855_) {
        super.dropWhenSilkTouch(p_250855_);
    }

    @Override
    public LootTable.Builder createOreDrop(Block p_250450_, Item p_249745_) {
        return super.createOreDrop(p_250450_, p_249745_);
    }
}
