package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class TCCreeperBombBlock extends AbstractTCBombBlock implements ITCRecipe {
    public static final ToIntFunction<BlockState> LIGHT_TCBOMB = state -> 2;

    public TCCreeperBombBlock() {
        super(BlockBehaviour.Properties.of().strength(0.1f, 0f).mapColor(MapColor.COLOR_LIGHT_GREEN).lightLevel(LIGHT_TCBOMB), "creeperbomb");
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {

            @Override
            public void appendHoverText(ItemStack p_40572_, TooltipContext p_327780_, List<Component> components, TooltipFlag p_40575_) {
                super.appendHoverText(p_40572_, p_327780_, components, p_40575_);
                components.add(Component.translatable("item.takumicraft.creeperbomb.desc"));
            }
        };
    }

    @Override
    public float getPower() {
        return 5f;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Bomb";
    }

    @Override
    public String getJaJPName() {
        return "匠式高性能爆弾";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, false);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                        TCBlockCore.CREEPER_BOMB)
                .define('#', Items.CREEPER_HEAD)
                .pattern(" # ")
                .pattern("###")
                .pattern(" # ")
                .unlockedBy("has_gunpowder", TCRecipeProvider.hasItem(Items.CREEPER_HEAD)));
    }
}
