package com.tntmodders.takumicraft.client.renderer.entity;

import com.tntmodders.takumicraft.TakumiCraftCore;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class TCCreeperRenderer extends CreeperRenderer {
    public TCCreeperRenderer(EntityRendererProvider.Context p_173958_) {
        super(p_173958_);
    }

    @Override
    public ResourceLocation getTextureLocation(Creeper creeper) {
        return new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/" + creeper.getType().getRegistryName().getPath() + ".png");
    }
}
