package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCBatCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public class TCBigBatCreeper extends TCBatCreeper {
    public TCBigBatCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 18.0);
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide) {
            for (int i = 0; i <= (this.isPowered() ? 5 : 3); i++) {
                TCBatCreeper batCreeper = (TCBatCreeper) TCEntityCore.BAT.entityType().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                batCreeper.copyPosition(this);
                this.level().addFreshEntity(batCreeper);
                AbstractTCCreeper rushCreeper = (AbstractTCCreeper) TCEntityCore.RUSH.entityType().create(this.level(), EntitySpawnReason.MOB_SUMMONED);
                rushCreeper.copyPosition(batCreeper);
                this.level().addFreshEntity(rushCreeper);
                rushCreeper.startRiding(batCreeper, true);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isPowered() && !this.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 3));
        }
        if (this.level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(1), this.getRandomY(1), this.getRandomZ(1), 0, 0, 0);
            }
        }
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BIGBAT;
    }

    public static class TCBigBatCreeperContext implements TCCreeperContext<TCBigBatCreeper> {
        private static final String NAME = "bigbatcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBigBatCreeper::new, MobCategory.MONSTER).sized(1.0F, 1.5F).eyeHeight(0.8F).clientTrackingRange(5).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おおこうもりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Bloody big bat, you will face to the nightmare of vampire.";
        }

        @Override
        public String getJaJPDesc() {
            return "血に染まりし大蝙蝠、上がる黒煙は何を意味する。";
        }

        @Override
        public String getEnUSName() {
            return "Big Bat Creeper";
        }

        @Override
        public String getJaJPName() {
            return "大蝙蝠匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x440000;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCBatCreeper::checkBatSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return TCBigBatCreeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCBatCreeper>) type, TCBatCreeperRenderer::new);
        }

        @Override
        public float getSizeFactor() {
            return 2f;
        }
    }
}
