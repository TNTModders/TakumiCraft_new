package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.*;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class TCCreeperBowItem extends BowItem implements ITCItems, ITCTranslator, ITCRecipe {
    public static final Predicate<ItemStack> CREEPER_ARROW_ONLY = p_43017_ -> p_43017_.is(TCItemCore.CREEPER_ARROWS);
    public static final Predicate<ItemStack> ARROW_AND_CARROW = p_43017_ -> p_43017_.is(TCItemCore.CREEPER_ARROWS) || p_43017_.is(ItemTags.ARROWS);

    public TCCreeperBowItem() {
        super(new Item.Properties().durability(256));
    }

    @Override
    protected Projectile createProjectile(Level p_333069_, LivingEntity p_334736_, ItemStack p_333680_, ItemStack p_329118_, boolean p_336242_) {
        TCCreeperArrowItem arrowitem = p_329118_.getItem() instanceof TCCreeperArrowItem arrowitem1 ? arrowitem1 : (TCCreeperArrowItem) TCItemCore.CREEPER_ARROW;
        AbstractArrow abstractarrow = arrowitem.createArrow(p_333069_, p_329118_, p_334736_, p_333680_);
        abstractarrow = customArrow(abstractarrow);
        if (p_336242_) {
            abstractarrow.setCritArrow(true);
        }
        return abstractarrow;
    }

    @Override
    protected void shootProjectile(LivingEntity p_329327_, Projectile p_335269_, int p_331005_, float p_332731_, float p_332848_, float p_332058_, @Nullable LivingEntity p_335061_) {
        super.shootProjectile(p_329327_, p_335269_, p_331005_, p_332731_, p_332848_ * 1.25f, p_332058_, p_335061_);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_AND_CARROW;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, TooltipContext p_333372_, List<Component> components, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, components, p_41424_);
        components.add(Component.translatable("item.takumicraft.creeperbow.desc"));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Bow";
    }

    @Override
    public String getJaJPName() {
        return "匠式強弓";
    }

    @Override
    public String getRegistryName() {
        return "creeperbow";
    }

    @Override
    public void registerItemModel(TCItemModelProvider provider) {
        ResourceLocation[] loc = new ResourceLocation[3];
        String name;
        for (int i = 0; i < 3; i++) {
            name = this.getRegistryName() + "_pulling_" + i;
            loc[i] = ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.getFolder() + "/" + name);
            provider.withExistingParent(name, provider.mcLoc("bow_pulling_" + i)).texture("layer0", loc[i]);
        }
        name = this.getRegistryName();
        provider.withExistingParent(name, provider.mcLoc("bow")).texture("layer0", ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.getFolder() + "/" + name))
                .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1f).model(new ModelFile.ExistingModelFile(loc[0], provider.existingFileHelper)).end()
                .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1f).predicate(ResourceLocation.withDefaultNamespace("pull"), 0.65f).model(new ModelFile.ExistingModelFile(loc[1], provider.existingFileHelper)).end()
                .override().predicate(ResourceLocation.withDefaultNamespace("pulling"), 1f).predicate(ResourceLocation.withDefaultNamespace("pull"), 0.9f).model(new ModelFile.ExistingModelFile(loc[2], provider.existingFileHelper));
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(TCItemCore.ELEMENTCORE_FIRE, TCItemCore.ELEMENTCORE_NORMAL, TCItemCore.ELEMENTCORE_WATER, TCItemCore.ELEMENTCORE_GRASS, TCItemCore.ELEMENTCORE_WIND, TCItemCore.ELEMENTCORE_GROUND),
                        Ingredient.of(Items.BOW), Ingredient.of(TCBlockCore.CREEPER_BOMB)
                        , RecipeCategory.COMBAT, TCItemCore.CREEPER_BOW).unlocks("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB))
                .save(consumer, "creeperbow_smithing");
    }

    @Override
    public boolean isFoil(ItemStack p_41453_) {
        return true;
    }
}
