package com.tntmodders.takumicraft.world.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

public class TCTier {
    public static final Tier TKM_SABER = new ForgeTier(5, 4096, 21.0F, 16.0F, 31,
            Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.of(TCBlockCore.CREEPER_BOMB));
}
