package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class TCRenderCore {
    public static void registerEntityRender(EntityRenderersEvent.RegisterRenderers event) {
        TCLoggingUtils.startRegistry("EntityRenderer");
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> context.registerRenderer(event, context.entityType()));
        TCLoggingUtils.completeRegistry("EntityRenderer");
    }
}
