package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCIceCreeper extends AbstractTCCreeper {

    public TCIceCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 2;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ICE;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> this.level().setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3));
        event.getAffectedBlocks().clear();
    }

    public static class TCIceCreeperContext implements TCCreeperContext<TCIceCreeper> {
        private static final String NAME = "icecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCIceCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "こおりたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Pack you into ice. Burn it by fire? No,frozen ice can freeze even fire.";
        }

        @Override
        public String getJaJPDesc() {
            return "氷漬けにする涼しげな匠。因みに火よりも風に弱い。";
        }

        @Override
        public String getEnUSName() {
            return "Ice Creeper";
        }

        @Override
        public String getJaJPName() {
            return "氷匠";
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
            return 0x8888ff;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WATER;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
