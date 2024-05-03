package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.ArrayList;
import java.util.List;

public class TCRestCreeper extends AbstractTCCreeper {

    public TCRestCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 5;
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.REST;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        List<Entity> entityList = new ArrayList<>();
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof Player player) {
                boolean flg = true;
                while (flg) {
                    BlockPos target = event.getAffectedBlocks().get(this.getRandom().nextInt(event.getAffectedBlocks().size()));
                    if (this.level().getBlockState(target).isAir()) {
                        for (Direction direction : Direction.values()) {
                            if (direction.getAxis() != Direction.Axis.Y && this.level().getBlockState(target.relative(direction)).isAir()) {
                                this.level().setBlock(target, Blocks.GRAY_BED.defaultBlockState().setValue(BedBlock.FACING, direction), 3);
                                this.level().setBlock(target.relative(direction), Blocks.GRAY_BED.defaultBlockState().setValue(BedBlock.PART, BedPart.HEAD).setValue(BedBlock.FACING, direction), 3);
                                player.teleportTo(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5);
                                InteractionResult result = this.level().getBlockState(target).useWithoutItem(this.level(), player, new BlockHitResult(Vec3.atCenterOf(target), direction, target, true));
                                flg = false;
                                break;
                            }
                        }
                    }
                }
            }
        });
        event.getExplosion().clearToBlow();
    }

    public static class TCRestCreeperContext implements TCCreeperContext<TCRestCreeper> {
        private static final String NAME = "restcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCRestCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "やすみたくみ";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "Take a rest... in peace.";
        }

        @Override
        public String getJaJPDesc() {
            return "平和のうちに休むが良い。";
        }

        @Override
        public String getEnUSName() {
            return "Rest Creeper";
        }

        @Override
        public String getJaJPName() {
            return "休匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xff3333;
        }

        @Override
        public int getPrimaryColor() {
            return 0xcccccc;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.NORMAL_D;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.MID;
        }
    }
}
