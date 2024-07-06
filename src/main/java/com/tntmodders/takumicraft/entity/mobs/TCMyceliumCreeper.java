package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCMyceliumCreeper extends AbstractTCCreeper {

    public TCMyceliumCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.MYCELIUM;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        for (BlockPos pos : event.getAffectedBlocks()) {
            if (!this.level().getBlockState(pos).isAir()) {
                this.level().setBlock(pos, Blocks.MYCELIUM.defaultBlockState(), 3);
            } else {
                this.level().setBlock(pos, Blocks.RED_MUSHROOM_BLOCK.defaultBlockState(), 3);
            }
        }
        event.getAffectedBlocks().clear();
    }

    public static class TCMyceliumCreeperContext implements TCCreeperContext<TCMyceliumCreeper> {
        private static final String NAME = "myceliumcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCMyceliumCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "きんしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "Creeping mycelium, do you want to eat mushroom?";
        }

        @Override
        public String getJaJPDesc() {
            return "這い寄る菌糸と茸の匠。";
        }

        @Override
        public String getEnUSName() {
            return "Mycelium Creeper";
        }

        @Override
        public String getJaJPName() {
            return "菌匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0x884488;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GROUND;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return Blocks.MYCELIUM;
        }
    }
}
