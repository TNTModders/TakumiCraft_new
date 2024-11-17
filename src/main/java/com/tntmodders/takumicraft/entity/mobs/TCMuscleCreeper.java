package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCMuscleCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class TCMuscleCreeper extends AbstractTCCreeper {

    public TCMuscleCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 10;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.MUSCLE;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 10));
                if (entity instanceof Creeper creeper) {
                    creeper.explosionRadius = 10;
                    creeper.getEntityData().set(ObfuscationReflectionHelper.getPrivateValue(Creeper.class, creeper, "DATA_IS_POWERED"), true);
                }
            }
        });
    }

    public static class TCMuscleCreeperContext implements TCCreeperContext<TCMuscleCreeper> {
        private static final String NAME = "musclecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCMuscleCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "まっするたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Happy, happy, mighty masucle!";
        }

        @Override
        public String getJaJPDesc() {
            return "ハッピー、ハッピー、筋肉で埋め尽くして。";
        }

        @Override
        public String getEnUSName() {
            return "Muscle Creeper";
        }

        @Override
        public String getJaJPName() {
            return "マッスル匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xffcd9b;
        }

        @Override
        public int getPrimaryColor() {
            return 0xeca25b;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<AbstractTCCreeper>) type, TCMuscleCreeperRenderer::new);
        }
    }
}
