package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.TCLanguageProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class TCColoredCreeperGlassPaneBlock extends TCCreeperGlassPaneBlock {
    private final DyeColor color;
    private final Block baseBlock;

    public TCColoredCreeperGlassPaneBlock(DyeColor colorIn) {
        super(TCBlockCore.CREEPER_COLORED_GLASS_MAP.get(colorIn));
        this.color = colorIn;
        this.baseBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.color.getName() + "_stained_glass_pane"));
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_COLORED_GLASS_PANE_MAP.get(this.color), 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', this.baseBlock)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB))
                .group("creeperglasspane"));

        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_COLORED_GLASS_PANE_MAP.get(this.color), 16)
                .define('#', TCBlockCore.CREEPER_COLORED_GLASS_MAP.get(this.color))
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_creeperglass", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_COLORED_GLASS_MAP.get(this.color)))
                .group("creeperglasspane_from_creeperglass"), "from_creeperglass");
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.PANE_STAINED_GLASS;
    }

    @Override
    public String getRegistryName() {
        return "creeperglasspane_" + this.color.getName();
    }

    @Override
    public String getEnUSName() {
        return TCLanguageProvider.TCEnUSLanguageProvider.TC_ENUS_LANGMAP.get("takumicraft.color." + this.color.getName()) + " Stained Glass Pane";
    }

    @Override
    public String getJaJPName() {
        return TCLanguageProvider.TCJaJPLanguageProvider.TC_JAJP_LANGMAP.get("takumicraft.color." + this.color.getName()) + "匠式硬質板硝子";
    }
}
