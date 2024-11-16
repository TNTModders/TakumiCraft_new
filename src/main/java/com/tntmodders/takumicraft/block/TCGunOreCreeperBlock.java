package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.TCGunOreCreeper;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class TCGunOreCreeperBlock extends TCGunOreBlock {

    private final UniformInt xpRange = UniformInt.of(10, 15);

    public TCGunOreCreeperBlock() {
        super(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(0f, 0f).mapColor(MapColor.STONE), "gunore_creeper");
    }

    @Override
    public float getPower() {
        return 0f;
    }

    @Override
    public String getEnUSName() {
        return "Gunore Creeper";
    }

    @Override
    public String getJaJPName() {
        return "火薬岩匠";
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return 0;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput output) {
    }

    private void spawnCreeper(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            TCGunOreCreeper creeper = (TCGunOreCreeper) TCEntityCore.GUNORE.entityType().create(level, EntitySpawnReason.NATURAL);
            creeper.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(creeper);
            creeper.ignite();
        }
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropXP) {
        if (stack.getEnchantments().getLevel(level.holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.MINESWEEPER)) == 0) {
            this.spawnCreeper(level, pos);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        level.destroyBlock(pos, false);
        this.spawnCreeper(level, pos);
    }
}
