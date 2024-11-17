package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ExplosionEvent;

public class TCPotatoCreeper extends AbstractTCCreeper {

    public TCPotatoCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.POTATO;
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        event.getAffectedEntities().forEach(entity -> {
            if (!this.level().isClientSide()) {
                if (entity instanceof LivingEntity living) {
                    for (int i = 0; i < (this.isPowered() ? 64 : 32); i++) {
                        ItemEntity item = new ItemEntity(this.level(), living.getRandomX(10), living.getY() + 5, living.getRandomZ(10), new ItemStack(Items.POISONOUS_POTATO, 1 + this.getRandom().nextInt(16)));
                        this.level().addFreshEntity(item);
                        this.level().explode(item, item.getX(), item.getY(), item.getZ(), 1f, Level.ExplosionInteraction.MOB);
                    }

                    if (!living.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && living.getItemInHand(InteractionHand.MAIN_HAND).getRarity() == Rarity.COMMON) {
                        living.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.POISONOUS_POTATO, 64));
                    }
                    if (!living.getItemInHand(InteractionHand.OFF_HAND).isEmpty() && living.getItemInHand(InteractionHand.OFF_HAND).getRarity() == Rarity.COMMON) {
                        living.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.POISONOUS_POTATO, 64));
                    }

                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 9));
                } else if (entity instanceof ItemEntity itemEntity) {
                    if (!itemEntity.getItem().is(Items.POISONOUS_POTATO) && itemEntity.getItem().getRarity() == Rarity.COMMON) {
                        itemEntity.setItem(new ItemStack(Items.POISONOUS_POTATO, 64));
                    }
                }
            }
        });
    }

    public static class TCPotatoCreeperContext implements TCCreeperContext<TCPotatoCreeper> {
        private static final String NAME = "potatocreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCPotatoCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TCEntityCore.TCEntityId(NAME));

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "いもたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "The (s)mashing creeper you always asked for!";
        }

        @Override
        public String getJaJPDesc() {
            return "ぜんぶがぽてとになっていく。";
        }

        @Override
        public String getEnUSName() {
            return "Potato Creeper";
        }

        @Override
        public String getJaJPName() {
            return "芋匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xffffaa;
        }

        @Override
        public int getPrimaryColor() {
            return 0x336611;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.GRASS_M;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }
    }
}
