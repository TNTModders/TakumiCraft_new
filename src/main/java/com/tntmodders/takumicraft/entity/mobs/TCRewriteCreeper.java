package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class TCRewriteCreeper extends AbstractTCCreeper {

    public TCRewriteCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.REWRITE;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<Block> blocks = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.defaultBlockState().getDestroySpeed(level(), blockPosition()) >= 0 && block.defaultBlockState().isCollisionShapeFullBlock(level(), blockPosition())).toList();
        BlockState state = blocks.get(getRandom().nextInt(blocks.size())).defaultBlockState();
        event.getAffectedBlocks().forEach(pos -> {
            if (pos.getY() < TCRewriteCreeper.this.getY()) {
                TCRewriteCreeper.this.level().setBlock(pos, state, 3);
            }
        });
        event.getAffectedBlocks().clear();
    }

    public static class TCRewriteCreeperContext implements TCCreeperContext<TCRewriteCreeper> {
        private static final String NAME = "rewritecreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCRewriteCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "へんげたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Rewrite blocks! What would it happen!?";
        }

        @Override
        public String getJaJPDesc() {
            return "周囲のブロックをランダムで一種類に書き換える。大惨事から超儲かる爆発。";
        }

        @Override
        public String getEnUSName() {
            return "Rewrite Creeper";
        }

        @Override
        public String getJaJPName() {
            return "変化匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaa0066;
        }

        @Override
        public int getPrimaryColor() {
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
