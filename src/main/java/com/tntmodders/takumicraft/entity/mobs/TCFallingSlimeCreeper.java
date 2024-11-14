package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCFallingSlimeCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCFallingSlimeCreeper extends AbstractTCCreeper {

    public TCFallingSlimeCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.FALLING_SLIME;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().clear();
        event.getAffectedBlocks().clear();
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, this.getJumpPower() * 100, vec3.z);
        if (this.isSprinting()) {
            float f = this.getYRot() * ((float) Math.PI / 180F);
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f) * 0.2F, 0.0D, Mth.cos(f) * 0.2F));
        }

        this.hasImpulse = true;
        this.move(MoverType.SELF, this.getDeltaMovement());
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
        for (int t = 0; t < 100 * (this.isPowered() ? 2 : 1); t++) {
            double x = (this.getRandom().nextDouble() - 0.5) * 30;
            double y = (this.getRandom().nextDouble() - 0.5) * 30;
            double z = (this.getRandom().nextDouble() - 0.5) * 30;

            TCSlimeCreeper slime = (TCSlimeCreeper) TCEntityCore.SLIME.entityType().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
            int i = this.getRandom().nextInt(3);
            if (i < 2 && this.getRandom().nextFloat() < 0.5F * this.level().getDifficulty().getId()) {
                ++i;
            }
            int j = 1 << i;
            slime.setSize(j, true);
            slime.ignite();
            slime.setDeltaMovement(x * (0.5 + this.getRandom().nextGaussian() * 3), -15, z * (0.5 + this.getRandom().nextGaussian() * 3));
            slime.move(MoverType.SELF, slime.getDeltaMovement());
            slime.setPos(this.getX() + x, this.getY() + y, this.getZ() + z);
            if (!this.level().isClientSide) {
                this.level().addFreshEntity(slime);
            }
        }
    }

    public static class TCFallingSlimeCreeperContext implements TCCreeperContext<TCFallingSlimeCreeper> {
        private static final String NAME = "fallingslimecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCFallingSlimeCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "あすらたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Not a few, but lots of falling slime.";
        }

        @Override
        public String getJaJPDesc() {
            return "阿修羅の修行は落ち来る脅威、虚空を埋めよ、全てを壊せ。";
        }

        @Override
        public String getEnUSName() {
            return "Falling Slime Creeper";
        }

        @Override
        public String getJaJPName() {
            return "阿修羅匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x004400;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaaffaa;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.SLIME_BLOCK;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCFallingSlimeCreeper>) type, TCFallingSlimeCreeperRenderer::new);
        }
    }
}
