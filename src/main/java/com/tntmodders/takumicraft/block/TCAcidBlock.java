package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.block.entity.TCAcidBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TCAcidBlock extends AbstractTCBombBlock implements EntityBlock {

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 15);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public TCAcidBlock() {
        super(Properties.of().requiresCorrectToolForDrops().strength(0.2f, 0f).mapColor(MapColor.COLOR_GREEN).speedFactor(0.3F).noOcclusion(), "acidblock");
        this.registerDefaultState(this.getStateDefinition().any().setValue(STAGE, 0));
    }


    @Override
    public VoxelShape getCollisionShape(BlockState p_56702_, BlockGetter p_56703_, BlockPos p_56704_, CollisionContext p_56705_) {
        return SHAPE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_56707_, BlockGetter p_56708_, BlockPos p_56709_) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_56684_, BlockGetter p_56685_, BlockPos p_56686_, CollisionContext p_56687_) {
        return Shapes.block();
    }


    @Override
    public float getPower() {
        return 0f;
    }

    @Override
    public String getEnUSName() {
        return "Acid Block";
    }

    @Override
    public String getJaJPName() {
        return "腐蝕ブロック";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TCAcidBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? TCAcidBlockEntity::clientTick : TCAcidBlockEntity::serverTick;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
}
