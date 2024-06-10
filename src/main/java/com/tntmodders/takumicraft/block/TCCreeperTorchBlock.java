package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.item.TCCreeperTorchBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.function.Supplier;

public class TCCreeperTorchBlock extends TorchBlock implements ITCBlocks {
    public TCCreeperTorchBlock() {
        super(ParticleTypes.SMOKE, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(p_50886_ -> 15).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).explosionResistance(10000000f));
    }

    @Override
    public String getRegistryName() {
        return "creepertorch";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCCreeperTorchBlockItem(TCBlockCore.CREEPER_TORCH, TCBlockCore.CREEPER_TORCH_WALL);
    }

    @Override
    public String getEnUSName() {
        return "Creeper Torch";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質松明";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        provider.simpleBlock(this, provider.models().withExistingParent(provider.key(this).toString(), "block/torch").texture("torch", provider.blockTexture(this)).renderType("cutout"));
        ResourceLocation name = new ResourceLocation(TakumiCraftCore.MODID, this.getRegistryName());
        provider.itemModels().singleTexture(name.getPath(), provider.mcLoc("item/generated"), "layer0", new ResourceLocation(name.getNamespace(), "block/" + name.getPath()));
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.WALL_POST_OVERRIDE);
    }
}
