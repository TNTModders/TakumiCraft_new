package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCCopperCreeper extends AbstractTCCreeper {

    public TCCopperCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 4;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.COPPER;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> {
            if (this.level().getBlockState(pos).getBlock() instanceof WeatheringCopper copper) {
                BlockState previous = this.level().getBlockState(pos);
                BlockState state = WeatheringCopper.getFirst(previous);
                if (!previous.getProperties().isEmpty()) {
                    for (Property property : previous.getProperties()) {
                        state.setValue(property, previous.getValue(property));
                    }
                }
                this.level().setBlock(pos, state, 3);
            } else if (!this.level().getBlockState(pos).isAir()) {
                this.level().setBlock(pos, Blocks.WAXED_COPPER_BLOCK.defaultBlockState(), 3);
            }
        });
        event.getExplosion().clearToBlow();
    }

    public static class TCCopperCreeperContext implements TCCreeperContext<TCCopperCreeper> {
        private static final String NAME = "coppercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCCopperCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "どうしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with copper metalic luster. Too unbated, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "輝く体躯と錆知らぬ技、銅の匠は衰えない。";
        }

        @Override
        public String getEnUSName() {
            return "Copper Creeper";
        }

        @Override
        public String getJaJPName() {
            return "銅匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaa3300;
        }

        @Override
        public int getSecondaryColor() {
            return 12303206;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes();
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
