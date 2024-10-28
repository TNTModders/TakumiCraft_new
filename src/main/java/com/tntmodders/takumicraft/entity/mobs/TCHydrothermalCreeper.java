package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCBlockCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCHydrothermalCreeper extends AbstractTCBlockCreeper {

    public TCHydrothermalCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 8;
    }

    @Override
    public BlockState getBlock() {
        return Blocks.MAGMA_BLOCK.defaultBlockState();
    }

    public static boolean checkHTCreeperSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor levelAccessor, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        boolean flg1 = pos.getY() <= levelAccessor.getSeaLevel();
        boolean flg2 = isDarkEnoughToSpawn(levelAccessor, pos, random);
        boolean flg3 = levelAccessor.getBlockState(pos).is(Blocks.WATER);
        boolean flg4 = !levelAccessor.getBlockState(pos.below()).isAir();
        return flg1 && flg2 && flg3 && flg4;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.HT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            for (int i = 0; i < 5; i++) {
                double x = this.getRandomX(0.5);
                double y = this.getRandomY(0.5) + this.getRandom().nextDouble() * i;
                double z = this.getRandomZ(0.5);
                if (this.level().getFluidState(new BlockPos((int) x, (int) y, (int) z)).is(FluidTags.WATER)) {
                    this.level().addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0.1, 0);
                }
            }
        }
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter wolrdIn, BlockPos pos, BlockState blockStateIn, FluidState p_19996_, float p_19997_) {
        return blockStateIn.isAir() || wolrdIn.getFluidState(pos).is(FluidTags.WATER) ? 0.05f : super.getBlockExplosionResistance(explosion, wolrdIn, pos, blockStateIn, p_19996_, p_19997_);
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> {
            if (!this.level().getBlockState(pos).isAir() && !this.level().getFluidState(pos).is(FluidTags.WATER) && this.level().getFluidState(pos.above()).is(FluidTags.WATER)) {
                this.level().setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
            }
        });
        event.getAffectedBlocks().clear();
        event.getAffectedEntities().clear();
    }

    public static class TCHydrothermalCreeperContext implements TCCreeperContext<TCHydrothermalCreeper> {
        private static final String NAME = "hydrothermalcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCHydrothermalCreeper::new, MobCategory.MONSTER).sized(1, 1).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ねっしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "This is not a block, but a creeper. So boils water, soon this explodes.";
        }

        @Override
        public String getJaJPDesc() {
            return "岩漿に化けた匠。水を沸かして、正体をあらわす。";
        }

        @Override
        public String getEnUSName() {
            return "Hydrothermal Creeper";
        }

        @Override
        public String getJaJPName() {
            return "熱匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x992244;
        }

        @Override
        public int getSecondaryColor() {
            return 0x442255;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.FIRE;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.MAGMA_BLOCK;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCHydrothermalCreeper>) type, context -> new TCBlockCreeperRenderer(context, true));
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCHydrothermalCreeper::checkHTCreeperSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }

        @Override
        public List<TagKey<EntityType<?>>> getEntityTypeTags() {
            List list = TCCreeperContext.super.getEntityTypeTags();
            list.add(EntityTypeTags.CAN_BREATHE_UNDER_WATER);
            return list;
        }

    }
}
