package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperChestBlock;
import com.tntmodders.takumicraft.block.entity.TCCreeperChestBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.Calendar;

import static net.minecraft.client.renderer.Sheets.CHEST_SHEET;

public class TCCreeperChestRenderer<T extends TCCreeperChestBlockEntity> implements BlockEntityRenderer<T> {
    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;
    private final ModelPart doubleLeftLid;
    private final ModelPart doubleLeftBottom;
    private final ModelPart doubleLeftLock;
    private final ModelPart doubleRightLid;
    private final ModelPart doubleRightBottom;
    private final ModelPart doubleRightLock;
    private boolean xmasTextures;

    public static final Material CHEST_LOCATION = chestMaterial("normal");
    public static final Material CHEST_LOCATION_LEFT = chestMaterial("normal_left");
    public static final Material CHEST_LOCATION_RIGHT = chestMaterial("normal_right");

    public static final Material CHEST_XMAS_LOCATION = chestMaterial("christmas");
    public static final Material CHEST_XMAS_LOCATION_LEFT = chestMaterial("christmas_left");
    public static final Material CHEST_XMAS_LOCATION_RIGHT = chestMaterial("christmas_right");

    public TCCreeperChestRenderer(BlockEntityRendererProvider.Context p_173607_) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26) {
            this.xmasTextures = true;
        }

        ModelPart modelpart = p_173607_.bakeLayer(ModelLayers.CHEST);
        this.bottom = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
        ModelPart modelpart1 = p_173607_.bakeLayer(ModelLayers.DOUBLE_CHEST_LEFT);
        this.doubleLeftBottom = modelpart1.getChild("bottom");
        this.doubleLeftLid = modelpart1.getChild("lid");
        this.doubleLeftLock = modelpart1.getChild("lock");
        ModelPart modelpart2 = p_173607_.bakeLayer(ModelLayers.DOUBLE_CHEST_RIGHT);
        this.doubleRightBottom = modelpart2.getChild("bottom");
        this.doubleRightLid = modelpart2.getChild("lid");
        this.doubleRightLock = modelpart2.getChild("lock");
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild(
                "lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        partdefinition.addOrReplaceChild(
                "lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createDoubleBodyRightLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild(
                "lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        partdefinition.addOrReplaceChild(
                "lock", CubeListBuilder.create().texOffs(0, 0).addBox(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createDoubleBodyLeftLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild(
                "lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        partdefinition.addOrReplaceChild(
                "lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(T p_112363_, float p_112364_, PoseStack p_112365_, MultiBufferSource p_112366_, int p_112367_, int p_112368_) {
        Level level = p_112363_.getLevel();
        boolean flag = level != null;
        BlockState blockstate = flag ? p_112363_.getBlockState() : TCBlockCore.CREEPER_CHEST.defaultBlockState().setValue(TCCreeperChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.hasProperty(TCCreeperChestBlock.TYPE) ? blockstate.getValue(TCCreeperChestBlock.TYPE) : ChestType.SINGLE;
        if (blockstate.getBlock() instanceof TCCreeperChestBlock abstractchestblock) {
            boolean flag1 = chesttype != ChestType.SINGLE;
            p_112365_.pushPose();
            float f = blockstate.getValue(TCCreeperChestBlock.FACING).toYRot();
            p_112365_.translate(0.5F, 0.5F, 0.5F);
            p_112365_.mulPose(Axis.YP.rotationDegrees(-f));
            p_112365_.translate(-0.5F, -0.5F, -0.5F);
            DoubleBlockCombiner.NeighborCombineResult<? extends TCCreeperChestBlockEntity> neighborcombineresult;
            if (flag) {
                neighborcombineresult = abstractchestblock.combine(blockstate, level, p_112363_.getBlockPos(), true);
            } else {
                neighborcombineresult = DoubleBlockCombiner.Combiner::acceptNone;
            }

            float f1 = neighborcombineresult.apply(TCCreeperChestBlock.opennessCombiner(p_112363_)).get(p_112364_);
            f1 = 1.0F - f1;
            f1 = 1.0F - f1 * f1 * f1;
            int i = neighborcombineresult.apply(new BrightnessCombiner<>()).applyAsInt(p_112367_);
            Material material = this.getMaterial(p_112363_, chesttype);
            VertexConsumer vertexconsumer = material.buffer(p_112366_, RenderType::entityCutout);
            if (flag1) {
                if (chesttype == ChestType.LEFT) {
                    this.render(p_112365_, vertexconsumer, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, p_112368_);
                } else {
                    this.render(p_112365_, vertexconsumer, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, p_112368_);
                }
            } else {
                this.render(p_112365_, vertexconsumer, this.lid, this.lock, this.bottom, f1, i, p_112368_);
            }

            p_112365_.popPose();
        }
    }

    private void render(
            PoseStack p_112370_,
            VertexConsumer p_112371_,
            ModelPart p_112372_,
            ModelPart p_112373_,
            ModelPart p_112374_,
            float p_112375_,
            int p_112376_,
            int p_112377_
    ) {
        p_112372_.xRot = -(p_112375_ * (float) (Math.PI / 2));
        p_112373_.xRot = p_112372_.xRot;
        p_112372_.render(p_112370_, p_112371_, p_112376_, p_112377_);
        p_112373_.render(p_112370_, p_112371_, p_112376_, p_112377_);
        p_112374_.render(p_112370_, p_112371_, p_112376_, p_112377_);
    }

    protected Material getMaterial(BlockEntity blockEntity, ChestType chestType) {
        return chooseMaterial(blockEntity, chestType, this.xmasTextures);
    }

    private static Material chestMaterial(String p_110779_) {
        return new Material(CHEST_SHEET, new ResourceLocation(TakumiCraftCore.MODID, "entity/chest/" + p_110779_));
    }

    public static Material chooseMaterial(BlockEntity p_110768_, ChestType p_110769_, boolean p_110770_) {
        if (p_110770_) {
            return switch (p_110769_) {
                case LEFT -> CHEST_XMAS_LOCATION_LEFT;
                case RIGHT -> CHEST_XMAS_LOCATION_RIGHT;
                default -> CHEST_XMAS_LOCATION;
            };
        }
        return switch (p_110769_) {
            case LEFT -> CHEST_LOCATION_LEFT;
            case RIGHT -> CHEST_LOCATION_RIGHT;
            default -> CHEST_LOCATION;
        };
    }
}
