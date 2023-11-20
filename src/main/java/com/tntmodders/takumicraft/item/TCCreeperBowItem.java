package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class TCCreeperBowItem extends BowItem implements ITCItems, ITCTranslator, ITCRecipe {
    public static final Predicate<ItemStack> CREEPER_ARROW_ONLY = p_43017_ -> p_43017_.is(TCItemCore.CREEPER_ARROWS);
    public static final Predicate<ItemStack> ARROW_AND_CARROW = p_43017_ -> p_43017_.is(TCItemCore.CREEPER_ARROWS) || p_43017_.is(ItemTags.ARROWS);

    public TCCreeperBowItem() {
        super(new Item.Properties().durability(256));
    }

    private void shootArrow(ItemStack bow, Level level, Player player, AbstractArrow abstractarrow, float power, ItemStack arrow) {
        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 5.0F, 1.0F);
        if (power == 1.0F) {
            abstractarrow.setCritArrow(true);
        }

        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow);
        if (j > 0) {
            abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double) j * 0.5D + 0.5D);
        }

        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow);
        if (k > 0) {
            abstractarrow.setKnockback(k);
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bow) > 0) {
            abstractarrow.setSecondsOnFire(100);
        }

        bow.hurtAndBreak(1, player, p_296888_ -> p_296888_.broadcastBreakEvent(player.getUsedItemHand()));
        abstractarrow.pickup = AbstractArrow.Pickup.DISALLOWED;

        level.addFreshEntity(abstractarrow);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int p_40670_) {
        if (livingEntity instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack itemstack = this.getProjectile(player, stack, CREEPER_ARROW_ONLY);
            if (!flag && (itemstack.isEmpty() || !(itemstack.getItem() instanceof TCCreeperArrowItem))) {
                this.releaseUsingNormal(stack, level, livingEntity, p_40670_);
                return;
            }
            int i = this.getUseDuration(stack) - p_40670_;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, level, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(TCItemCore.CREEPER_ARROW);
                }

                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || itemstack.getItem() instanceof ArrowItem && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, stack, player);
                    if (!level.isClientSide) {
                        TCCreeperArrowItem arrowitem = (TCCreeperArrowItem) (itemstack.getItem() instanceof TCCreeperArrowItem ? itemstack.getItem() : TCItemCore.CREEPER_ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player);
                        abstractarrow = customArrow(abstractarrow);
                        this.shootArrow(stack, level, player, abstractarrow, f, itemstack);
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    private void releaseUsingNormal(ItemStack p_40667_, Level p_40668_, LivingEntity p_40669_, int p_40670_) {
        if (p_40669_ instanceof Player player) {
            boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, p_40667_) > 0;
            ItemStack itemstack = this.getProjectile(player, p_40667_, super.getAllSupportedProjectiles());

            int i = this.getUseDuration(p_40667_) - p_40670_;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(p_40667_, p_40668_, player, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    boolean flag1 = player.getAbilities().instabuild || itemstack.getItem() instanceof ArrowItem && ((ArrowItem) itemstack.getItem()).isInfinite(itemstack, p_40667_, player);
                    if (!p_40668_.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(p_40668_, itemstack, player);
                        abstractarrow = customArrow(abstractarrow);
                        this.shootArrow(p_40667_, p_40668_, player, abstractarrow, f, itemstack);
                    }

                    p_40668_.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (p_40668_.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            player.getInventory().removeItem(itemstack);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    private ItemStack getProjectile(Player player, ItemStack bow, Predicate<ItemStack> predicate) {
        if (!(bow.getItem() instanceof ProjectileWeaponItem)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(player, predicate);
            if (!itemstack.isEmpty()) {
                return net.minecraftforge.common.ForgeHooks.getProjectile(player, bow, itemstack);
            } else {

                for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                    ItemStack itemstack1 = player.getInventory().getItem(i);
                    if (predicate.test(itemstack1)) {
                        return net.minecraftforge.common.ForgeHooks.getProjectile(player, bow, itemstack1);
                    }
                }

                return net.minecraftforge.common.ForgeHooks.getProjectile(player, bow, player.getAbilities().instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_AND_CARROW;
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
    public EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.BOW;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(TCItemCore.ELEMENTCORE_FIRE, TCItemCore.ELEMENTCORE_NORMAL, TCItemCore.ELEMENTCORE_WATER, TCItemCore.ELEMENTCORE_GRASS, TCItemCore.ELEMENTCORE_WIND, TCItemCore.ELEMENTCORE_GROUND),
                        Ingredient.of(Items.BOW), Ingredient.of(TCBlockCore.CREEPER_BOMB)
                        , RecipeCategory.COMBAT, TCItemCore.CREEPER_BOW).unlocks("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB))
                .save(consumer, "creeperbow_smithing");
    }
}
