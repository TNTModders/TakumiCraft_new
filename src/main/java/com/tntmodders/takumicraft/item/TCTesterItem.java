package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCTranslator;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class TCTesterItem extends Item implements ITCItems, ITCTranslator {

    private final AttributeModifier defaultModifier;

    public TCTesterItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 19f, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()).setId(TCItemCore.TCItemId("tester")));

        this.defaultModifier = new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 99, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean hideOnCreativeTab() {
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return super.use(level, player, hand);
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
            superBlock.setLockCode(p_41427_.getLevel(), "LOCK_TEST");
            superBlock.setChanged();
        }
        return super.useOn(p_41427_);
    }

    @Override
    public void inventoryTick(ItemStack p_41404_, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, p_41405_, p_41406_, p_41407_, p_41408_);
        if (!p_41404_.isEnchanted()) {
            p_41404_.enchant(p_41405_.holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.ANTI_POWERED), 1);
            p_41404_.enchant(p_41405_.holderLookup(Registries.ENCHANTMENT).getOrThrow(TCEnchantmentCore.MINESWEEPER), 1);
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
