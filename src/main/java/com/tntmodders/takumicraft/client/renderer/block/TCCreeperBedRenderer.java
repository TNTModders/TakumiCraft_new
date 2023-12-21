package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperBedBlock;
import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import java.util.*;

public class TCCreeperBedRenderer implements BlockEntityRenderer<TCCreeperBedBlockEntity> {
    public static final Material[] BED_TEXTURES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(p_110766_ -> new Material(Sheets.BED_SHEET, new ResourceLocation(TakumiCraftCore.MODID, "entity/bed/" + p_110766_.getName()))).toArray(Material[]::new);

    public static final Material SUPER_TEXTURE = new Material(Sheets.BED_SHEET, new ResourceLocation(TakumiCraftCore.MODID, "entity/bed/super"));
    public static final Material OVERLAY_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(TakumiCraftCore.MODID, "block/creeperbomb"));

    public static final ResourceLocation POWERED_TEXTURE = new ResourceLocation(TakumiCraftCore.MODID, "textures/block/super_block.png");

    private final ModelPart headRoot;
    private final ModelPart footRoot;
    private final ModelPart superHeadRoot;
    private final ModelPart superFootRoot;

    public TCCreeperBedRenderer(BlockEntityRendererProvider.Context p_173540_) {
        this.headRoot = p_173540_.bakeLayer(ModelLayers.BED_HEAD);
        this.footRoot = p_173540_.bakeLayer(ModelLayers.BED_FOOT);

        this.superHeadRoot = p_173540_.bakeLayer(TCRenderCore.SUPERBED_HEAD);
        this.superFootRoot = p_173540_.bakeLayer(TCRenderCore.SUPERBED_FOOT);
    }

    public static LayerDefinition createSuperHeadLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("super_up", CubeListBuilder.create().texOffs(0, 8).addBox(0F, 8F, -0.002F, 16.0F, 8.0F, 0.001F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("super_east", CubeListBuilder.create().texOffs(8, -8).addBox(-16.002F, 0F, 8F, 0.001F, 4F, 8F), PartPose.rotation((float) Math.PI / -2f, (float) Math.PI, 0));
        partdefinition.addOrReplaceChild("super_west", CubeListBuilder.create().texOffs(8, -8).addBox(-0.002F, 0F, -16F, 0.001F, 4F, 8F), PartPose.rotation((float) Math.PI / 2f, 0, 0));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    public static LayerDefinition createSuperFootLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        //top
        partdefinition.addOrReplaceChild("super_up", CubeListBuilder.create().texOffs(0, 0).addBox(0F, 0F, -0.002F, 16.0F, 16.0F, 0.001F), PartPose.ZERO);
        //left
        partdefinition.addOrReplaceChild("super_east", CubeListBuilder.create().texOffs(0, -16).addBox(-16.002F, 0F, -0F, 0.001F, 4F, 16F), PartPose.rotation((float) Math.PI / -2f, (float) Math.PI, 0));
        //right
        partdefinition.addOrReplaceChild("super_west", CubeListBuilder.create().texOffs(0, -16).addBox(-0.002F, 0F, -16F, 0.001F, 4F, 16F), PartPose.rotation((float) Math.PI / 2f, 0, 0));
        //foot
        partdefinition.addOrReplaceChild("super_south", CubeListBuilder.create().texOffs(-4, 0).addBox(0F, -16.002F, -4F, 16F, 0.001F, 4F), PartPose.rotation((float) Math.PI, 0, 0));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void render(TCCreeperBedBlockEntity blockEntity, float p_112206_, PoseStack poseStack, MultiBufferSource bufferSource, int p_112209_, int p_112210_) {
        if (blockEntity.isSuper()) {
            Material material = SUPER_TEXTURE;
            Level level = blockEntity.getLevel();
            if (level != null) {
                BlockState blockstate = blockEntity.getBlockState();
                DoubleBlockCombiner.NeighborCombineResult<? extends TCCreeperBedBlockEntity> neighborcombineresult = DoubleBlockCombiner.combineWithNeigbour(TCBlockEntityCore.CREEPER_BED, TCCreeperBedBlock::getBlockType, TCCreeperBedBlock::getConnectedDirection, ChestBlock.FACING, blockstate, level, blockEntity.getBlockPos(), (p_112202_, p_112203_) -> false);
                int i = neighborcombineresult.apply(new BrightnessCombiner<>()).get(p_112209_);
                this.renderPiece(poseStack, bufferSource, blockstate.getValue(TCCreeperBedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot, blockstate.getValue(TCCreeperBedBlock.FACING), material, i, p_112210_, false);

                this.renderSuperPiece(blockEntity, poseStack, bufferSource, blockstate.getValue(TCCreeperBedBlock.PART) == BedPart.HEAD ? this.superHeadRoot : this.superFootRoot, blockstate.getValue(TCCreeperBedBlock.FACING), i, p_112210_, false);
            } else {
                this.renderPiece(poseStack, bufferSource, this.headRoot, Direction.SOUTH, material, p_112209_, p_112210_, false);
                this.renderPiece(poseStack, bufferSource, this.footRoot, Direction.SOUTH, material, p_112209_, p_112210_, true);
            }
        } else {
            Level level = blockEntity.getLevel();
            Material material = BED_TEXTURES[blockEntity.getColor().getId()];
            if (level != null) {
                BlockState blockstate = blockEntity.getBlockState();
                DoubleBlockCombiner.NeighborCombineResult<? extends TCCreeperBedBlockEntity> neighborcombineresult = DoubleBlockCombiner.combineWithNeigbour(TCBlockEntityCore.CREEPER_BED, TCCreeperBedBlock::getBlockType, TCCreeperBedBlock::getConnectedDirection, ChestBlock.FACING, blockstate, level, blockEntity.getBlockPos(), (p_112202_, p_112203_) -> false);
                int i = neighborcombineresult.apply(new BrightnessCombiner<>()).get(p_112209_);
                this.renderPiece(poseStack, bufferSource, blockstate.getValue(TCCreeperBedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot, blockstate.getValue(TCCreeperBedBlock.FACING), material, i, p_112210_, false);
            } else {
                this.renderPiece(poseStack, bufferSource, this.headRoot, Direction.SOUTH, material, p_112209_, p_112210_, false);
                this.renderPiece(poseStack, bufferSource, this.footRoot, Direction.SOUTH, material, p_112209_, p_112210_, true);
            }
        }
    }

    private void renderPiece(PoseStack p_173542_, MultiBufferSource p_173543_, ModelPart p_173544_, Direction p_173545_, Material p_173546_, int p_173547_, int p_173548_, boolean p_173549_) {
        p_173542_.pushPose();
        p_173542_.translate(0.0F, 0.5625F, p_173549_ ? -1.0F : 0.0F);
        p_173542_.mulPose(Axis.XP.rotationDegrees(90.0F));
        p_173542_.translate(0.5F, 0.5F, 0.5F);
        p_173542_.mulPose(Axis.ZP.rotationDegrees(180.0F + p_173545_.toYRot()));
        p_173542_.translate(-0.5F, -0.5F, -0.5F);
        VertexConsumer vertexconsumer = p_173546_.buffer(p_173543_, RenderType::entitySolid);
        p_173544_.render(p_173542_, vertexconsumer, p_173547_, p_173548_);
        p_173542_.popPose();
    }

    private void renderSuperPiece(TCCreeperBedBlockEntity entity, PoseStack poseStack, MultiBufferSource source, ModelPart modelPart, Direction direction, int p_173547_, int p_173548_, boolean p_173549_) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5625F, p_173549_ ? -1.0F : 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F + direction.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        if (entity.getTexture() == null) {
            float f = Minecraft.getInstance().player.tickCount * 0.025f;
            VertexConsumer vertexconsumer = source.getBuffer(RenderType.energySwirl(POWERED_TEXTURE, f % 1.0F, f % 1.0F));

            modelPart.render(poseStack, vertexconsumer, p_173547_, p_173548_);
        } else {
            BlockState blockState = entity.getBlock().defaultBlockState();
            BlockColors colors = BlockColors.createDefault();
            int i = colors.getColor(blockState, null, null, 0);
            float f0 = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
            Map<Direction, TextureAtlasSprite> textureMap = new HashMap<>();
            Direction.stream().forEach(side -> {
                if (side != Direction.DOWN && side != Direction.NORTH) {
                    List<BakedQuad> list = model.getQuads(blockState, side, entity.getLevel().getRandom());
                    if (!list.isEmpty()) {
                        textureMap.put(side, list.get(0).getSprite());
                    }
                }
            });
            if (textureMap.size() == 4) {
                textureMap.forEach((side, texture) -> {
                    Material material = new Material(texture.atlasLocation(), texture.contents().name());
                    if (modelPart != this.superHeadRoot || side != Direction.SOUTH) {
                        modelPart.getChild("super_" + side.getName()).render(poseStack, material.buffer(source, RenderType::entityTranslucentCull), p_173547_, p_173548_, f0, f1, f2, 1f);
                    }
                });
            } else {
                float f = Minecraft.getInstance().player.tickCount * 0.025f;
                VertexConsumer vertexconsumer = source.getBuffer(RenderType.energySwirl(POWERED_TEXTURE, f % 1.0F, f % 1.0F));
                modelPart.render(poseStack, vertexconsumer, p_173547_, p_173548_);
            }
        }
        poseStack.popPose();
    }
}
