package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCLanguageProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.registries.ForgeRegistries;

public class TCWoolBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {
    private final DyeColor color;
    private final Block baseBlock;

    public TCWoolBlock(DyeColor colorIn) {
        super(BlockBehaviour.Properties.of().mapColor(colorIn.getMapColor()).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).setId(TCBlockCore.TCBlockId("creeperwool_" + colorIn.getName())));
        this.color = colorIn;
        this.baseBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryBuild("minecraft", this.color.getName() + "_wool"));
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_WOOL_MAP.get(this.color), 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', this.baseBlock)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB))
                .group("creeperwool"));
    }

    @Override
    public String getRegistryName() {
        return "creeperwool_" + this.color.getName();
    }

    @Override
    public String getEnUSName() {
        return TCLanguageProvider.TCEnUSLanguageProvider.TC_ENUS_LANGMAP.get("takumicraft.color." + this.color.getName()) + " Wool";
    }

    @Override
    public String getJaJPName() {
        return TCLanguageProvider.TCJaJPLanguageProvider.TC_JAJP_LANGMAP.get("takumicraft.color." + this.color.getName()) + "匠式硬質羊毛";
    }
}
