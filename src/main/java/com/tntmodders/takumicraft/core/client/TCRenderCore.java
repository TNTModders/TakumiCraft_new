package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.model.TCChildCreeperModel;
import com.tntmodders.takumicraft.client.renderer.block.*;
import com.tntmodders.takumicraft.client.renderer.block.model.TCSaberModel;
import com.tntmodders.takumicraft.client.renderer.block.model.TCShieldModel;
import com.tntmodders.takumicraft.client.renderer.entity.TCAmethystBombRenderer;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperFrameRenderer;
import com.tntmodders.takumicraft.client.renderer.entity.TCKingBlockRenderer;
import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperFrame;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperGlowingFrame;
import com.tntmodders.takumicraft.entity.misc.TCKingBlock;
import com.tntmodders.takumicraft.entity.misc.TCKingStorm;
import com.tntmodders.takumicraft.entity.projectile.*;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

@OnlyIn(Dist.CLIENT)
public class TCRenderCore {
    public static final ModelLayerLocation CHILD = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, TCEntityCore.CHILD.getRegistryName()), TCEntityCore.CHILD.getRegistryName());
    public static final ModelLayerLocation FALLING_SLIME = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, TCEntityCore.FALLING_SLIME.getRegistryName()), TCEntityCore.FALLING_SLIME.getRegistryName());
    public static final ModelLayerLocation SABER = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "typesword_normal"), "typesword_normal");
    public static final ModelLayerLocation SHIELD = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creepershield"), "creepershield");
    public static final ModelLayerLocation SUPERBED_HEAD = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "super_creeperbed_head"), "super_creeperbed_head");
    public static final ModelLayerLocation SUPERBED_FOOT = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "super_creeperbed_foot"), "super_creeperbed_foot");
    public static final ModelLayerLocation ACID = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "acidblock"), "acidblock");
    public static final ModelLayerLocation FRAME = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperframe"), "creeperframe");
    public static final ModelLayerLocation PROTECTOR = new ModelLayerLocation(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "protector"), "protector");

    public static void registerEntityRender(EntityRenderersEvent.RegisterRenderers event) {
        TCLoggingUtils.startRegistry("EntityRenderer");
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> context.registerRenderer(event, context.entityType()));
        additionalEntityRender(event);
        TCLoggingUtils.completeRegistry("EntityRenderer");

        TCLoggingUtils.startRegistry("BlockEntityRenderer");
        blockEntityRender(event);
        TCLoggingUtils.completeRegistry("BlockEntityRenderer");
    }

    private static void additionalEntityRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TCCreeperArrow.ARROW, p_174010_ -> new ArrowRenderer<>(p_174010_) {

            @Override
            public ArrowRenderState createRenderState() {
                return new ArrowRenderState();
            }

            @Override
            protected ResourceLocation getTextureLocation(ArrowRenderState p_364393_) {
                return ResourceLocation.tryBuild(TakumiCraftCore.MODID, "texutres/entity/projectile/creeperarrow.png");
            }
        });
        event.registerEntityRenderer(TCAmethystBomb.AMETHYST_BOMB, TCAmethystBombRenderer::new);
        event.registerEntityRenderer(TCBirdBomb.BIRD_BOMB, ThrownItemRenderer::new);
        event.registerEntityRenderer(TCKingStorm.KING_STORM, p_174010_ -> new EntityRenderer<>(p_174010_) {
            @Override
            public EntityRenderState createRenderState() {
                return new EntityRenderState();
            }
        });
        event.registerEntityRenderer(TCKingBlock.KING_BLOCK, TCKingBlockRenderer::new);
        event.registerEntityRenderer(TCCreeperGrenade.GRENADE, ThrownItemRenderer::new);
        event.registerEntityRenderer(TCCreeperFrame.ITEM_FRAME, TCCreeperFrameRenderer::new);
        event.registerEntityRenderer(TCCreeperGlowingFrame.GLOWING_FRAME, TCCreeperFrameRenderer::new);
        //event.registerEntityRenderer(TCBreezeCreeperWindCharge.BREEZE_WIND_CHARGE, WindChargeRenderer::new);
        event.registerEntityRenderer(TCLlamaCreeperSpit.LLAMA_SPIT, LlamaSpitRenderer::new);
    }

    private static void blockEntityRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TCBlockEntityCore.CREEPER_BED, TCCreeperBedRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.ACID, TCAcidBlockRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.MONSTER_BOMB, TCMonsterBombBlockRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.CHEST, TCCreeperChestRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.CAMPFIRE, TCCreeperCampFireRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.SIGN, TCCreeperSignRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.HANGING_SIGN, TCCreeperHangingSignRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.SHULKER, TCCreeperShulkerBoxRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.SUPER_BLOCK, TCSuperBlockRenderer::new);
        event.registerBlockEntityRenderer(TCBlockEntityCore.PROTECTOR, TCCreeperProtectorBlockRenderer::new);
    }

    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CHILD, () -> TCChildCreeperModel.createBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(FALLING_SLIME, () -> CreeperModel.createBodyLayer(new CubeDeformation(1.0f)));
        event.registerLayerDefinition(SABER, TCSaberModel::createLayer);
        event.registerLayerDefinition(SHIELD, TCShieldModel::createLayer);
        event.registerLayerDefinition(SUPERBED_HEAD, TCCreeperBedRenderer::createSuperHeadLayer);
        event.registerLayerDefinition(SUPERBED_FOOT, TCCreeperBedRenderer::createSuperFootLayer);
        event.registerLayerDefinition(ACID, TCAcidBlockRenderer::createLayer);
        event.registerLayerDefinition(FRAME, TCCreeperFrameRenderer::createLayer);
        event.registerLayerDefinition(PROTECTOR, TCCreeperProtectorBlockRenderer::createLayer);
    }
}
