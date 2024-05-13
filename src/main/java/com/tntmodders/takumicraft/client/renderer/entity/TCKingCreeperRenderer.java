package com.tntmodders.takumicraft.client.renderer.entity;

import com.tntmodders.takumicraft.client.renderer.entity.layer.TCKingCreeperLayer;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TCKingCreeperRenderer<T extends TCKingCreeper> extends TCCreeperRenderer<T> {
    public TCKingCreeperRenderer(EntityRendererProvider.Context context, AbstractTCCreeper.TCCreeperContext creeperContext) {
        super(context, true, creeperContext);
        this.addLayer(new TCKingCreeperLayer<>(this));
    }
}
