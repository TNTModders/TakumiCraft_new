package com.tntmodders.takumicraft.client.renderer.entity;

import com.tntmodders.takumicraft.client.renderer.entity.layer.TCMuscleCreeperArmLayer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TCMuscleCreeperRenderer extends TCCreeperRenderer {
    public TCMuscleCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, false, TCEntityCore.MUSCLE);
        this.addLayer(new TCMuscleCreeperArmLayer(this, context.getModelSet()));
    }
}
