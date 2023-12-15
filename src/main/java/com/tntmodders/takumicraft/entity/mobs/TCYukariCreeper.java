package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCYukariCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCYukariCreeper extends AbstractTCCreeper {
    public static final ResourceLocation YUKARI_LOCATION = new ResourceLocation(TakumiCraftCore.MODID, "textures/entity/creeper/yukaricreeper_armor.png");

    public TCYukariCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            int i = this.isPowered() ? 40 : 25;
            for (int x = -i; x <= i; x++) {
                for (int z = -i; z <= i; z++) {
                    if (x * x + z * z < i * i) {
                        TCExplosionUtils.createExplosion(this.level(), this, this.getX() + x, this.getY(), this.getZ() + z, (float) Math.sqrt(i - Math.sqrt(x * x + z * z) + 1) * 1.75f, true);
                    }
                }
            }
            this.discard();
            this.spawnLingeringCloud();
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().getToBlow().forEach(pos -> {
            if (pos.getY() >= this.getY()) {
                event.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else {
                event.getLevel().setBlock(pos, TCBlockCore.CREEPER_WOOL_MAP.get(DyeColor.PINK).defaultBlockState(), 3);
            }
        });
        event.getExplosion().clearToBlow();
        DamageSource source = event.getLevel().damageSources().explosion(event.getExplosion());
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living && !living.isInvulnerableTo(source)) {
                entity.hurt(source, living.getHealth() - 1f);
            }
        });
        event.getAffectedEntities().clear();
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter getter, BlockPos pos, BlockState state, FluidState fluidState, float power) {
        return TCBlockUtils.TCAntiExplosiveBlock(this.level(), pos, state) ? super.getBlockExplosionResistance(explosion, getter, pos, state, fluidState, power) : 0.25f;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.YUKARI;
    }

    public static class TCYukariCreeperContext implements TCCreeperContext<TCYukariCreeper> {
        private static final String NAME = "yukaricreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCYukariCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ゆかりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Even powered! She unites you all with superflat.";
        }

        @Override
        public String getJaJPDesc() {
            return "全てが平坦に帰す爆発に、この匠は現れる。結べ、月と広い大地を。";
        }

        @Override
        public String getEnUSName() {
            return "Yukari Creeper";
        }

        @Override
        public String getJaJPName() {
            return "ゆかり匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xff00ff;
        }

        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<TCYukariCreeper>) type, TCYukariCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.YUKARI;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.HIGH;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCBlockCore.YUKARI_BOMB;
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            return false;
        }

        @Override
        public void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1f, 1f);
        }

        @Override
        public ResourceLocation getArmor() {
            return YUKARI_LOCATION;
        }
    }
}
