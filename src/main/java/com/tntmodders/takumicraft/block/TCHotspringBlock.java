package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCFluidCore;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TCHotspringBlock extends LiquidBlock implements ITCBlocks {

    public TCHotspringBlock() {
        super(() -> (FlowingFluid) TCFluidCore.HOTSPRING.get(), BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).replaceable().noCollission().randomTicks().strength(100f, 1000000f).lightLevel(state -> 7).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
    }

    @Override
    public String getRegistryName() {
        return "takumihotspring";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Hot Spring";
    }

    @Override
    public String getJaJPName() {
        return "匠温泉";
    }

    @Override
    public boolean hideOnCreativeTab() {
        return true;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().withExistingParent(provider.name(this), "block/water");
        provider.getVariantBuilder(this).partialState().setModels(new ConfiguredModel(model));
        provider.itemModels().withExistingParent(provider.name(this), "block/water");
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player p_299124_, LevelAccessor p_153772_, BlockPos p_153773_, BlockState p_153774_) {
        return super.pickupBlock(p_299124_, p_153772_, p_153773_, p_153774_);
    }
}
