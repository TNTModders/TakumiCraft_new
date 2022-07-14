package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.List;

public class TCLightCreeper extends AbstractTCCreeper {

    public TCLightCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 3;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.LIGHT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive() && !this.level.isClientSide) {
            this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 400, 0));
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 400, 0));
            }
        });
        List<BlockPos> posList = new ArrayList<>();
        event.getAffectedBlocks().forEach(blockPos -> {
            int i = this.level.getBlockState(blockPos).getLightEmission(this.level, blockPos);
            if (i > 0.5 && event.getExplosion().radius > 1) {
                this.level.destroyBlock(blockPos, true, this);
                TCExplosionUtils.createExplosion(this.level, this, blockPos, event.getExplosion().radius - 0.2f);
                posList.add(blockPos);

            }
        });
        if (!posList.isEmpty()) {
            event.getAffectedBlocks().removeAll(posList);
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    public static class TCLightCreeperContext implements TCCreeperContext<TCLightCreeper> {
        private static final String NAME = "lightcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCLightCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ひかりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Destroy light, it is enough that the light is only one, this creeper.";
        }

        @Override
        public String getJaJPDesc() {
            return "光からの襲撃で、明かりは仄暗く消えてしまうだろう。";
        }

        @Override
        public String getEnUSName() {
            return "Light Creeper";
        }

        @Override
        public String getJaJPName() {
            return "光匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 39168;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaaff11;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer(((EntityType<Creeper>) type), context -> new TCCreeperRenderer(context, true));
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
