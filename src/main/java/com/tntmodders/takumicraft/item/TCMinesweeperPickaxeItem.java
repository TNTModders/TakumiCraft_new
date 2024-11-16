package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TCMinesweeperPickaxeItem extends PickaxeItem implements ITCItems, ITCTranslator, ITCRecipe {
    public TCMinesweeperPickaxeItem() {
        super(ToolMaterial.IRON, 1, -2.8f, new Properties());
    }

    @Override
    public float getDestroySpeed(ItemStack p_41004_, BlockState p_41005_) {
        return super.getDestroySpeed(p_41004_, p_41005_) * 3f;
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        if (!p_41404_.isEnchanted()) {
            p_41404_.enchant(p_41405_.holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.MINESWEEPER), 1);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, components, tooltipFlag);
        components.add(Component.translatable("item.takumicraft.minesweeper_tool.desc"));
    }

    @Override
    public String getEnUSName() {
        return "Minesweeper Pixckaxe";
    }

    @Override
    public String getJaJPName() {
        return "対地雷ピッケル";
    }

    @Override
    public String getRegistryName() {
        return "minesweeper_pickaxe";
    }

    @Override
    public void registerItemModel(TCItemModelProvider provider) {
        provider.singleItem(this, "handheld");
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.MINESWEEPER_TOOLS, ItemTags.PICKAXES);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(TCItemCore.ELEMENTCORE_FIRE, TCItemCore.ELEMENTCORE_NORMAL, TCItemCore.ELEMENTCORE_WATER, TCItemCore.ELEMENTCORE_GRASS, TCItemCore.ELEMENTCORE_WIND, TCItemCore.ELEMENTCORE_GROUND),
                        Ingredient.of(Items.IRON_PICKAXE), Ingredient.of(TCBlockCore.CREEPER_BOMB)
                        , RecipeCategory.TOOLS, TCItemCore.MINESWEEPER_PICKAXE).unlocks("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB))
                .save(consumer, this.getRegistryName() + "_smithing");
    }
}
