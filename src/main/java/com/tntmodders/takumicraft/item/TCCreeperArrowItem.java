package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.projectile.TCCreeperArrow;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.List;

public class TCCreeperArrowItem extends ArrowItem implements ITCItems, ITCTranslator, ITCRecipe {
    public TCCreeperArrowItem() {
        super(new Item.Properties());
    }

    @Override
    public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        TCCreeperArrow arrow = new TCCreeperArrow(p_40513_, p_40515_, p_40514_);
        arrow.setEffectsFromItem(p_40514_);
        return arrow;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Arrow";
    }

    @Override
    public String getJaJPName() {
        return "匠式爆裂矢";
    }

    @Override
    public String getRegistryName() {
        return "creeperarrow";
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.CREEPER_ARROWS);
    }


    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TCItemCore.CREEPER_ARROW, 1)
                .define('#', Items.STICK)
                .define('X', Items.CREEPER_HEAD)
                .define('Y', Items.FEATHER)
                .pattern("X").pattern("#").pattern("Y")
                .unlockedBy("has_feather", TCRecipeProvider.hasItem(Items.CREEPER_HEAD)));

    }
}
