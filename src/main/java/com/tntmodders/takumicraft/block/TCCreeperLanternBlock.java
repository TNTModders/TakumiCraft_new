package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.function.Function;

public class TCCreeperLanternBlock extends LanternBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperLanternBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().requiresCorrectToolForDrops().strength(3.5F, 100000F).sound(SoundType.LANTERN).lightLevel(p_187433_ -> 15).noOcclusion().pushReaction(PushReaction.DESTROY).setId(TCBlockCore.TCBlockId("creeperlantern")));
    }

    @Override
    public String getRegistryName() {
        return "creeperlantern";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_LANTERN, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', Blocks.LANTERN)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Lantern";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質吊灯";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().withExistingParent(provider.key(this).toString(), "block/lantern").texture("lantern", provider.blockTexture(this)).renderType("cutout");
        ModelFile model_hanging = provider.models().withExistingParent(provider.key(this).toString() + "_hanging", "block/lantern_hanging").texture("lantern", provider.blockTexture(this)).renderType("cutout");
        provider.getVariantBuilder(this)
                .partialState().with(TCCreeperLanternBlock.HANGING, false).setModels(new ConfiguredModel(model))
                .partialState().with(TCCreeperLanternBlock.HANGING, true).setModels(new ConfiguredModel(model_hanging));
        provider.itemModels().basicItem(this.asItem());
    }

    @Override
    public String getBlockRenderType() {
        return "cutout";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }
}
