package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.TCZombieCreeper;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
/*        player.setItemSlot(EquipmentSlot.HEAD, getSuperArmor(Items.NETHERITE_HELMET));
        player.setItemSlot(EquipmentSlot.CHEST, getSuperArmor(Items.NETHERITE_CHESTPLATE));
        player.setItemSlot(EquipmentSlot.LEGS, getSuperArmor(Items.NETHERITE_LEGGINGS));
        player.setItemSlot(EquipmentSlot.FEET, getSuperArmor(Items.NETHERITE_BOOTS));
        player.addItem(getSuperWeapon(TCItemCore.CREEPER_SWORD));
        player.addItem(getSuperWeapon(TCItemCore.CREEPER_SHIELD));*/
        return super.use(level, player, hand);
    }

    private void summonCreeper(Player player) {
        if (!player.level().isClientSide()) {
            TCZombieCreeper creeper = (TCZombieCreeper) TCEntityCore.ZOMBIE.entityType().create(player.level());
            creeper.copyPosition(player);
            creeper.setItemSlot(EquipmentSlot.HEAD, getSuperArmor(Items.NETHERITE_HELMET));
            creeper.setItemSlot(EquipmentSlot.CHEST, getSuperArmor(Items.NETHERITE_CHESTPLATE));
            creeper.setItemSlot(EquipmentSlot.LEGS, getSuperArmor(Items.NETHERITE_LEGGINGS));
            creeper.setItemSlot(EquipmentSlot.FEET, getSuperArmor(Items.NETHERITE_BOOTS));
            creeper.setItemSlot(EquipmentSlot.MAINHAND, getSuperWeapon(TCItemCore.CREEPER_SWORD));
            creeper.setItemSlot(EquipmentSlot.OFFHAND, getSuperWeapon(TCItemCore.CREEPER_SHIELD));
            player.level().addFreshEntity(creeper);
        }
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
    public InteractionResult useOn(UseOnContext p_41427_) {
        if (p_41427_.getLevel().getBlockEntity(p_41427_.getClickedPos()) instanceof TCCreeperSuperBlockEntity superBlock) {
            p_41427_.getItemInHand().set(DataComponents.CUSTOM_NAME, Component.literal("LOCK_TEST"));
            superBlock.setLockCode("LOCK_TEST");
            superBlock.setChanged();
        }
        return super.useOn(p_41427_);
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
