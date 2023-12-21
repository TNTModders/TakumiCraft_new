package com.tntmodders.takumicraft.block;

import com.ibm.icu.impl.Pair;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Supplier;

public class TCGunOreBlock extends AbstractTCBombBlock implements ITCRecipe {

    private final UniformInt xpRange = UniformInt.of(3, 8);

    public TCGunOreBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5f, 0f).mapColor(MapColor.STONE), "gunore");
    }

    public TCGunOreBlock(Properties properties, String name) {
        super(properties, name);
    }

    @Override
    public void spawnAfterBreak(BlockState p_221086_, ServerLevel p_221087_, BlockPos p_221088_, ItemStack p_221089_, boolean p_221090_) {
        super.spawnAfterBreak(p_221086_, p_221087_, p_221088_, p_221089_, p_221090_);
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
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
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, false);
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
        return List.of(BlockTags.NEEDS_STONE_TOOL, TCBlockCore.GUNORES, BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.ORES, Tags.Blocks.ORE_RATES_SINGULAR, this.oreTag());
    }

    protected TagKey<Block> oreTag() {
        return Tags.Blocks.ORES_IN_GROUND_STONE;
    }

    @Override
    public List<Pair<TagKey<Block>, TagKey<Item>>> getItemTags() {
        return List.of(Pair.of(TCBlockCore.GUNORES, TCItemCore.GUNORES));
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.add(block, p_250359_ -> loot.createOreDropWithMinesweeper(p_250359_, Items.GUNPOWDER, UniformGenerator.between(1f, 4f)));
    }
}
