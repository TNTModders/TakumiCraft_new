package com.tntmodders.takumicraft.entity.mobs;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCBlockUtils;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.level.ExplosionEvent;

import java.util.List;

public class TCFireworksCreeper extends AbstractTCCreeper {

    public TCFireworksCreeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
        this.explosionRadius = 4;
    }

    public static Fireworks getFireworks() {
        return new Fireworks(1, List.of(new FireworkExplosion(FireworkExplosion.Shape.CREEPER, IntList.of(65280), IntList.of(65280), true, true)));
    }

    public static void createTree(Entity entity, BlockPos pos) {
        for (int y = 0; y < 7; y++) {
            switch (y) {
                case 1: {
                    int[] xRange = {-1, 1, 0, 0};
                    int[] zRange = {0, 0, -1, 1};
                    for (int i = 0; i < 4; i++) {
                        TCBlockUtils.TCSetBlock(entity.level(), pos.offset(xRange[i], y, zRange[i]),
                                Blocks.GLOWSTONE.defaultBlockState());
                        TCBlockUtils.TCSetBlock(entity.level(), pos.offset(xRange[i] * 2, y, zRange[i] * 2),
                                Blocks.CHEST.defaultBlockState());
                        BlockEntity tile = entity.level().getBlockEntity(pos.offset(xRange[i] * 2, y, zRange[i] * 2));
                        if (tile instanceof ChestBlockEntity chest) {
                            int point = entity.level().getRandom().nextInt(5) + 1;
                            ItemLike[] item = {Items.DIAMOND, Items.GUNPOWDER, TCBlockCore.SUPER_CREEPER_BED, Items.EMERALD, TCItemCore.CREEPER_ARROW, TCItemCore.CREEPER_SHIELD, TCItemCore.CREEPER_SWORD, Items.ELYTRA, Items.NETHERITE_INGOT, TCBlockCore.CREEPER_BOMB, TCBlockCore.TAKUMI_ALTAR};
                            for (int p = 0; p <= point; p++) {
                                ItemLike returner = item[entity.level().getRandom().nextInt(item.length)];
                                int max = returner == Items.NETHERITE_INGOT || returner == TCBlockCore.TAKUMI_ALTAR ? 1 : returner.asItem().getDefaultMaxStackSize();
                                ItemStack stack = new ItemStack(returner, entity.level().getRandom().nextInt(max / 4 + 1) + 1);
                                stack.inventoryTick(entity.level(), entity, 0, false);
                                chest.setItem(p, stack);
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    for (int x = -2; x <= 2; x++) {
                        for (int z = -2; z <= 2; z++) {
                            TCBlockUtils.TCSetBlock(entity.level(), pos.offset(x, y, z), Blocks.OAK_LEAVES.defaultBlockState());
                        }
                    }
                    break;
                }
                case 3, 5: {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            TCBlockUtils.TCSetBlock(entity.level(), pos.offset(x, y, z), Blocks.OAK_LEAVES.defaultBlockState());
                        }
                    }
                    break;
                }
                case 4: {
                    int[] xRange = {-1, 1, 0, 0};
                    int[] zRange = {0, 0, -1, 1};
                    for (int i = 0; i < 4; i++) {
                        TCBlockUtils.TCSetBlock(entity.level(), pos.offset(xRange[i], y, zRange[i]), Blocks.GLOWSTONE.defaultBlockState());
                    }
                    break;
                }
                case 6: {
                    TCBlockUtils.TCSetBlock(entity.level(), pos.above(y), Blocks.OAK_LEAVES.defaultBlockState());
                    break;
                }
            }
            if (y != 6) {
                TCBlockUtils.TCSetBlock(entity.level(), pos.above(y), Blocks.OAK_LOG.defaultBlockState());
            }
        }
    }

    public static void createKagamimochi(Entity entity) {
        int[] count = {3, 2, 1, 1, 0};

        for (int y = 0; y < 5; y++) {
            for (int x = -1 * count[y]; x <= count[y]; x++) {
                for (int z = -1 * count[y]; z <= count[y]; z++) {
                    if (count[y] != 0) {
                        TCBlockUtils.TCSetBlock(entity.level(),
                                new BlockPos(entity.getBlockX() + x, entity.getBlockY() + y, entity.getBlockZ() + z), Blocks.WHITE_WOOL.defaultBlockState());
                    }
                }
            }
            if (count[y] == 0) {
                BlockPos pos = new BlockPos(entity.getBlockX(), entity.getBlockY() + y, entity.getBlockZ());
                TCBlockUtils.TCSetBlock(entity.level(), pos, Blocks.ORANGE_WOOL.defaultBlockState());
                TCBlockUtils.TCSetBlock(entity.level(), pos.below(), Blocks.TRAPPED_CHEST.defaultBlockState());
                BlockEntity tile = entity.level().getBlockEntity(pos.below());
                if (tile instanceof ChestBlockEntity chest) {
                    int point = entity.level().getRandom().nextInt(10) + 1;
                    ItemLike[] item = {Items.DIAMOND, Items.GUNPOWDER, TCBlockCore.SUPER_CREEPER_BED, Items.EMERALD, TCItemCore.CREEPER_ARROW, TCItemCore.CREEPER_SHIELD, TCItemCore.CREEPER_SWORD, Items.ELYTRA, Items.NETHERITE_INGOT, TCBlockCore.CREEPER_BOMB, TCBlockCore.TAKUMI_ALTAR};
                    for (int p = 0; p <= point; p++) {
                        ItemLike returner = item[entity.level().getRandom().nextInt(item.length)];
                        int max = returner == Items.NETHERITE_INGOT || returner == TCBlockCore.TAKUMI_ALTAR ? 1 : returner.asItem().getDefaultMaxStackSize();
                        ItemStack stack = new ItemStack(returner, entity.level().getRandom().nextInt(max / 4 + 1) + 1);
                        stack.inventoryTick(entity.level(), entity, 0, false);
                        chest.setItem(p, stack);
                    }
                }
                TCBlockUtils.TCSetBlock(entity.level(), pos.below(2), Blocks.TNT.defaultBlockState());
            }
        }
    }

    @Override
    public void explodeCreeperEvent(ExplosionEvent.Detonate event) {
        if (TCEntityUtils.isNewYear() || TCEntityUtils.isXmas()) {
            event.getExplosion().clearToBlow();
        }
    }

    @Override
    public void explodeCreeper() {
        super.explodeCreeper();
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        stack.set(DataComponents.FIREWORKS, getFireworks());
        for (int i = 0; i < 5 * (this.isPowered() ? 2 : 1); i++) {
            FireworkRocketEntity entity = new FireworkRocketEntity(this.level(), this.getRandomX(5), this.getRandomY(), this.getRandomZ(5), stack);
            this.level().addFreshEntity(entity);
        }

        if (TCEntityUtils.isXmas()) {
            createTree(this, this.getOnPos().above());
            this.spawn();
        } else if (TCEntityUtils.isNewYear()) {
            createKagamimochi(this);
            this.spawn();
        }
    }

    public void spawn() {
        for (int i = 0; i < (this.isPowered() ? 20 : 10); i++) {
            TCCreeperContext context = TCEntityCore.ENTITY_CONTEXTS.get(this.level().getRandom().nextInt(TCEntityCore.ENTITY_CONTEXTS.size()));
            if (context.getRank().getLevel() < TCCreeperContext.EnumTakumiRank.HIGH.getLevel()) {
                Entity entity = context.entityType().create(this.level());
                entity.setPos(this.getRandomX(10), this.getRandomY(3), this.getRandomZ(10));
                if (entity instanceof AbstractTCCreeper creeper && creeper.checkSpawnObstruction(this.level())) {
                    this.level().addFreshEntity(entity);
                }
            }
        }
    }

    @Override
    public TCCreeperContext<? extends AbstractTCCreeper> getContext() {
        return TCEntityCore.FIREWORKS;
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
            return TCCreeperContext.super.additionalBuilder(lootTable).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.FIREWORK_ROCKET).apply(SetComponentsFunction.setComponent(DataComponents.FIREWORKS, getFireworks())).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1)))));
        }
    }
}
