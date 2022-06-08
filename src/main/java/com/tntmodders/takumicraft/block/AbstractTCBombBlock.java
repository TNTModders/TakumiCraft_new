package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTCBombBlock extends Block implements ITCBlocks {
    public AbstractTCBombBlock(Properties properties, String name) {
        super(properties);
    }

    public abstract float getPower();

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        this.explode(level, pos);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean flg = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
        if (flg) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLAST_PROTECTION, player.getMainHandItem()) == 0) {
                this.explode(world, pos);
            }
        }
        return flg;
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.playerDestroy(level, player, pos, blockState, blockEntity, itemStack);
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
        return false;
    }

    protected void explode(Level level, BlockPos pos) {
        TCExplosionUtils.createExplosion(level, null, pos, this.getPower());
    }
}
