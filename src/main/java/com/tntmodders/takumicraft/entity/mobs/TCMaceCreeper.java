package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCMaceCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import javax.annotation.Nullable;

public class TCMaceCreeper extends TCZombieCreeper {

    public TCMaceCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 6;
    }


    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.MACE;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32372_, DifficultyInstance p_32373_, MobSpawnType p_32374_, @Nullable SpawnGroupData p_32375_) {
        p_32375_ = super.finalizeSpawn(p_32372_, p_32373_, p_32374_, p_32375_);
        if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.HEAVY_CORE));
            this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 2.0F;
        }

        return p_32375_;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance p_32348_) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(TCItemCore.CREEPER_MACE));
    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack p_32364_, ItemStack p_32365_) {
        if (p_32365_.is(Items.HEAVY_CORE)) {
            return false;
        } else if (p_32365_.is(TCItemCore.CREEPER_MACE)) {
            if (p_32364_.is(TCItemCore.CREEPER_MACE)) {
                return p_32364_.getDamageValue() < p_32365_.getDamageValue();
            } else {
                return false;
            }
        } else {
            return p_32364_.is(TCItemCore.CREEPER_MACE) || super.canReplaceCurrentItem(p_32364_, p_32365_);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.swell > this.maxSwell - 5 && this.swell < this.maxSwell && this.onGround()) {
            TCExplosionUtils.createExplosion(this.level(), this, this.getOnPos(), 0f);
            this.move(MoverType.SELF, new Vec3(0, 40, 0));
            this.setDeltaMovement(0, -4, 0);
            this.ignite();
        }
    }

    @Override
    public void explodeCreeper() {
        if (this.onGround()) {
            super.explodeCreeper();
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 2));
            }
        });
    }

    @Override
    public boolean causeFallDamage(float p_149687_, float p_149688_, DamageSource p_149689_) {
        return false;
    }


    public static class TCMaceCreeperContext implements TCCreeperContext<TCMaceCreeper> {
        private static final String NAME = "macecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCMaceCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "なぐりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Bloody calnic's shattering blow smash everything, even the souls.";
        }

        @Override
        public String getJaJPDesc() {
            return "血濡れ司祭の地を砕く一撃は、魂すらも全てを巻き込み爆砕せしめん。";
        }

        @Override
        public String getEnUSName() {
            return "Mace Creeper";
        }

        @Override
        public String getJaJPName() {
            return "殴匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x339933;
        }

        @Override
        public int getSecondaryColor() {
            return 0x440000;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Monster.createMonsterAttributes()
                    .add(Attributes.FOLLOW_RANGE, 35.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.23F)
                    .add(Attributes.ATTACK_DAMAGE, 3.0D)
                    .add(Attributes.ARMOR, 2.0D)
                    .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCMaceCreeper>) type, TCMaceCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
