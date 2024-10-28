package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.generators.ModelFile;

public class TCCreeperIlluminationBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {
    public TCCreeperIlluminationBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.HAT).strength(3.0f, 1000000f).lightLevel(p_152688_ -> 15).noOcclusion().isRedstoneConductor(TCBlockCore::never).sound(SoundType.GLASS));
    }

    @Override
    public String getRegistryName() {
        return "creeperillumination";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Illumination";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質角灯";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().withExistingParent(provider.name(this), "block/beacon").texture("particle", provider.blockTexture(TCBlockCore.CREEPER_GLASS)).texture("glass", provider.blockTexture(TCBlockCore.CREEPER_GLASS)).texture("beacon", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperbeacon"))).renderType("cutout");
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }


    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_ILLUMINATION, 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.BEACON).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }
}
