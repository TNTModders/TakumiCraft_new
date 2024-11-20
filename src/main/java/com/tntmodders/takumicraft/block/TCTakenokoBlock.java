package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.block.entity.TCTakenokoBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TCTakenokoBlock extends BambooSaplingBlock implements ITCBlocks, EntityBlock {
    public TCTakenokoBlock() {
        super(TCBlockCore.variant(Blocks.BAMBOO_SAPLING, false).mapColor(MapColor.WOOD).forceSolidOn().randomTicks().instabreak().noCollission().strength(1.0F).sound(SoundType.BAMBOO_SAPLING).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY).setId(TCBlockCore.TCBlockId("takenoko")));
    }

    @Override
    protected boolean canSurvive(BlockState p_48986_, LevelReader p_48987_, BlockPos p_48988_) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source) {
/*        if (level.isEmptyBlock(pos.above())) {
            this.growBamboo(level, pos);
        }
        Direction.stream().forEach(direction -> {
            BlockPos relPos = pos.relative(direction);
            if (level.getBlockState(relPos).is(this)) {
                this.growBamboo(level, relPos);
            }
        });*/
    }

    @Override
    public void growBamboo(Level level, BlockPos pos) {
        int height = 3 + level.getRandom().nextInt(3) + level.getRandom().nextInt(3);
        for (int i = 0; i < height; i++) {
            if (level.isEmptyBlock(pos.above(i)) || level.getBlockState(pos.above(i)).is(this)) {
                level.setBlock(pos.above(i), Blocks.BAMBOO.defaultBlockState().setValue(BambooStalkBlock.LEAVES, i < 3 ? BambooLeaves.NONE : i < 6 ? BambooLeaves.SMALL : BambooLeaves.LARGE), 3);
                level.explode(null, pos.getX(), pos.getY() + i, pos.getZ(), (float) Math.sqrt(2 * (2 - i / 4f)), Level.ExplosionInteraction.NONE);
            } else {
                break;
            }
        }
    }

    @Override
    public String getRegistryName() {
        return "takenoko";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Bamboo Sapling";
    }

    @Override
    public String getJaJPName() {
        return "たけのこ";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.simpleBlock(this, provider.models().withExistingParent(this.getRegistryName(), "bamboo_sapling").renderType("cutout"));
        provider.itemModels().getBuilder(this.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", "minecraft:block/bamboo_stage0");
    }

    @Override
    public String getBlockRenderType() {
        return "cutout";
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new TCTakenokoBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide() ? TCTakenokoBlockEntity::serverTick : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return EntityBlock.super.getListener(p_221121_, p_221122_);
    }
}
