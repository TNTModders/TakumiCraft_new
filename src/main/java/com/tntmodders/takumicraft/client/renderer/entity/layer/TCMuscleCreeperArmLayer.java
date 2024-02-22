package com.tntmodders.takumicraft.client.renderer.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.entity.mobs.TCMuscleCreeper;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TCMuscleCreeperArmLayer<T extends TCMuscleCreeper, M extends EntityModel<T>> extends ElytraLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/musclecreeperarm.png");
    private final ElytraModel<T> elytraModel;

    public TCMuscleCreeperArmLayer(RenderLayerParent p_174493_, EntityModelSet p_174494_) {
        super(p_174493_, p_174494_);
        this.elytraModel = new MuscleCreeperArmModel(p_174494_.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack p_116951_, MultiBufferSource p_116952_, int p_116953_, T p_116954_, float p_116955_, float p_116956_, float p_116957_, float p_116958_, float p_116959_, float p_116960_) {
        ItemStack itemstack = p_116954_.getItemBySlot(EquipmentSlot.CHEST);
        if (shouldRender(itemstack, p_116954_)) {
            ResourceLocation resourcelocation = getElytraTexture(itemstack, p_116954_);

            p_116951_.pushPose();
            p_116951_.translate(0.0F, 0.0F, 0.125F);
            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(p_116954_, p_116955_, p_116956_, p_116958_, p_116959_, p_116960_);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_116952_, RenderType.armorCutoutNoCull(resourcelocation), false, itemstack.hasFoil());
            this.elytraModel.renderToBuffer(p_116951_, vertexconsumer, p_116953_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            p_116951_.popPose();
        }
    }

    /**
     * Determines if the ElytraLayer should render.
     * ItemStack and Entity are provided for modder convenience,
     * For example, using the same ElytraLayer for multiple custom Elytra.
     *
     * @param stack  The Elytra ItemStack
     * @param entity The entity being rendered.
     * @return If the ElytraLayer should render.
     */
    @Override
    public boolean shouldRender(ItemStack stack, T entity) {
        return true;
    }

    /**
     * Gets the texture to use with this ElytraLayer.
     * This assumes the vanilla Elytra model.
     *
     * @param stack  The Elytra ItemStack.
     * @param entity The entity being rendered.
     * @return The texture.
     */
    @Override
    public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        return WINGS_LOCATION;
    }

    protected static class MuscleCreeperArmModel extends ElytraModel {

        private final ModelPart rightWing;
        private final ModelPart leftWing;

        public MuscleCreeperArmModel(ModelPart p_170538_) {
            super(p_170538_);
            this.leftWing = p_170538_.getChild("left_wing");
            this.rightWing = p_170538_.getChild("right_wing");
        }

        @Override
        public void setupAnim(LivingEntity p_102544_, float p_102545_, float p_102546_, float p_102547_, float p_102548_, float p_102549_) {
            if (p_102544_.tickCount / 10 % 2 == 0) {
                this.leftWing.y = 8f;
                this.leftWing.x = 14f;
                this.leftWing.z = 0f;
                this.leftWing.xRot = (float) Math.PI;
            } else {
                this.leftWing.y = 4f;
                this.leftWing.x = 12f;
                this.leftWing.z = -6f;
                this.leftWing.xRot = 0;
            }
            this.rightWing.y = this.leftWing.y;
            this.rightWing.x = -this.leftWing.x;
            this.rightWing.z = this.leftWing.z;
            this.rightWing.xRot = this.leftWing.xRot;
        }
    }
}
