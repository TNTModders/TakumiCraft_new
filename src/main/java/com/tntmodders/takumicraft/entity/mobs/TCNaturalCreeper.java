package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCNaturalCreeper extends AbstractTCCreeper {

    public TCNaturalCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 3;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.NATURAL;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().clearToBlow();
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        if (!this.level().isClientSide) {
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(6), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(6).east(1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(6).north(1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(6).east(-1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(6).north(-1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(5), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(5).east(1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(5).north(1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(5).east(-1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(5).north(-1), TCBlockCore.CREEPER_BOMB.defaultBlockState());
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x * z) != 4) {
                        TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(4).offset(x, 0, z), TCBlockCore.CREEPER_BOMB.defaultBlockState());
                        TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(3).offset(x, 0, z), TCBlockCore.CREEPER_BOMB.defaultBlockState());
                    }
                }
            }
            for (int y = 0; y < 6; y++) {
                TCBlockUtils.TCSetBlock(this.level(), this.getOnPos().above(y), (this.isPowered() ? TCBlockCore.DEEPSLATE_GUNORE : TCBlockCore.GUNORE).defaultBlockState());
            }
        }
    }

    public static class TCNaturalCreeperContext implements TCCreeperContext<TCNaturalCreeper> {
        private static final String NAME = "naturalcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCNaturalCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "しぜんたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Good for nature... no, no, the good look tree contains explosive!";
        }

        @Override
        public String getJaJPDesc() {
            return "その木何の木? 爆弾の木!! 資源にもなるが誘爆注意。";
        }

        @Override
        public String getEnUSName() {
            return "Natural Creeper";
        }

        @Override
        public String getJaJPName() {
            return "自然匠";
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
            return 122752;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
