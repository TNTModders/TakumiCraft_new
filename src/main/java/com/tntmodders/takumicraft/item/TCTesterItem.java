package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class TCTesterItem extends Item implements ITCItems, ITCTranslator {

    private final AttributeModifier defaultModifier;

    public TCTesterItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 19f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));

        this.defaultModifier = new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 99, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean hideOnCreativeTab() {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
/*        p_41433_.setItemSlot(EquipmentSlot.HEAD, getSuperArmor(Items.NETHERITE_HELMET));
        p_41433_.setItemSlot(EquipmentSlot.CHEST, getSuperArmor(Items.NETHERITE_CHESTPLATE));
        p_41433_.setItemSlot(EquipmentSlot.LEGS, getSuperArmor(Items.NETHERITE_LEGGINGS));
        p_41433_.setItemSlot(EquipmentSlot.FEET, getSuperArmor(Items.NETHERITE_BOOTS));
        p_41433_.addItem(getSuperWeapon(TCItemCore.CREEPER_SWORD));
        p_41433_.addItem(getSuperWeapon(TCItemCore.CREEPER_SHIELD));*/
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    private ItemStack getSuperArmor(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.enchant(Enchantments.BLAST_PROTECTION, 2);
        stack.enchant(Enchantments.PROTECTION, 10);
        stack.enchant(Enchantments.FIRE_PROTECTION, 5);
        stack.enchant(Enchantments.FEATHER_FALLING, 5);
        stack.enchant(Enchantments.UNBREAKING, 5);
        return stack;
    }

    private ItemStack getSuperWeapon(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.enchant(TCEnchantmentCore.ANTI_POWERED, 10);
        stack.enchant(Enchantments.UNBREAKING, 5);
        return stack;
    }

    @Override
    public ItemAttributeModifiers getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? ItemAttributeModifiers.EMPTY.withModifierAdded(Attributes.ATTACK_DAMAGE, this.defaultModifier, EquipmentSlotGroup.MAINHAND) : super.getDefaultAttributeModifiers();
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack p_41398_, Player p_41399_, LivingEntity p_41400_, InteractionHand p_41401_) {
        if (!p_41399_.level().isClientSide() && p_41400_ instanceof Creeper creeper) {
            creeper.ignite();
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(p_41398_, p_41399_, p_41400_, p_41401_);
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        if (!p_41404_.isEnchanted()) {
            p_41404_.enchant(TCEnchantmentCore.ANTI_POWERED, 1);
            p_41404_.enchant(TCEnchantmentCore.MINESWEEPER, 1);
        }
    }

    @Override
    public String getEnUSName() {
        return "Creeper Rod";
    }

    @Override
    public String getJaJPName() {
        return "匠式錫杖";
    }

    @Override
    public String getRegistryName() {
        return "tester";
    }
}
