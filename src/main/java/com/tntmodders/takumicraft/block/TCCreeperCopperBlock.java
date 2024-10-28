package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.List;

public class TCCreeperCopperBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {

    private final Block base;
    private final WeatheringCopper.WeatherState state;

    public TCCreeperCopperBlock(WeatheringCopper.WeatherState weather, Block block, Properties properties) {
        super(properties);
        this.state = weather;
        this.base = block;
    }

    public TCCreeperCopperBlock(WeatheringCopper.WeatherState weather, Block block) {
        this(weather, block, Properties.of().requiresCorrectToolForDrops().strength(5.0F, 1000000.0F).sound(SoundType.COPPER));
    }

    protected String getBaseRegistryName() {
        return "creepercopper";
    }

    protected String baseEnUSName() {
        return "Creeper Copper";
    }

    protected String baseJaJPName() {
        return "匠式硬質銅";
    }

    @Override
    public String getRegistryName() {
        return this.getBaseRegistryName() + (this.state == WeatheringCopper.WeatherState.UNAFFECTED ? "" : "_" + this.state.getSerializedName());
    }

    @Override
    public String getEnUSName() {
        switch (this.state) {
            case EXPOSED -> {
                return this.baseEnUSName() + " [Exposed]";
            }
            case OXIDIZED -> {
                return this.baseEnUSName() + " [Oxidized]";
            }
            case WEATHERED -> {
                return this.baseEnUSName() + " [Weathered]";
            }
            default -> {
                return this.baseEnUSName();
            }
        }
    }

    @Override
    public String getJaJPName() {
        switch (this.state) {
            case EXPOSED -> {
                return this.baseJaJPName() + "[風化]";
            }
            case OXIDIZED -> {
                return this.baseJaJPName() + "[酸化]";
            }
            case WEATHERED -> {
                return this.baseJaJPName() + "[錆]";
            }
            default -> {
                return this.baseJaJPName();
            }
        }
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS,
                        this, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', base)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB))
                .group(this.getBaseRegistryName()));
    }
}
