package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.client.renderer.entity.TCBlockCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public class TCGunOreCreeper extends AbstractTCBlockCreeper {

    public TCGunOreCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 6;
        this.maxSwell = 40;
    }

    public static boolean checkGunOreCreeperSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor levelAccessor, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getBlockState(pos.below()).is(Blocks.STONE) && checkTakumiSpawnRules(type, levelAccessor, spawnType, pos, random);
    }

    @Override
    public BlockState getBlock() {
        return TCBlockCore.GUNORE_CREEPER.defaultBlockState();
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.GUNORE;
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter wolrdIn, BlockPos pos, BlockState blockStateIn, FluidState p_19996_, float p_19997_) {
        return blockStateIn.isAir() || blockStateIn.is(BlockTags.MINEABLE_WITH_PICKAXE) ? 0.05f : super.getBlockExplosionResistance(explosion, wolrdIn, pos, blockStateIn, p_19996_, p_19997_);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource p_21016_, float p_21017_) {
        float amount = p_21017_;
        if (p_21016_.getEntity() instanceof LivingEntity living && living.getMainHandItem().is(ItemTags.PICKAXES)) {
            amount *= 10f;
        }
        return super.hurtServer(level, p_21016_, amount);
    }

    public static class TCGunOreCreeperContext implements TCCreeperContext<TCGunOreCreeper> {
        private static final String NAME = "gunorecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCGunOreCreeper::new, MobCategory.MONSTER).sized(1, 1).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "かやくいわたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "This is not a block, but a creeper. You pick this, soon this explodes.";
        }

        @Override
        public String getJaJPDesc() {
            return "火薬岩に化けた匠。掘ればその途端、正体をあらわす。";
        }

        @Override
        public String getEnUSName() {
            return "Gunpowder Ore Creeper";
        }

        @Override
        public String getJaJPName() {
            return "火薬岩匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 7829367;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1, 16);
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCGunOreCreeper>) type, TCBlockCreeperRenderer::new);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            event.register(type, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TCGunOreCreeper::checkGunOreCreeperSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
            return true;
        }
    }
}
