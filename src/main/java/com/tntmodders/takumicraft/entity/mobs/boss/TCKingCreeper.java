package com.tntmodders.takumicraft.entity.mobs.boss;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TCKingCreeper extends AbstractTCCreeper {

    private static final EntityDataAccessor<Integer> ATTACK_ID = SynchedEntityData.defineId(TCKingCreeper.class, EntityDataSerializers.INT);

    public TCKingCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 7;
        this.maxSwell = 60;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_ID, 0);
    }

    @Override
    public void ignite() {
        if (!this.isIgnited()) {
            this.setRandomAttackID();
        }
        this.entityData.set(DATA_IS_IGNITED, !isIgnited());
    }

    public int getSwell() {
        return this.swell;
    }

    public EnumTCKingCreeperAttackID getAttackID() {
        return EnumTCKingCreeperAttackID.getID(this.getEntityData().get(ATTACK_ID));
    }

    protected void setAttackID(EnumTCKingCreeperAttackID id) {
        this.getEntityData().set(ATTACK_ID, id.getID());
    }

    public void setRandomAttackID() {
        this.setAttackID(EnumTCKingCreeperAttackID.getRandomID(this.getRandom()));
    }

    @Override
    public void explodeCreeper() {
        TCLoggingUtils.info(this.getAttackID());
        if (!this.level().isClientSide) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, Level.ExplosionInteraction.MOB);
            if (this.isIgnited()) {
                this.ignite();
            }
            this.setSwellDir(-2);
            this.setAttackID(EnumTCKingCreeperAttackID.NONE);
        }
        this.swell = this.maxSwell / 2;
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
        return TCEntityCore.KING;
    }

    public static class TCKingCreeperContext implements TCCreeperContext<TCKingCreeper> {
        private static final String NAME = "kingcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCKingCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おうしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "The king has come, it's time to bomb.";
        }

        @Override
        public String getJaJPDesc() {
            return "跪き、命乞いをしろ。王は凱旋する、匠たちの勝利の時。";
        }

        @Override
        public String getEnUSName() {
            return "King Creeper";
        }

        @Override
        public String getJaJPName() {
            return "王匠";
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
            return EnumTakumiElement.TAKUMI_MD;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.BOSS;
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

    public enum EnumTCKingCreeperAttackID {
        NONE(0, true),
        RANDOM_EXP(1),
        BALL_EXP(2),
        RANDOM_FIRE(3),
        BALL_FIRE(4),
        RANDOM_THUNDER(5),
        FOLLOW_THUNDER(6),
        FIREBALL(7),
        TP_EXP(8),
        STORM(9),
        SWORD(10),
        MACE(11),
        ARROWRAIN(12),
        KINGBLOCK(13),
        SP_EXP(90, true);

        private final int id;
        private final boolean isSPAtk;

        EnumTCKingCreeperAttackID(int id) {
            this(id, false);
        }

        EnumTCKingCreeperAttackID(int id, boolean isSPAtk) {
            this.id = id;
            this.isSPAtk = isSPAtk;
        }

        public int getID() {
            return id;
        }

        public boolean isSPAtk() {
            return isSPAtk;
        }

        public static Stream<EnumTCKingCreeperAttackID> getIDs() {
            return Arrays.stream(EnumTCKingCreeperAttackID.values()).sorted(Comparator.comparingInt(EnumTCKingCreeperAttackID::getID));
        }

        public static EnumTCKingCreeperAttackID getID(int i) {
            Optional<EnumTCKingCreeperAttackID> optional = getIDs().filter(id -> id.getID() == i).findAny();
            return optional.orElse(NONE);
        }

        public static EnumTCKingCreeperAttackID getRandomID(RandomSource random) {
            List<EnumTCKingCreeperAttackID> list = Arrays.stream(EnumTCKingCreeperAttackID.values()).filter(id -> !id.isSPAtk).toList();
            return list.get(random.nextInt(list.size()));
        }
    }
}
