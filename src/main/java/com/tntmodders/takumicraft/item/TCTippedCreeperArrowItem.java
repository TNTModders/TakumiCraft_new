package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCCreativeModeTabCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.TCItemModelProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class TCTippedCreeperArrowItem extends TCCreeperArrowItem {
    public static final MobEffectInstance WITHER = new MobEffectInstance(MobEffects.WITHER, 800, 1);

    public TCTippedCreeperArrowItem() {
        super();
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack itemstack = super.getDefaultInstance();
        itemstack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
        return itemstack;
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, Item.TooltipContext p_328767_, List<Component> p_43361_, TooltipFlag p_43362_) {
        PotionContents potioncontents = p_43359_.get(DataComponents.POTION_CONTENTS);
        if (potioncontents != null) {
            potioncontents.addPotionTooltip(p_43361_::add, 0.125F, p_328767_.tickRate());
        }
    }

    @Override
    public boolean isSPOnCreativeTab() {
        return true;
    }

    @Override
    public void performSPOnCreativeTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        params.holders().lookup(Registries.POTION).ifPresent(lookup -> TCCreativeModeTabCore.generatePotionEffectTypes(output, lookup, TCItemCore.TIPPED_CREEPER_ARROW, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, params.enabledFeatures()));
        ItemStack stack = super.getDefaultInstance();
        stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withEffectAdded(WITHER));
        output.accept(stack);
    }

    @Override
    public String getEnUSName() {
        return "Tipped Creeper Arrow";
    }

    @Override
    public String getJaJPName() {
        return "匠式爆裂薬矢";
    }

    @Override
    public String getRegistryName() {
        return "tipped_creeperarrow";
    }

    @Override
    public Component getName(ItemStack p_41458_) {
        String base = Potion.getName(p_41458_.get(DataComponents.POTION_CONTENTS).potion(), "item.minecraft.tipped_arrow.effect.");
        String parts = Component.translatable(base).getString().replaceAll("の矢|な矢", "").replaceAll("ありふれた矢", "平凡").replaceAll("タートルマスター", "亀").replaceAll("クラフト不可能な効能付き", "???").replaceAll("Arrow", "Creeper Arrow");
        return Component.translatable(this.getDescriptionId()).append("[" + parts + "]");
    }

    @Override
    public void registerItemModel(TCItemModelProvider provider) {
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
    }
}
