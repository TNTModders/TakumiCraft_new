package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.projectile.TCAmethystBomb;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCAmethystCreeper extends AbstractTCCreeper {

    private boolean amethystBombSummoned;

    public TCAmethystCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    private List<TCAmethystBomb> getAmethystBombs() {
        return this.level().getEntitiesOfClass(TCAmethystBomb.class, this.getBoundingBox().inflate(15, 15, 15));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.isAlive()) {
            if (this.getSwellDir() > 0 && this.getAmethystBombs().isEmpty() && !this.amethystBombSummoned) {
                this.summonAmethystBomb();
            } else if (this.getSwellDir() <= 0 && !this.isIgnited() && !this.getAmethystBombs().isEmpty()) {
                this.getAmethystBombs().forEach(Entity::discard);
                this.amethystBombSummoned = false;
            }
        }
    }

    @Override
    public void ignite() {
        super.ignite();
        this.summonAmethystBomb();
    }

    private void summonAmethystBomb() {
        for (int i = 0; i < 5 * (this.isPowered() ? 2 : 1); i++) {
            TCAmethystBomb bomb = new TCAmethystBomb(this.level(), this);
            bomb.setPos(this.getRandomX(10), this.getRandomY() + 0.75d, this.getRandomZ(10));
            bomb.setDeltaMovement(0, 0, 0);
            this.level().addFreshEntity(bomb);
            this.amethystBombSummoned = true;
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().removeIf(entity -> entity instanceof TCAmethystBomb);
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.getAmethystBombs().isEmpty()) {
            double x = this.getTarget() != null ? this.getTarget().getX() : this.getLookControl().getWantedX();
            double y = this.getTarget() != null ? this.getTarget().getY() + this.getTarget().getEyeHeight() : this.getLookControl().getWantedY() + this.getEyeHeight();
            double z = this.getTarget() != null ? this.getTarget().getZ() : this.getLookControl().getWantedZ();
            this.getAmethystBombs().forEach(bomb -> bomb.setDeltaMovement((x - bomb.getX()) / 30, (y - bomb.getY()) / 30, (z - bomb.getZ()) / 30));
        }
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.AMETHYST;
    }

    public static class TCAmethystCreeperContext implements TCCreeperContext<TCAmethystCreeper> {
        private static final String NAME = "amethystcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCAmethystCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "あめじすとたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with diamond heart. Too hard, too strong.";
        }

        @Override
        public String getJaJPDesc() {
            return "硬い精神と強靭な肉体、ダイヤの匠は砕けない。";
        }

        @Override
        public String getEnUSName() {
            return "Amethyst Creeper";
        }

        @Override
        public String getJaJPName() {
            return "アメジスト匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xdec0de;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaa00ff;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Items.AMETHYST_SHARD;
        }

        @Override
        public UniformGenerator getDropRange() {
            return UniformGenerator.between(1.0F, 8.0F);
        }
    }
}
