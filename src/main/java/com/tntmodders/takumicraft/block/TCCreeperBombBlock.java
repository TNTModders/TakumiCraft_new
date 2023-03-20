package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class TCCreeperBombBlock extends AbstractTCBombBlock implements ITCRecipe {
    public static final ToIntFunction<BlockState> LIGHT_TCBOMB = (state) -> 2;

    public TCCreeperBombBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE).strength(0.1f, 0f).color(MaterialColor.COLOR_LIGHT_GREEN).lightLevel(LIGHT_TCBOMB), "creeperbomb");
    }

    @Override
    public float getPower() {
        return 5f;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Bomb";
    }

    @Override
    public String getJaJPName() {
        return "匠式高性能爆弾";
    }

    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, false);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, Consumer<FinishedRecipe> consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                        TCBlockCore.CREEPER_BOMB)
                .define('#', Items.CREEPER_HEAD)
                .pattern(" # ")
                .pattern("###")
                .pattern(" # ")
                .unlockedBy("has_gunpowder", TCRecipeProvider.hasItem(Items.CREEPER_HEAD)));
    }
}
