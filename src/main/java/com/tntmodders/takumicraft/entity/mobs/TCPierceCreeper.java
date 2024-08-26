package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TCPierceCreeper extends AbstractTCCreeper {

    public TCPierceCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 4;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.PIERCE;
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.setInvisible(true);
            this.setInvulnerable(true);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, Level.ExplosionInteraction.MOB);
            if (!this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || this.getOnPos().getY() < this.level().getMinBuildHeight() || this.isUnbreakableBlock(this.level().getBlockState(this.getOnPos())) || this.isUnbreakableBlock(this.level().getBlockState(this.getOnPos().below()))) {
                this.dead = true;
                this.triggerOnDeathMobEffects(Entity.RemovalReason.KILLED);
                this.discard();
            }
        }
    }

    private boolean isUnbreakableBlock(BlockState state) {
        return state.is(BlockTags.WITHER_IMMUNE) || state.is(TCBlockCore.ANTI_EXPLOSION);
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return !p_21016_.is(DamageTypeTags.IS_FALL) && super.hurt(p_21016_, p_21017_);
    }


    public static class TCPierceCreeperContext implements TCCreeperContext<TCPierceCreeper> {
        private static final String NAME = "piercecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCPierceCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "かんしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Armor, wall, and ground. All things it can pierce.";
        }

        @Override
        public String getJaJPDesc() {
            return "すべてを貫く、大地の底まで。いざ岩盤へ、去らば。";
        }

        @Override
        public String getEnUSName() {
            return "Pierce Creeper";
        }

        @Override
        public String getJaJPName() {
            return "貫匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x88ff00;
        }

        @Override
        public int getSecondaryColor() {
            return 0x001155;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
