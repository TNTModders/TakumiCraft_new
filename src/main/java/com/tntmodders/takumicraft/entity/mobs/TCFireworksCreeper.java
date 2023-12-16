package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class TCFireworksCreeper extends AbstractTCCreeper {

    public TCFireworksCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 4;
    }

    public static CompoundTag getFireworks() {
        try {
            return TagParser.parseTag("{Fireworks:{Flight:1,Explosions:[{Type:3,Flicker:1,Trail:1,Colors:[I;65280],FadeColors:[I;65280]}]}}");
        } catch (Exception ignored) {
        }
        return CompoundTag.builder().build();
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.FIREWORKS;
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        stack.setTag(getFireworks());
        for (int i = 0; i < 5 * (this.isPowered() ? 2 : 1); i++) {
            FireworkRocketEntity entity = new FireworkRocketEntity(this.level(), this.getRandomX(5), this.getRandomY(), this.getRandomZ(5), stack);
            this.level().addFreshEntity(entity);
        }
    }

    public static class TCFireworksCreeperContext implements TCCreeperContext<TCFireworksCreeper> {
        private static final String NAME = "fireworkscreeper";
        public static final EntityType<? extends AbstractTCCreeper> CREEPER = EntityType.Builder.of(TCFireworksCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build(TakumiCraftCore.MODID + ":" + NAME);

        @Override
        public String getRegistryName() {
            return NAME;
        }

        @Override
        public String getJaJPRead() {
            return "はなびたくみ";
        }

        @Override
        public String getEnUSDesc() {
            return "Fireworks! Give you a happiness, for this terrible world.";
        }

        @Override
        public String getJaJPDesc() {
            return "夜空に大輪を、匠の印を打ち上げる。たまには夜空を見上げようか。";
        }

        @Override
        public String getEnUSName() {
            return "Fireworks Creeper";
        }

        @Override
        public String getJaJPName() {
            return "花火匠";
        }

        @Override
        public EntityType<?> entityType() {
            return CREEPER;
        }

        @Override
        public int getSecondaryColor() {
            return 0xAEBC1B;
        }

        @Override
        public EnumTakumiElement getElement() {
            return EnumTakumiElement.FIRE;
        }

        @Override
        public EnumTakumiRank getRank() {
            return EnumTakumiRank.LOW;
        }

        @Override
        public LootTable.Builder additionalBuilder(LootTable.Builder lootTable) {
            return TCCreeperContext.super.additionalBuilder(lootTable).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.FIREWORK_ROCKET).apply(SetNbtFunction.setTag(TCFireworksCreeper.getFireworks())))).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)));
        }
    }
}
