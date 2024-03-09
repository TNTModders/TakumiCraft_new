package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class TCTippedCreeperArrowItem extends TCCreeperArrowItem {

    public static final MobEffectInstance WITHER = new MobEffectInstance(MobEffects.WITHER, 800, 1);

    public TCTippedCreeperArrowItem() {
        super();
    }

    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.POISON);
    }

    @Override
    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        PotionUtils.addPotionTooltip(p_43359_, p_43361_, 0.125F, p_43360_ == null ? 20.0F : p_43360_.tickRateManager().tickrate());
    }

    @Override
    public boolean isSPOnCreativeTab() {
        return true;
    }

    @Override
    public void performSPOnCreativeTab(CreativeModeTab.Output output) {
        ForgeRegistries.POTIONS.forEach(potion -> {
            if (potion != Potions.EMPTY) {
                ItemStack stack = PotionUtils.setPotion(new ItemStack(this), potion);
                output.accept(stack);
            }
        });
        output.accept(PotionUtils.setCustomEffects(new ItemStack(this), List.of(WITHER)));
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
        String base = PotionUtils.getPotion(p_41458_).getName("item.minecraft.tipped_arrow.effect.");
        String parts = Component.translatable(base).getString().replaceAll("の矢|な矢", "").replaceAll("ありふれた矢", "平凡").replaceAll("タートルマスター", "亀").replaceAll("クラフト不可能な効能付き", "???").replaceAll("Arrow", "Creeper Arrow");
        return Component.translatable(this.getDescriptionId()).append("[" + parts + "]");
    }

    @Override
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SP;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
    }
}
