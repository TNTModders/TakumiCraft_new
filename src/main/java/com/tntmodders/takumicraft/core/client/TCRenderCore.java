package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCChildCreeperModel;
import com.tntmodders.takumicraft.client.renderer.block.model.TCSaberModel;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class TCRenderCore {
    public static final ModelLayerLocation CHILD = new ModelLayerLocation(new ResourceLocation(TakumiCraftCore.MODID, TCEntityCore.CHILD.getRegistryName()), TCEntityCore.CHILD.getRegistryName());
    public static final ModelLayerLocation SABER = new ModelLayerLocation(new ResourceLocation(TakumiCraftCore.MODID, "typesword_normal"), "typesword_normal");

    public static void registerEntityRender(EntityRenderersEvent.RegisterRenderers event) {
        TCLoggingUtils.startRegistry("EntityRenderer");
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> context.registerRenderer(event, context.entityType()));
        TCLoggingUtils.completeRegistry("EntityRenderer");
    }

    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CHILD, () -> TCChildCreeperModel.createBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(SABER, TCSaberModel::createLayer);
    }
}
