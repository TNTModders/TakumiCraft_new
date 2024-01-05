package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TCArtCreeper extends AbstractTCCreeper {

    public TCArtCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.ART;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<Block> blocks = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.defaultBlockState().getDestroySpeed(level(), blockPosition()) >= 0 && block.defaultBlockState().isCollisionShapeFullBlock(level(), blockPosition())).toList();

        event.getExplosion().getToBlow().forEach(pos -> this.level().setBlock(pos, blocks.get(getRandom().nextInt(blocks.size())).defaultBlockState(), 3));
        event.getExplosion().clearToBlow();
    }

    public static class TCArtCreeperContext implements TCCreeperContext<TCArtCreeper> {
        private static final String NAME = "artcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCArtCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "げいじゅつたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Artistic blocks! What should you do?";
        }

        @Override
        public String getJaJPDesc() {
            return "周囲にブロックをランダムで設置する。まさにアーティスティック。";
        }

        @Override
        public String getEnUSName() {
            return "Art Creeper";
        }

        @Override
        public String getJaJPName() {
            return "藝術匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaa0066;
        }

        @Override
        public int getSecondaryColor() {
            return 0x00ff00;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
