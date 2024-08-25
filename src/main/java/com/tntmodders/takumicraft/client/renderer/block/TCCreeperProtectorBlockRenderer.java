package com.tntmodders.takumicraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.TCCreeperProtectorBlock;
import com.tntmodders.takumicraft.block.entity.TCCreeperProtectorBlockEntity;
import com.tntmodders.takumicraft.core.client.TCRenderCore;
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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class TCCreeperProtectorBlockRenderer<T extends TCCreeperProtectorBlockEntity> implements BlockEntityRenderer<T> {
    private static final ResourceLocation TEXTURE = ResourceLocation.tryBuild(TakumiCraftCore.MODID, "textures/entity/creeperprotector.png");
    private final ModelPart modelPart;

    public TCCreeperProtectorBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.modelPart = context.bakeLayer(TCRenderCore.PROTECTOR);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("down", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0.02f, 0, 16, 0, 16), PartPose.ZERO);
        partdefinition.addOrReplaceChild("up", CubeListBuilder.create().texOffs(0, 0).addBox(0, 15.98f, 0, 16, 0, 16), PartPose.ZERO);
        partdefinition.addOrReplaceChild("north", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0.02f, 16, 16, 0), PartPose.ZERO);
        partdefinition.addOrReplaceChild("south", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 15.98f, 16, 16, 0), PartPose.ZERO);
        partdefinition.addOrReplaceChild("west", CubeListBuilder.create().texOffs(0, 0).addBox(0.02f, 0, 0, 0, 16, 16), PartPose.ZERO);
        partdefinition.addOrReplaceChild("east", CubeListBuilder.create().texOffs(0, 0).addBox(15.98f, 0, 0, 0, 16, 16), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void render(T blockentity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int p_112311_, int p_112312_) {
        poseStack.pushPose();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE)).setColor(0.8f, 0.8f, 0.8f, 0.8f);
        BlockState state = blockentity.getBlockState();
        Direction.stream().forEach(direction -> {
            if (state.hasProperty(TCCreeperProtectorBlock.FACEING_PROPERTIES.get(direction))) {
                this.modelPart.getChild(direction.getName()).visible = state.getValue(TCCreeperProtectorBlock.FACEING_PROPERTIES.get(direction));
            }
        });
        this.modelPart.render(poseStack, consumer, p_112311_, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
