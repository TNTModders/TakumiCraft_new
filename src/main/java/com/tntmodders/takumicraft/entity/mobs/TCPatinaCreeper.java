package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCPatinaCreeper extends AbstractTCCreeper {

    public TCPatinaCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 10;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.COPPER;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().forEach(pos -> {
            if (this.level().getBlockState(pos).getBlock() instanceof WeatheringCopper copper) {
                BlockState previous = this.level().getBlockState(pos);
                Block previousBlock = previous.getBlock();
                if (WeatheringCopper.getNext(previousBlock).isPresent()) {
                    while (WeatheringCopper.getNext(previousBlock).isPresent()) {
                        previousBlock = WeatheringCopper.getNext(previousBlock).get();
                    }
                    BlockState state = previousBlock.defaultBlockState();
                    if (!previous.getProperties().isEmpty()) {
                        for (Property property : previous.getProperties()) {
                            state = state.setValue(property, previous.getValue(property));
                        }
                    }
                    this.level().setBlock(pos, state, 3);
                    this.level().explode(null, pos.getX(), pos.getY(), pos.getZ(), this.isPowered() ? 6f : 4f, Level.ExplosionInteraction.NONE);
                }
            }
        });
        event.getAffectedBlocks().clear();
        event.getAffectedEntities().clear();
    }

    public static class TCPatinaCreeperContext implements TCCreeperContext<TCPatinaCreeper> {
        private static final String NAME = "patinacreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder
                .of(TCPatinaCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
                .build(TCEntityUtils.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "ろくしょう";
        }

        @Override
        public String getEnUSDesc() {
            return "A creeper with copper metalic rust. Too bated, too brittle.";
        }

        @Override
        public String getJaJPDesc() {
            return "錆びた体躯と染み付いた技、銅の匠は衰えない。";
        }

        @Override
        public String getEnUSName() {
            return "Patina Creeper";
        }

        @Override
        public String getJaJPName() {
            return "緑匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0x33aa00;
        }

        @Override
        public int getSecondaryColor() {
            return 0xaa3300;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
