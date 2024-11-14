package com.tntmodders.takumicraft.client.renderer.block.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class TCSaberModel extends Model {

    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart blade;

    public TCSaberModel(ModelPart part) {
        super(part, RenderType::entitySolid);
        this.root = part;
        this.base = part.getChild("base");
        this.blade = part.getChild("blade");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.0F, 10.0F, -2.0F, 4.0F, 14.0F, 4.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-2.0F, -16.0F, -2.0F, 4.0F, 26.0F, 4.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void renderToBufferBase(PoseStack p_103703_, VertexConsumer p_103704_, int p_103705_, int p_103706_, float p_103707_, float p_103708_, float p_103709_, float p_103710_) {
        this.base.render(p_103703_, p_103704_, p_103705_, p_103706_);
    }

    public void renderToBufferBlade(PoseStack p_103703_, VertexConsumer p_103704_, int p_103705_, int p_103706_, float p_103707_, float p_103708_, float p_103709_, float p_103710_) {
        this.blade.render(p_103703_, p_103704_, p_103705_, p_103706_);
    }

    public void renderToBufferAll(PoseStack p_103703_, VertexConsumer p_103704_, int p_103705_, int p_103706_, int i) {
        this.base.render(p_103703_, p_103704_, p_103705_, p_103706_);
        this.blade.render(p_103703_, p_103704_, p_103705_, p_103706_);
    }
}
