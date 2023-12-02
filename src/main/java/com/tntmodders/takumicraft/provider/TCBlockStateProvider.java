package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.*;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraftforge.client.model.generators.*;
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
                switch (((ITCBlocks) block).getBlockStateModelType()) {
                    case SIMPLE -> this.simpleBlockWithItem(block);
                    case STAIRS -> this.stairsBlockWithItem(block);
                    case SLAB -> this.halfBlockWithItem(block);
                    case WALL -> this.wallBlockWithItem(block);
                    case FENCE -> this.fenceBlockWithItem(block);
                    case FENCE_GATE -> this.fenceGateBlockWithItem(block);
                    case GLASS -> this.glassBlockWithItem(block);
                    case STAINED_GLASS -> this.stainedGlassBlockWithItem(block);
                    case PANE_GLASS, PANE_STAINED_GLASS ->
                            this.paneGlassBlockWithItem(block, ((ITCBlocks) block).getBlockStateModelType());
                    case LADDER -> this.ladderBlockWithItem(block);
                    case SCCAFOLDING -> this.scaffoldingBlockWithItem(block);
                    case DOOR -> this.doorBlockWithItem(block);
                    case TRAP_DOOR -> this.trapdoorBlockWithItem(block);
                    case CARPET -> this.carpetBlockWithItem(block);
                }
                TCLoggingUtils.entryRegistry("BlockStateModel_" + ((ITCBlocks) block).getBlockStateModelType().name(), ((ITCBlocks) block).getRegistryName());
            }
        });

        TCLoggingUtils.completeRegistry("BlockStateModel");
    }

    private void carpetBlockWithItem(Block block) {
        if(block instanceof TCCarpetBlock carpet){
            ModelFile model = models().withExistingParent(carpet.getRegistryName(), "block/carpet").texture("wool", blockTexture(TCBlockCore.CREEPER_WOOL_MAP.get(carpet.getColor())));
            this.simpleBlock(carpet,model);
            this.simpleBlockItem(carpet,model);
        }
    }

    private void trapdoorBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionTrapDoorBlock door) {
            this.trapdoorBlockWithRenderType(door, blockTexture(door), door.isOrientable(), "cutout");
            itemModels().withExistingParent(door.getRegistryName(), blockFolder(new ResourceLocation(TakumiCraftCore.MODID, door.getRegistryName() + "_bottom")));
        }
    }

    private void doorBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionDoorBlock door) {
            this.doorBlockWithRenderType(door, blockFolder(new ResourceLocation(TakumiCraftCore.MODID, key(door.getBaseBlock()).getPath() + "_door_bottom")), blockFolder(new ResourceLocation(TakumiCraftCore.MODID, key(door.getBaseBlock()).getPath() + "_door_top")), "cutout");
            this.singleBlockItem(block, blockTexture(block));
        }
    }

    private void fenceGateBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionFenceGateBlock fence) {
            this.fenceGateBlock(fence, blockTexture(fence.getBaseBlock()));
            itemModels().withExistingParent(fence.getRegistryName(), blockFolder(key(fence)));
        }
    }

    private void fenceBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionFenceBlock fence) {
            this.fenceBlock(fence, blockTexture(fence.getBaseBlock()));
            ModelFile model = models().withExistingParent(fence.getRegistryName(), "block/fence_inventory").texture("texture", blockTexture(fence.getBaseBlock()));
            this.simpleBlockItem(block, model);
        }
    }

    private void simpleBlockWithItem(Block block) {
        ModelFile model = this.cubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private void scaffoldingBlockWithItem(Block block) {
        ResourceLocation top = blockFolder(new ResourceLocation(TakumiCraftCore.MODID, name(block) + "_top"));
        ResourceLocation side = blockFolder(new ResourceLocation(TakumiCraftCore.MODID, name(block) + "_side"));
        ResourceLocation bottom = blockFolder(new ResourceLocation(TakumiCraftCore.MODID, name(block) + "_bottom"));

        ModelFile model_stable = this.models().withExistingParent(name(block) + "_stable", "scaffolding_stable").texture("particle", top)
                .texture("top", top).texture("side", side).texture("bottom", bottom).renderType("cutout");
        ModelFile model_unstable = this.models().withExistingParent(name(block) + "_unstable", "scaffolding_unstable").texture("particle", top)
                .texture("top", top).texture("side", side).texture("bottom", bottom).renderType("cutout");

        this.getVariantBuilder(block).partialState().with(ScaffoldingBlock.BOTTOM, false).addModels(new ConfiguredModel(model_stable)).partialState().with(ScaffoldingBlock.BOTTOM, true).addModels(new ConfiguredModel(model_unstable));
        this.simpleBlockItem(block, model_stable);
    }

    private void ladderBlockWithItem(Block block) {
        ResourceLocation location = blockTexture(block);
        ModelFile model = this.models().withExistingParent(name(block), "ladder").texture("particle", location).texture("texture", location).renderType("cutout");
        this.horizontalBlock(block, model);
        this.singleBlockItem(block, location);
    }

    private void glassBlockWithItem(Block block) {
        ModelFile model = this.glassCubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private ModelFile glassCubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block)).renderType("cutout");
    }

    private void paneGlassBlockWithItem(Block block, ITCBlocks.EnumTCBlockStateModelType type) {
        if (block instanceof TCCreeperGlassPaneBlock pane) {
            ResourceLocation sourceName = blockTexture(pane.getBaseTakumiBlock());
            if (type == ITCBlocks.EnumTCBlockStateModelType.PANE_GLASS) {
                ResourceLocation topName = blockFolder(new ResourceLocation(TakumiCraftCore.MODID, "creeperglasspane_top"));
                this.paneBlockWithRenderType(pane, sourceName, topName, type.getType());
            } else {
                this.paneBlockWithRenderType(pane, sourceName, sourceName, type.getType());
            }
            this.singleBlockItem(block, sourceName);
        }

    }

    private void stainedGlassBlockWithItem(Block block) {
        ModelFile model = this.stainedGlassCubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    private ModelFile stainedGlassCubeAll(Block block) {
        return models().cubeAll(name(block), blockTexture(block)).renderType("translucent");
    }

    private void halfBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionHalfBlock slab) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            Direction.stream().forEach(direction -> builder.partialState().with(TCAntiExplosionHalfBlock.FACING, direction).addModels(new ConfiguredModel(models().withExistingParent(name(slab) + "_" + direction.getName(), new ResourceLocation(TakumiCraftCore.MODID, "half_" + direction.getName())).texture("top", blockFolder(new ResourceLocation(TakumiCraftCore.MODID, slab.getTopTextureName()))).texture("side", blockFolder(new ResourceLocation(TakumiCraftCore.MODID, slab.getTextureName()))).texture("bottom", blockFolder(new ResourceLocation(TakumiCraftCore.MODID, slab.getBottomTextureName()))).renderType("cutout"))));
            itemModels().withExistingParent(slab.getRegistryName(), blockFolder(new ResourceLocation(TakumiCraftCore.MODID, slab.getRegistryName() + "_east")));
        }
    }

    private void stairsBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionStairsBlock stairs) {
            this.stairsBlock(stairs, blockTexture(stairs.getBaseBlock()));
            String location = stairs.getRegistryName();
            itemModels().withExistingParent(location, blockFolder(new ResourceLocation(TakumiCraftCore.MODID, location)));
        }
    }

    private void wallBlockWithItem(Block block) {
        if (block instanceof TCAntiExplosionWallBlock wall) {
            this.wallBlock(wall, blockTexture(wall.getBaseBlock()));
            ModelFile model = models().withExistingParent(wall.getRegistryName(), "block/wall_inventory").texture("wall", blockTexture(wall.getBaseBlock()));
            this.simpleBlockItem(block, model);
        }
    }

    private void singleBlockItem(Block block, ResourceLocation sourceName) {
        itemModels().getBuilder(key(block).getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", sourceName);
    }

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    private ResourceLocation blockFolder(ResourceLocation name) {
        return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + '/' + name.getPath());
    }

    private String name(Block block) {
        return key(block).getPath();
    }


}
