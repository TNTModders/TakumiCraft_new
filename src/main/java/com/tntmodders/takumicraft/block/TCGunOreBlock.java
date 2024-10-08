package com.tntmodders.takumicraft.block;

import com.ibm.icu.impl.Pair;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Function;

public class TCGunOreBlock extends AbstractTCBombBlock implements ITCRecipe {

    private final UniformInt xpRange = UniformInt.of(3, 8);

    public TCGunOreBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5f, 0f).mapColor(MapColor.STONE), "gunore");
    }

    public TCGunOreBlock(Properties properties, String name) {
        super(properties, name);
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? this.xpRange.sample(randomSource) : 0;
    }

    @Override
    public float getPower() {
        return 2f;
    }

    @Override
    public String getEnUSName() {
        return "Gunpowder Ore";
    }

    @Override
    public String getJaJPName() {
        return "火薬岩";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput output) {
        provider.saveSmeltingRecipe(itemLike, output, SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemLike),
                RecipeCategory.BUILDING_BLOCKS, Items.GUNPOWDER, 0.5f, 100));
        provider.saveBlastingRecipe(itemLike, output, SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemLike),
                RecipeCategory.BUILDING_BLOCKS, Items.GUNPOWDER, 0.5f, 50));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(BlockTags.NEEDS_STONE_TOOL, TCBlockCore.GUNORES, BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.ORES, Tags.Blocks.ORE_RATES_SINGULAR, this.oreTag(), TCBlockCore.EXPLOSIVES);
    }

    protected TagKey<Block> oreTag() {
        return Tags.Blocks.ORES_IN_GROUND_STONE;
    }

    @Override
    public List<Pair<TagKey<Block>, TagKey<Item>>> getItemTags() {
        return List.of(Pair.of(TCBlockCore.GUNORES, TCItemCore.GUNORES), Pair.of(TCBlockCore.EXPLOSIVES, TCItemCore.EXPLOSIVES));
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.add(block, p_250359_ -> loot.createOreDropWithMinesweeper(p_250359_, Items.GUNPOWDER, UniformGenerator.between(1f, 4f)));
    }
}
