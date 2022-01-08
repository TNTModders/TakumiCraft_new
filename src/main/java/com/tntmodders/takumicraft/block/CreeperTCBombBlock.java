package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CreeperTCBombBlock extends AbstractTCBombBlock implements ITCRecipe {
    public CreeperTCBombBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE).strength(0.1f, 0f).color(MaterialColor.COLOR_LIGHT_GREEN), "creeperbomb");
    }

    @Override
    public float getPower() {
        return 5f;
    }

    @Override
    public String getEnUSname() {
        return "Creeper Bomb";
    }

    @Override
    public String getJaJPname() {
        return "匠式高性能爆弾";
    }

    @Override
    public Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot() {
        return () -> new BlockLoot() {
            @Override
            protected @NotNull Iterable<Block> getKnownBlocks() {
                return TCBlockCore.BLOCKS;
            }

            @Override
            protected void addTables() {
                this.dropSelf(TCBlockCore.CREEPER_BOMB);
            }
        };
    }

    @Override
    public RecipeBuilder addRecipe() {
        return ShapedRecipeBuilder.shaped(TCBlockCore.CREEPER_BOMB)
                .define('#', Items.CREEPER_HEAD)
                .pattern(" # ")
                .pattern("###")
                .pattern(" # ")
                .unlockedBy("has_gunpowder", TCRecipeProvider.hasItem(Items.CREEPER_HEAD));
    }
}
