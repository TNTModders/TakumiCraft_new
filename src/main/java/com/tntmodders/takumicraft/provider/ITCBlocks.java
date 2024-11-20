package com.tntmodders.takumicraft.provider;

import com.ibm.icu.impl.Pair;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ITCBlocks extends ITCTranslator {
    String getRegistryName();

    default TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block);
    }

    default boolean hideOnCreativeTab() {
        return false;
    }

    default void drop(Block block, TCBlockLoot loot) {
        loot.dropSelf(block);
    }

    Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block);

    default List<TagKey<Block>> getBlockTags() {
        return new ArrayList<>();
    }

    default List<Pair<TagKey<Block>, TagKey<Item>>> getItemTags() {
        return new ArrayList<>();
    }

    default void onRemovedfromExplosionList(Level level, BlockPos pos) {
    }

    /**
     * If you need vanilla rendertype,see {@link net.minecraft.client.renderer.ItemBlockRenderTypes }.
     */
    default void registerStateAndModel(TCBlockStateProvider provider) {
        if (this instanceof Block block) {
            ModelFile model = provider.cubeAll(block);
            provider.simpleBlock(block, model);
            provider.simpleBlockItem(block, model);
        }
    }

    default String getBlockRenderType() {
        return "";
    }
}
