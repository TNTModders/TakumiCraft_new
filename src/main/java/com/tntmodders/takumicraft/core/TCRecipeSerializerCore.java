package com.tntmodders.takumicraft.core;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.item.crafting.CreeperShieldDecorationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class TCRecipeSerializerCore {
    public static RecipeSerializer CREEPER_SHIELD_DECO = new SimpleCraftingRecipeSerializer<>(CreeperShieldDecorationRecipe::new);

    public static void register(final RegisterEvent event) {
        event.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), new ResourceLocation(TakumiCraftCore.MODID, "creepershield_decoration"), () -> CREEPER_SHIELD_DECO);
    }
}
