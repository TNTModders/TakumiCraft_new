package com.tntmodders.takumicraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperFrame;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperGlowingFrame;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class TCCreeperFrameRenderer<T extends TCCreeperFrame> extends ItemFrameRenderer<T> {
    public static final ResourceLocation CREEPER_FRAME_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/block/creeperframe.png");
    public static final ResourceLocation CREEPER_GLOW_FRAME_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/block/creeperframe_glowing.png");

    public static final ResourceLocation GLOW_ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("glow_item_frame");
    public static final ResourceLocation ITEM_FRAME_LOCATION = ResourceLocation.withDefaultNamespace("item_frame");
    public static final ResourceLocation TC_GLOW_ITEM_FRAME_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperframe_glowing");
    public static final ResourceLocation TC_ITEM_FRAME_LOCATION = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeperframe");
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder<Block, BlockState>(Blocks.AIR)
            .add(BooleanProperty.create("map"))
            .create(Block::defaultBlockState, BlockState::new);
    public static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = Map.of(ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, GLOW_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, TC_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION, TC_GLOW_ITEM_FRAME_LOCATION, ITEM_FRAME_FAKE_DEFINITION);

    public static final ModelResourceLocation GLOW_MAP_FRAME_LOCATION = new ModelResourceLocation(TC_GLOW_ITEM_FRAME_LOCATION, "map=true");
    public static final ModelResourceLocation GLOW_FRAME_LOCATION = new ModelResourceLocation(TC_GLOW_ITEM_FRAME_LOCATION, "map=false");
    public static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation(TC_ITEM_FRAME_LOCATION, "map=true");
    public static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation(TC_ITEM_FRAME_LOCATION, "map=false");

    private final ModelPart modelPart;
    private final ItemRenderer itemRenderer;
    private final MapRenderer mapRenderer;
    private final BlockRenderDispatcher blockRenderer;

    public TCCreeperFrameRenderer(EntityRendererProvider.Context p_174204_) {
        super(p_174204_);
        this.modelPart = p_174204_.bakeLayer(TCRenderCore.FRAME);
        this.itemRenderer = p_174204_.getItemRenderer();
        this.mapRenderer = p_174204_.getMapRenderer();
        this.blockRenderer = p_174204_.getBlockRenderDispatcher();
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -8, -0.6f, 16, 16, 0), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void render(ItemFrameRenderState p_361692_, PoseStack p_115061_, MultiBufferSource p_115062_, int p_115063_) {
        super.render(p_361692_, p_115061_, p_115062_, p_115063_);
        p_115061_.pushPose();
        Direction direction = p_361692_.direction;
        Vec3 vec3 = this.getRenderOffset(p_361692_);
        p_115061_.translate(-vec3.x(), -vec3.y(), -vec3.z());
        double d0 = 0.46875;
        p_115061_.translate((double) direction.getStepX() * 0.46875, (double) direction.getStepY() * 0.46875, (double) direction.getStepZ() * 0.46875);
        float f;
        float f1;
        if (direction.getAxis().isHorizontal()) {
            f = 0.0F;
            f1 = 180.0F - direction.toYRot();
        } else {
            f = (float) (-90 * direction.getAxisDirection().getStep());
            f1 = 180.0F;
        }

        p_115061_.mulPose(Axis.XP.rotationDegrees(f));
        p_115061_.mulPose(Axis.YP.rotationDegrees(f1));
        ItemStack itemstack = p_361692_.itemStack;
        if (!p_361692_.isInvisible) {
            ModelManager modelmanager = this.blockRenderer.getBlockModelShaper().getModelManager();
            ModelResourceLocation modelresourcelocation = this.getFrameModelResourceLoc(p_361692_.isGlowFrame, itemstack);
            p_115061_.pushPose();
            p_115061_.translate(-0.5F, -0.5F, -0.5F);
            this.blockRenderer
                    .getModelRenderer()
                    .renderModel(
                            p_115061_.last(),
                            p_115062_.getBuffer(RenderType.entitySolidZOffsetForward(TextureAtlas.LOCATION_BLOCKS)),
                            null,
                            modelmanager.getModel(modelresourcelocation),
                            1.0F,
                            1.0F,
                            1.0F,
                            p_115063_,
                            OverlayTexture.NO_OVERLAY
                    );
            p_115061_.popPose();
        }

        if (!itemstack.isEmpty()) {
            MapId mapid = p_361692_.mapId;
            if (p_361692_.isInvisible) {
                p_115061_.translate(0.0F, 0.0F, 0.5F);
            } else {
                p_115061_.translate(0.0F, 0.0F, 0.4375F);
            }

            int j = mapid != null ? p_361692_.rotation % 4 * 2 : p_361692_.rotation;
            p_115061_.mulPose(Axis.ZP.rotationDegrees((float) j * 360.0F / 8.0F));
            if (!net.minecraftforge.client.event.ForgeEventFactoryClient.onRenderItemInFrame(p_361692_, this, p_115061_, p_115062_, p_115063_)) {
                if (mapid != null) {
                    p_115061_.mulPose(Axis.ZP.rotationDegrees(180.0F));
                    float f2 = 0.0078125F;
                    p_115061_.scale(0.0078125F, 0.0078125F, 0.0078125F);
                    p_115061_.translate(-64.0F, -64.0F, 0.0F);
                    p_115061_.translate(0.0F, 0.0F, -1.0F);
                    int i = this.getLightVal(p_361692_.isGlowFrame, 15728850, p_115063_);
                    this.mapRenderer.render(p_361692_.mapRenderState, p_115061_, p_115062_, true, i);
                } else if (p_361692_.itemModel != null) {
                    int k = this.getLightVal(p_361692_.isGlowFrame, 15728880, p_115063_);
                    p_115061_.scale(0.5F, 0.5F, 0.5F);
                    this.itemRenderer.render(itemstack, ItemDisplayContext.FIXED, false, p_115061_, p_115062_, k, OverlayTexture.NO_OVERLAY, p_361692_.itemModel);
                }
            }
        }

        p_115061_.popPose();
    }

    private int getLightVal(boolean p_368253_, int p_174210_, int p_174211_) {
        return p_368253_ ? p_174210_ : p_174211_;
    }

    private ModelResourceLocation getFrameModelResourceLoc(boolean p_366996_, ItemStack p_174214_) {
        if (p_174214_.has(DataComponents.MAP_ID)) {
            return p_366996_ ? GLOW_MAP_FRAME_LOCATION : MAP_FRAME_LOCATION;
        } else {
            return p_366996_ ? GLOW_FRAME_LOCATION : FRAME_LOCATION;
        }
    }

    @Override
    public void extractRenderState(T p_369136_, ItemFrameRenderState p_364469_, float p_366511_) {
        super.extractRenderState(p_369136_, p_364469_, p_366511_);
        p_364469_.isGlowFrame = p_369136_.getType() == TCCreeperGlowingFrame.GLOWING_FRAME;
    }

    @Override
    protected int getBlockLightLevel(T p_174216_, BlockPos p_174217_) {
        return p_174216_.getType() == TCCreeperGlowingFrame.GLOWING_FRAME ? Math.max(7, super.getBlockLightLevel(p_174216_, p_174217_)) : super.getBlockLightLevel(p_174216_, p_174217_);
    }
}
