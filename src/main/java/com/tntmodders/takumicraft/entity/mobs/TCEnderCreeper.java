package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCEnderCreeper extends AbstractTCCreeper {

    public TCEnderCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 3;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ENDER;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity && entity != this) {
                boolean flg;
                for (int i = 0; i < 5 * (this.isPowered() ? 2 : 1); i++) {
                    flg = this.teleport((LivingEntity) entity);
                    if (flg) {
                        break;
                    }
                }
            }
        });
        event.getAffectedEntities().clear();
    }

    protected boolean teleport(LivingEntity entity) {
        if (!entity.level().isClientSide() && entity.isAlive()) {
            double d = this.isPowered() ? 2 : 1;
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 128.0D * d;
            double d1 = this.getY() + (double) (this.random.nextInt(128) - 64);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 128.0D * d;
            return this.teleport(d0, d1, d2, entity);
        } else {
            return false;
        }
    }

    private boolean teleport(double x, double y, double z, LivingEntity entity) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        while (blockpos$mutableblockpos.getY() > entity.level().getMinBuildHeight() && !entity.level().getBlockState(blockpos$mutableblockpos).blocksMotion()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
        }

        BlockState blockstate = entity.level().getBlockState(blockpos$mutableblockpos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
            net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(entity, x, y, z);
            if (event.isCanceled()) return false;
            boolean flag2 = entity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2 && !entity.isSilent()) {
                entity.level().playSound(null, entity.xo, entity.yo, entity.zo, SoundEvents.ENDERMAN_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
                entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }

    public static class TCEnderCreeperContext implements TCCreeperContext<TCEnderCreeper> {
        private static final String NAME = "endercreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCEnderCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "えんだーたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Warp you to somewhere, no one knows where. you might enjoy the trip :)";
        }

        @Override
        public String getJaJPDesc() {
            return "闇より襲われればもう、気がつけば見知らぬ土地に飛んでいるだろう。";
        }

        @Override
        public String getEnUSName() {
            return "Ender Creeper";
        }

        @Override
        public String getJaJPName() {
            return "エンダー匠";
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
            return 0x111133;
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
            return EnumTakumiElement.GROUND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
