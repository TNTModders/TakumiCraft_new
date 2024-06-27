package com.tntmodders.takumicraft.block;

import com.ibm.icu.impl.Pair;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public abstract class AbstractTCBombBlock extends Block implements ITCBlocks {
    private final String registryName;

    public AbstractTCBombBlock(Properties properties, String name) {
        super(properties);
        this.registryName = name;
    }

    public abstract float getPower();

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        this.explode(level, pos, Math.max(Math.min(this.getPower(), explosion.radius - 0.1f), this.getPower() / 2));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean flg = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
        if (flg) {
            if (EnchantmentHelper.getItemEnchantmentLevel(world.holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.MINESWEEPER), player.getMainHandItem()) == 0 && EnchantmentHelper.getItemEnchantmentLevel(world.holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), player.getMainHandItem()) == 0 && !player.getMainHandItem().is(TCItemCore.MINESWEEPER_TOOLS)) {
                this.explode(world, pos);
            }
        }
        return flg;
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        return false;
    }

    protected void explode(Level level, BlockPos pos) {
        this.explode(level, pos, this.getPower());
    }

    protected void explode(Level level, BlockPos pos, float power) {
        TCExplosionUtils.createExplosion(level, null, pos, power);
    }

    @Override
    public String getRegistryName() {
        return this.registryName;
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.add(block, loot.createSingleItemTableWithMinesweeper(block));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.EXPLOSIVES);
    }

    @Override
    public List<Pair<TagKey<Block>, TagKey<Item>>> getItemTags() {
        return List.of(Pair.of(TCBlockCore.EXPLOSIVES, TCItemCore.EXPLOSIVES));
    }
}
