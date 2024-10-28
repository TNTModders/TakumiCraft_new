package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

public class TCBoltCreeper extends AbstractTCCreeper {

    public TCBoltCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 7;
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        int factor = this.isPowered() ? 15 : 5;
        for (int i = 0; i < factor * 2; i++) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, this.level());
            bolt.setPos(this.getRandomX(factor), this.getRandomY(factor / 3f), this.getRandomZ(factor));
            this.level().addFreshEntity(bolt);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide() && this.getHealth() > 0f && !this.isPowered()) {
            var entity = TCEntityCore.BOLT.entityType().create(this.level());
            if (entity instanceof TCBoltCreeper bolt) {
                bolt.copyPosition(this);
                bolt.setPowered(true);
                bolt.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * 2);
                this.level().addFreshEntity(bolt);
            }
        }
        super.remove(reason);
    }

    @Override
    public void thunderHit(ServerLevel p_32286_, LightningBolt p_32287_) {
        this.setPowered(true);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        return !source.is(DamageTypes.LIGHTNING_BOLT) && !source.is(DamageTypes.IN_FIRE) && !source.is(DamageTypes.EXPLOSION) && super.hurt(source, damage);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.BOLT;
    }

    public static class TCBoltCreeperContext implements TCCreeperContext<TCBoltCreeper> {
        private static final String NAME = "boltcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCBoltCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "かみなりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Lightning powered! Attack speedy, and deal with it rapidly.";
        }

        @Override
        public String getJaJPDesc() {
            return "雷鳴激しく轟き、この匠は現れる。抗え、焼き尽くされる前に。";
        }

        @Override
        public String getEnUSName() {
            return "Bolt Creeper";
        }

        @Override
        public String getJaJPName() {
            return "雷匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x8888ff;
        }

        @Override
        public boolean alterSpawn() {
            return true;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_MD;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.HIGH;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCItemCore.BOLTSTONE;
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
    }
}
