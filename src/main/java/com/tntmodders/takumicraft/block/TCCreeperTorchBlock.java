package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCCreeperTorchBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.function.Function;

public class TCCreeperTorchBlock extends TorchBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperTorchBlock() {
        super(ParticleTypes.SMOKE, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(p_50886_ -> 15).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).explosionResistance(10000000f));
    }

    @Override
    public String getRegistryName() {
        return "creepertorch";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCCreeperTorchBlockItem(TCBlockCore.CREEPER_TORCH, TCBlockCore.CREEPER_TORCH_WALL);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Torch";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質松明";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.simpleBlock(this, provider.models().withExistingParent(provider.key(this).toString(), "block/torch").texture("torch", provider.blockTexture(this)).renderType("cutout"));
        ResourceLocation name = ResourceLocation.tryBuild(TakumiCraftCore.MODID, this.getRegistryName());
        provider.itemModels().singleTexture(name.getPath(), provider.mcLoc("item/generated"), "layer0", ResourceLocation.tryBuild(name.getNamespace(), "block/" + name.getPath()));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.WALL_POST_OVERRIDE);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, itemLike, 2).define('#', TCBlockCore.CREEPER_BOMB).define('S', Items.STICK).pattern("#").pattern("S").unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }
}
