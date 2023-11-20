package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;

public class TCCreeperShieldItem extends ShieldItem implements ITCItems, ITCTranslator, ITCRecipe {

    public TCCreeperShieldItem() {
        super(new Item.Properties().durability(256));
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.EXPLOSIVE_SHIELDS, Tags.Items.TOOLS_SHIELDS);
    }

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
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SP;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Shield";
    }

    @Override
    public String getJaJPName() {
        return "匠式防盾";
    }

    @Override
    public String getRegistryName() {
        return "creepershield";
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(TCItemCore.ELEMENTCORE_FIRE, TCItemCore.ELEMENTCORE_NORMAL, TCItemCore.ELEMENTCORE_WATER, TCItemCore.ELEMENTCORE_GRASS, TCItemCore.ELEMENTCORE_WIND, TCItemCore.ELEMENTCORE_GROUND),
                        Ingredient.of(Items.SHIELD), Ingredient.of(TCBlockCore.CREEPER_BOMB)
                        , RecipeCategory.COMBAT, TCItemCore.CREEPER_SHIELD).unlocks("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB))
                .save(consumer, "creepershield_smithing");
    }
}
