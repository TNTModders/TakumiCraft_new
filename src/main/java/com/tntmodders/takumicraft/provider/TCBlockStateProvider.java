package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class TCBlockStateProvider extends BlockStateProvider {
    public TCBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TakumiCraftCore.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        TCLoggingUtils.startRegistry("BlockStateModel");
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCBlocks) {
                ((ITCBlocks) block).registerStateAndModel(this);
                TCLoggingUtils.entryRegistry("BlockStateModel", ((ITCBlocks) block).getRegistryName());
            }
        });
        TCLoggingUtils.completeRegistry("BlockStateModel");
    }

    public void sideBlockWithItem(Block block) {
        String name = name(block);
        ModelFile model = this.models().cubeBottomTop(name, blockFolder(new ResourceLocation(TakumiCraftCore.MODID, name + "_side")), blockFolder(new ResourceLocation(TakumiCraftCore.MODID, name + "_bottom")), blockFolder(new ResourceLocation(TakumiCraftCore.MODID, name + "_top")));
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    public void simpleBlockWithItem(Block block) {
        ModelFile model = this.cubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    public ModelFile glassCubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block)).renderType("cutout");
    }

    public ModelFile stainedGlassCubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block)).renderType("translucent");
    }

    public void singleBlockItem(Block block, ResourceLocation sourceName) {
        itemModels().getBuilder(key(block).getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", sourceName);
    }

    public ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public ResourceLocation blockFolder(ResourceLocation name) {
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + '/' + name.getPath());
    }

    public String name(Block block) {
        return key(block).getPath();
    }


}
