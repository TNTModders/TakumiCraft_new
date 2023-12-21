package com.tntmodders.takumicraft.block;

import com.ibm.icu.impl.Pair;
import com.tntmodders.takumicraft.block.entity.TCCreeperBedBlockEntity;
import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCLanguageProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TCCreeperBedBlock extends BedBlock implements ITCBlocks, ITCRecipe {
    private final DyeColor color;

    public TCCreeperBedBlock(DyeColor colorIn) {
        super(colorIn, BlockBehaviour.Properties.of().mapColor(p_284863_ -> p_284863_.getValue(BedBlock.PART) == BedPart.FOOT ? colorIn.getMapColor() : MapColor.WOOL).sound(SoundType.WOOD).strength(0.2F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY).strength(5.0F, 6.0F).explosionResistance(1000000f).lightLevel(state -> state.getBlock() instanceof TCSuperCreeperBedBlock ? 15 : 0));
        this.color = colorIn;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_BED_MAP.get(this.color))
                .define('#', TCBlockCore.CREEPER_PLANKS)
                .define('B', TCBlockCore.CREEPER_WOOL_MAP.get(this.color))
                .pattern("BBB")
                .pattern("###")
                .unlockedBy("has_creeperplanks", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_PLANKS))
                .group("creeperbed"));
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.BED;
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, true);
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.add(block, loot.createSinglePropConditionTable(block, BedBlock.PART, BedPart.HEAD));
    }

    @Override
    public String getRegistryName() {
        return "creeperbed_" + this.color.getName();
    }

    @Override
    public String getEnUSName() {
        return TCLanguageProvider.TCEnUSLanguageProvider.TC_ENUS_LANGMAP.get("takumicraft.color." + this.color.getName()) + " Creeper Bed";
    }

    @Override
    public String getJaJPName() {
        return TCLanguageProvider.TCJaJPLanguageProvider.TC_JAJP_LANGMAP.get("takumicraft.color." + this.color.getName()) + "匠式硬質寝具";
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_152175_, BlockState p_152176_) {
        return new TCCreeperBedBlockEntity(p_152175_, p_152176_, this.color, false);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, TCBlockCore.CREEPER_BED);
    }

    @Override
    public List<Pair<TagKey<Block>, TagKey<Item>>> getItemTags() {
        return List.of(Pair.of(TCBlockCore.CREEPER_BED, TCItemCore.CREEPER_BED));
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {
            @Override
            public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                consumer.accept(new IClientItemExtensions() {
                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return new TCBEWLRenderer();
                    }
                });
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        };
    }
}
