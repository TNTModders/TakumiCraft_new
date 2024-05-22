package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCChildCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.List;

public class TCChildCreeper extends AbstractTCCreeper {
    public TCChildCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.CHILD;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<BlockPos> posList = new ArrayList<>();
        event.getAffectedBlocks().forEach(blockPos -> {
            float f = this.level().getBlockState(blockPos).getExplosionResistance(this.level(), blockPos, event.getExplosion());
            if (f > 0.2 && event.getExplosion().radius > 1) {
                this.level().destroyBlock(blockPos, true, this);
                TCExplosionUtils.createExplosion(this.level(), this, blockPos, Math.max(0, event.getExplosion().radius - 0.2f - 2 / f));
                posList.add(blockPos);

            }
        });
        if (!posList.isEmpty()) {
            event.getAffectedBlocks().removeAll(posList);
        }
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState, float f) {
        return blockState.getDestroySpeed(level, pos) < 0 ? super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState, f) : 1f;
    }

    public static class TCChildCreeperContext implements TCCreeperContext<TCChildCreeper> {
        private static final String NAME = "childcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCChildCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おやこたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Carring the love in their bodies, and not afraid to explode in order to protect their own children.";
        }

        @Override
        public String getJaJPDesc() {
            return "おやこあいをその身に宿して、我が子を守るためなら爆発さえ恐れないという。";
        }

        @Override
        public String getEnUSName() {
            return "Child Creeper";
        }

        @Override
        public String getJaJPName() {
            return "親子匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xccaaaa;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00aa00;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes();
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCChildCreeperRenderer::new);
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
