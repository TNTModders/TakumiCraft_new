package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCVoidCreeper extends AbstractTCCreeper {

    public TCVoidCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 8;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.VOID;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> pos.getY() < this.getY());
    }

    public static class TCVoidCreeperContext implements TCCreeperContext<TCVoidCreeper> {
        private static final String NAME = "voidcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCVoidCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "きょむたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Nothing is there but air. Flat world made by creeper.";
        }

        @Override
        public String getJaJPDesc() {
            return "上を消し飛ばせ、平面をもたらせ。大地を削り、新たなる世界へ導く。";
        }

        @Override
        public String getEnUSName() {
            return "Void Creeper";
        }

        @Override
        public String getJaJPName() {
            return "虚無匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaa00aa;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
