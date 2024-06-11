package com.tntmodders.takumicraft.client.renderer.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

import static net.minecraft.client.renderer.Sheets.SIGN_SHEET;

public class TCCreeperHangingSignRenderer extends HangingSignRenderer {
    public TCCreeperHangingSignRenderer(BlockEntityRendererProvider.Context p_248772_) {
        super(p_248772_);
    }

    @Override
    protected Material getSignMaterial(WoodType p_251961_) {
        return new Material(SIGN_SHEET, new ResourceLocation(TakumiCraftCore.MODID, "entity/signs/creeperhangingsign"));
    }
}
