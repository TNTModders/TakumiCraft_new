package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class TCFallingBombBlock extends ColoredFallingBlock implements ITCBlocks {

    public TCFallingBombBlock() {
        super(new ColorRGBA(0x006600), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.SNARE).strength(0.6F, 0F).sound(SoundType.GRAVEL));
    }

    @Override
    public String getRegistryName() {
        return "fallingbomb";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public String getEnUSName() {
        return "Falling Bomb";
    }

    @Override
    public String getJaJPName() {
        return "阿闍梨匠の爆弾";
    }

    private void explode(Level level, BlockPos pos) {
        TCExplosionUtils.createExplosion(level, null, pos, 2f);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState state1, FallingBlockEntity entity) {
        this.explode(level, pos);
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
        this.explode(level, pos);
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
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }
}
