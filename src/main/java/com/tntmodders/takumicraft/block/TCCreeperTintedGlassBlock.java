package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class TCCreeperTintedGlassBlock extends TCCreeperGlassBlock {

    public TCCreeperTintedGlassBlock() {
        super("creepertintedglass");
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_TINTED_GLASS, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.TINTED_GLASS).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)).group("creeperglass"));

        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_TINTED_GLASS, 2).define('#', TCBlockCore.CREEPER_GLASS).define('A', Items.AMETHYST_SHARD).pattern(" A ").pattern("A#A").pattern(" A ").unlockedBy("has_creeperglass", provider.hasItem(TCBlockCore.CREEPER_GLASS)).group("creeperglass"), "from_creeperglass");
    }

    @Override
    public String getRegistryName() {
        return "creepertintedglass";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Tinted Glass";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質遮光硝子";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.stainedGlassCubeAll(this);
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }

    @Override
    public String getBlockRenderType() {
        return "translucent";
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_154824_) {
        return false;
    }

    @Override
    protected int getLightBlock(BlockState p_154828_) {
        return 15;
    }
}
