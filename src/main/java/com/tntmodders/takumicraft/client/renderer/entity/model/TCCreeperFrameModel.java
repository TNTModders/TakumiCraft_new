package com.tntmodders.takumicraft.client.renderer.entity.model;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TCCreeperFrameModel extends BakedModelWrapper {

    public TCCreeperFrameModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> list = super.getQuads(state, side, rand, extraData, renderType);
        return overrideQuads(list);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        List<BakedQuad> list = super.getQuads(state, side, rand);
        return overrideQuads(list);
    }

    private List<BakedQuad> overrideQuads(List<BakedQuad> list) {
        List<BakedQuad> newList = new ArrayList<>();
        list.forEach(bakedQuad -> {
            TextureAtlasSprite sprite = bakedQuad.getSprite();
            TextureAtlasSprite newSprite = null;
            if (sprite.contents().name().equals(new ResourceLocation("block/birch_planks"))) {
                TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
                newSprite = atlas.getSprite(new ResourceLocation(TakumiCraftCore.MODID, "block/creeperplanks"));
            } else if (sprite.contents().name().equals(new ResourceLocation("block/item_frame"))) {
                TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
                newSprite = atlas.getSprite(new ResourceLocation(TakumiCraftCore.MODID, "block/creeperframe"));
            } else if (sprite.contents().name().equals(new ResourceLocation("block/glow_item_frame"))) {
                TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
                newSprite = atlas.getSprite(new ResourceLocation(TakumiCraftCore.MODID, "block/creeperframe_glowing"));
            }
            if (newSprite != null) {
                BakedQuad quad = new BakedQuad(bakedQuad.getVertices(), bakedQuad.getTintIndex(), bakedQuad.getDirection(), newSprite, bakedQuad.isShade(), bakedQuad.hasAmbientOcclusion());
                newList.add(quad);
            }
        });
        return newList;
    }
}
