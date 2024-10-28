package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCArrowCreeper extends AbstractTCCreeper {

    public TCArrowCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ARROW;
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
        for (int i = 0; i < 750 * (this.isPowered() ? 2 : 1); i++) {
            double x = this.getRandom().nextDouble() - 0.5;
            double y = this.getRandom().nextDouble() - 0.5;
            double z = this.getRandom().nextDouble() - 0.5;

            Arrow arrow = new Arrow(this.level(), this.getX() + x, this.getY() + y, this.getZ() + z, Items.ARROW.getDefaultInstance(), null);
            arrow.setBaseDamage(this.level().getDifficulty().getId() * 5);
            arrow.setCritArrow(true);
            if (this.isPowered()) {
                arrow.igniteForSeconds(100);
            }
            arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            arrow.setDeltaMovement(x * (0.5 + this.getRandom().nextGaussian() * 3), -7, z * (0.5 + this.getRandom().nextGaussian() * 3));
            arrow.move(MoverType.SELF, arrow.getDeltaMovement());
            arrow.setPos(this.getX() + x, this.getY() + y, this.getZ() + z);
            if (!this.level().isClientSide) {
                this.level().addFreshEntity(arrow);
            }
        }
    }

    public static class TCArrowCreeperContext implements TCCreeperContext<TCArrowCreeper> {
        private static final String NAME = "arrowcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCArrowCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "やたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Hundreds of thousand of arrows, to destroy you.";
        }

        @Override
        public String getJaJPDesc() {
            return "幾千もの矢が、天から地へと降り注ぐ。";
        }

        @Override
        public String getEnUSName() {
            return "Arrow Creeper";
        }

        @Override
        public String getJaJPName() {
            return "矢匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xf5d0a9;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.ARROW;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1, 64);
        }
    }
}
