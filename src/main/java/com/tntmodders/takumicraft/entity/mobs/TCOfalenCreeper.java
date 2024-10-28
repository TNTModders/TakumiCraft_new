package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public class TCOfalenCreeper extends AbstractTCCreeper {

    public TCOfalenCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.maxSwell = 150;
        this.explosionRadius = 7;
    }

    @Override
    public void tick() {
        super.tick();
        if ((this.getSwellDir() > 0 || this.isIgnited()) && this.swell > 0 && this.swell % 50 == 0) {
            this.explode();
        }
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        this.explode();
    }

    private void explode() {
        for (int x = -10; x <= 10; x++) {
            for (int y = -10; y <= 10; y++) {
                for (int z = -10; z <= 10; z++) {
                    int sqrt = x * x + y * y + z * z;
                    if (sqrt > 98 && sqrt < 102) {
                        TCExplosionUtils.createExplosion(this.level(), this, this.getX() + x, this.getY() + y, this.getZ() + z, this.isPowered() ? 5 : 2.5f, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return !source.is(DamageTypes.EXPLOSION) && super.hurt(source, damage);
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter getter, BlockPos pos, BlockState state, FluidState fluidState, float power) {
        return TCBlockUtils.TCAntiExplosiveBlock(this.level(), pos, state) ? super.getBlockExplosionResistance(explosion, getter, pos, state, fluidState, power) : 0.25f;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.OFALEN;
    }

    public static class TCOfalenCreeperContext implements TCCreeperContext<TCOfalenCreeper> {
        private static final String NAME = "ofalencreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCOfalenCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おふぁれんたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Ore powered! Hard attack, and destroy it with patient.";
        }

        @Override
        public String getJaJPDesc() {
            return "異世界の鉱石の力を纏い、この匠は現れる。戦え、魔には武を以て。";
        }

        @Override
        public String getEnUSName() {
            return "Ofalen Creeper";
        }

        @Override
        public String getJaJPName() {
            return "王覇錬匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x0000dd;
        }

        @Override
        public int getSecondaryColor() {
            return 0xdd0000;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_MD;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.HIGH;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCItemCore.OFALEN;
        }

        @Override
        public AttributeSupplier.Builder entityAttribute() {
            return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.65);
        }

        @Override
        public boolean registerSpawn(SpawnPlacementRegisterEvent event, EntityType<AbstractTCCreeper> type) {
            return false;
        }

        @Override
        public void registerModifierSpawn(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        }

        @Override
        public boolean alterSpawn() {
            return true;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1f, 1f);
        }
    }
}
