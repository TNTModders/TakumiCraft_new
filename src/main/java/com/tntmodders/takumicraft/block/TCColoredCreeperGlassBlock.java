package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCLanguageProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class TCColoredCreeperGlassBlock extends TCCreeperGlassBlock {
    private final DyeColor color;
    private final Block baseBlock;

    public TCColoredCreeperGlassBlock(DyeColor colorIn) {
        super();
        this.color = colorIn;
        this.baseBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.withDefaultNamespace(this.color.getName() + "_stained_glass"));
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_COLORED_GLASS_MAP.get(this.color), 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', this.baseBlock).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB)).group("creeperglass"));
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.stainedGlassCubeAll(this);
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }

    @Override
    public String getRegistryName() {
        return "creeperglass_" + this.color.getName();
    }

    @Override
    public String getEnUSName() {
        return TCLanguageProvider.TCEnUSLanguageProvider.TC_ENUS_LANGMAP.get("takumicraft.color." + this.color.getName()) + " Stained Glass";
    }

    @Override
    public String getJaJPName() {
        return TCLanguageProvider.TCJaJPLanguageProvider.TC_JAJP_LANGMAP.get("takumicraft.color." + this.color.getName()) + "匠式硬質硝子";
    }
}
