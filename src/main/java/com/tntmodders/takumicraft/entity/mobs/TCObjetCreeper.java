package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCObjetCreeper extends AbstractTCCreeper {

    public TCObjetCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.OBJET;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getExplosion().clearToBlow();
        int range = this.isPowered() ? 6 : 4;
        BlockState state = this.getRandom().nextBoolean() ? TCBlockCore.CREEPER_BRICKS.defaultBlockState() :
                TCBlockCore.CREEPER_IRON.defaultBlockState();
        for (int x = (int) this.getX() - range; x < (int) this.getX() + range; x++) {
            for (int y = (int) this.getY(); y < (int) this.getY() + range * 1.5 + 3; y++) {
                for (int z = (int) this.getZ() - range; z < (int) this.getZ() + range; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (y > this.getY() + range * 1.5) {
                        if (this.getRandom().nextInt(8) == 0) {
                            TCBlockUtils.TCSetBlock(this.level(), pos, TCBlockCore.CREEPER_BOMB.defaultBlockState());
                        }
                    } else if (this.getRandom().nextBoolean()) {
                        TCBlockUtils.TCSetBlock(this.level(), pos, state);
                    }
                }
            }
        }
    }

    public static class TCObjetCreeperContext implements TCCreeperContext<TCObjetCreeper> {
        private static final String NAME = "objetcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCObjetCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "おぶじぇたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Create an objet to you, explosively anti-explosion art.";
        }

        @Override
        public String getJaJPDesc() {
            return "爆発的な芸術を、硬質ブロックの対爆オブジェを作り出す匠。";
        }

        @Override
        public String getEnUSName() {
            return "Objet Creeper";
        }

        @Override
        public String getJaJPName() {
            return "オブジェ匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 1171372;
        }


        @Override
        public void registerRenderer(EntityRenderersEvent.RegisterRenderers event, EntityType<?> type) {
            event.registerEntityRenderer((EntityType<Creeper>) type, TCCreeperRenderer::new);
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public ItemLike getMainDropItem() {
            return TCBlockCore.CREEPER_BOMB;
        }
    }
}
