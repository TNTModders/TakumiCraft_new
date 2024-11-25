package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.TCItemModelProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class AbstractTCStandingBlockItem extends TCBlockItem implements ITCItems {
    protected final Block wallBlock;
    private final Direction attachmentDirection;
    private final Block base;

    public AbstractTCStandingBlockItem(Block block, Block wallBlock, Direction direction) {
        super(block, new Item.Properties().setId(TCItemCore.TCItemId(((ITCBlocks) block).getRegistryName())));
        this.wallBlock = wallBlock;
        this.attachmentDirection = direction;
        this.base = block;
    }

    protected boolean canPlace(LevelReader level, BlockState state, BlockPos pos) {
        return state.canSurvive(level, pos);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext placeContext) {
        BlockState blockstate = this.wallBlock.getStateForPlacement(placeContext);
        BlockState blockstate1 = null;
        LevelReader levelreader = placeContext.getLevel();
        BlockPos blockpos = placeContext.getClickedPos();

        for (Direction direction : placeContext.getNearestLookingDirections()) {
            if (direction != this.attachmentDirection.getOpposite()) {
                BlockState blockstate2 = direction == this.attachmentDirection ? this.getBlock().getStateForPlacement(placeContext) : blockstate;
                if (blockstate2 != null && this.canPlace(levelreader, blockstate2, blockpos)) {
                    blockstate1 = blockstate2;
                    break;
                }
            }
        }

        return blockstate1 != null && levelreader.isUnobstructed(blockstate1, blockpos, CollisionContext.empty()) ? blockstate1 : null;
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {
        super.registerBlocks(map, item);
        map.put(this.wallBlock, item);
    }

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
        super.removeFromBlockToItemMap(blockToItemMap, itemIn);
        blockToItemMap.remove(this.wallBlock);
    }

    @Override
    public void registerItemModel(TCItemModelProvider provider) {
    }

    @Override
    public String getRegistryName() {
        return ((ITCBlocks) this.base).getRegistryName();
    }

    @Override
    public String getEnUSName() {
        return ((ITCBlocks) this.base).getEnUSName();
    }

    @Override
    public String getJaJPName() {
        return ((ITCBlocks) this.base).getJaJPName();
    }
}
