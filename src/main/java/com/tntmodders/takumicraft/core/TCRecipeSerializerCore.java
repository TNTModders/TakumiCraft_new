package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.item.crafting.TCCreeperShieldDecorationRecipe;
import com.tntmodders.takumicraft.item.crafting.TCCreeperSwordUpgradeRecipe;
import com.tntmodders.takumicraft.item.crafting.TCSaberRecipe;
import com.tntmodders.takumicraft.item.crafting.TCTippedCreeperArrowRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class TCRecipeSerializerCore {
    public static RecipeSerializer CREEPER_SHIELD_DECO = new CustomRecipe.Serializer<>(TCCreeperShieldDecorationRecipe::new);
    public static RecipeSerializer CREEPER_ARROW_TIP = new CustomRecipe.Serializer<>(TCTippedCreeperArrowRecipe::new);
    public static RecipeSerializer CREEPER_SWORD_UPGRADE = new TCCreeperSwordUpgradeRecipe.Serializer();
    public static RecipeSerializer SABER = new TCSaberRecipe.Serializer();

    public static void register(final RegisterEvent event) {
        registerRecipes(event, "creepershield_decoration", CREEPER_SHIELD_DECO);
        registerRecipes(event, "creeperarrow_tipped", CREEPER_ARROW_TIP);
        registerRecipes(event, "creepersword_upgrade", CREEPER_SWORD_UPGRADE);
        registerRecipes(event, "saber", SABER);
    }

    private static void registerRecipes(final RegisterEvent event, String name, RecipeSerializer<?> serializer) {
        event.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), ResourceLocation.tryBuild(TakumiCraftCore.MODID, name), () -> serializer);
    }
}
