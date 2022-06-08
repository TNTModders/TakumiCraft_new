package com.tntmodders.takumicraft.client.renderer.entity;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class TCCreeperRenderer extends CreeperRenderer {
    private boolean isBright = false;

    public TCCreeperRenderer(EntityRendererProvider.Context p_173958_) {
        super(p_173958_);
    }

    public TCCreeperRenderer(EntityRendererProvider.Context p_173958_, boolean isBright) {
        this(p_173958_);
        this.isBright = isBright;
    }

    @Override
    public ResourceLocation getTextureLocation(Creeper creeper) {
        return new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().toShortString() + ".png");
    }

    @Override
    protected int getBlockLightLevel(Creeper p_114496_, BlockPos p_114497_) {
        return this.isBright ? 15 : super.getBlockLightLevel(p_114496_, p_114497_);
    }
}
