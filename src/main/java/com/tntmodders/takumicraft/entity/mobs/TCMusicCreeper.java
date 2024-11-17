package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCMusicCreeper extends AbstractTCCreeper {

    public TCMusicCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.MUSIC;
    }

    @Override
    public void tick() {
        if (this.isAlive() && this.getRandom().nextInt(5) == 0) {
            this.level().addParticle(ParticleTypes.NOTE, this.getRandomX(2), this.getRandomY(2), this.getRandomZ(2), 0, 0, 0);
        }
        super.tick();
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().clear();
        event.getAffectedBlocks().clear();
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        this.level().levelEvent(null, 1010, this.getOnPos(), Item.getId(Items.MUSIC_DISC_13));
    }

    public static class TCMusicCreeperContext implements TCCreeperContext<TCMusicCreeper> {
        private static final String NAME = "musiccreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCMusicCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おとたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Terrible sound for you! Give you a record, for special present for you.";
        }

        @Override
        public String getJaJPDesc() {
            return "音を奏で自慢の曲をプレイヤーに披露する。SAN値はピンチ、現地は死地に。";
        }

        @Override
        public String getEnUSName() {
            return "Music Creeper";
        }

        @Override
        public String getJaJPName() {
            return "音匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xffaa00;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.MUSIC_DISC_13;
        }
    }
}
