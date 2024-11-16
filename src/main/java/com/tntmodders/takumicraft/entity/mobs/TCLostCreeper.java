package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCLostCreeper extends AbstractTCCreeper {

    public TCLostCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.LOST;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                living.getMainHandItem().shrink(this.getRandom().nextInt(5 + 1));
                living.getOffhandItem().shrink(this.getRandom().nextInt(5 + 1));
                if (this.isPowered()) {
                    living.setItemSlot(EquipmentSlot.values()[this.getRandom().nextInt(EquipmentSlot.values().length)], ItemStack.EMPTY);
                }
            }
        });
    }

    public static class TCLostCreeperContext implements TCCreeperContext<TCLostCreeper> {
        private static final String NAME = "lostcreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCLostCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "しっしょう";
        }

        @Override
        public boolean showRead() {
            return true;
        }

        @Override
        public String getEnUSDesc() {
            return "<entity data had lost>";
        }

        @Override
        public String getJaJPDesc() {
            return "<データが消失しています>";
        }

        @Override
        public String getEnUSName() {
            return "Lost Creeper";
        }

        @Override
        public String getJaJPName() {
            return "失匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getPrimaryColor() {
            return 0xaa44ff;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.WIND_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
