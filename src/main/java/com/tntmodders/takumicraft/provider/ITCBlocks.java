package com.tntmodders.takumicraft.provider;

import com.ibm.icu.impl.Pair;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface ITCBlocks extends ITCTranslator {
    String getRegistryName();

    default TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block);
    }

    default boolean hideOnCreativeTab() {
        return false;
    }

    default EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.SIMPLE;
    }

    default void drop(Block block, TCBlockLoot loot) {
        loot.dropSelf(block);
    }

    Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block);

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
    enum EnumTCBlockStateModelType {
        SIMPLE("solid"),
        SIDE("solid"),
        SLAB("solid"),
        STAIRS("solid"),
        WALL("solid"),
        FENCE("solid"),
        FENCE_GATE("solid"),
        DOOR("cutout"),
        TRAP_DOOR("cutout"),
        GLASS("cutout"),
        STAINED_GLASS("translucent"),
        PANE_GLASS("cutout"),
        PANE_STAINED_GLASS("translucent"),
        LADDER("cutout"),
        SCCAFOLDING("cutout"),
        CARPET("cutout"),
        BED("cutout"),
        BARREL("solid"),
        SLIME("translucent"),
        ANIMATED("solid"),
        NONE("solid");

        private final String type;

        EnumTCBlockStateModelType(String renderType) {
            this.type = renderType;
        }

        public String getType() {
            return type;
        }
    }
}
