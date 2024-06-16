package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.block.entity.TCCreeperSuperBlockEntity;
import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

public class TCCreeperSuperBlockItem extends TCBlockItem {
    public TCCreeperSuperBlockItem(Block block) {
        super(block, new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new TCBEWLRenderer();
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(stack, p_41405_, p_41406_, p_41407_, p_41408_);
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("replace", false);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if (p_41433_.isCreative() && p_41433_.isShiftKeyDown()) {
            ItemStack stack = p_41433_.getItemInHand(p_41434_);
            CompoundTag tag = new CompoundTag();
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
            }
            boolean replace = false;
            if (tag.contains("replace")) {
                replace = tag.getBoolean("replace");
            }
            tag.putBoolean("replace", !replace);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return InteractionResultHolder.success(stack);
        }
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        boolean replace = false;
        if (stack.has(DataComponents.CUSTOM_DATA) && stack.get(DataComponents.CUSTOM_DATA).copyTag().contains("replace")) {
            replace = stack.get(DataComponents.CUSTOM_DATA).copyTag().getBoolean("replace");
        }
        if (replace && !context.getPlayer().isShiftKeyDown()) {
            if (context.getPlayer().isCreative()) {
                BlockState oldState = context.getLevel().getBlockState(context.getClickedPos());
                if (oldState.getBlock() != TCBlockCore.SUPER_BLOCK && oldState.canEntityDestroy(context.getLevel(), context.getClickedPos(), context.getPlayer())) {
                    context.getLevel().setBlock(context.getClickedPos(), TCBlockCore.SUPER_BLOCK.defaultBlockState(), 18);
                    if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TCCreeperSuperBlockEntity superBlock) {
                        superBlock.setState(oldState);
                        superBlock.setChanged();
                    }
                    return InteractionResult.CONSUME_PARTIAL;
                }
            } else {
                CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                tag.putBoolean("replace", false);
                ItemStack newStack = stack;
                newStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                stack = newStack;
            }
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void appendHoverText(ItemStack p_40572_, TooltipContext p_327780_, List<Component> components, TooltipFlag p_40575_) {
        super.appendHoverText(p_40572_, p_327780_, components, p_40575_);
        boolean replace = false;
        if (p_40572_.has(DataComponents.CUSTOM_DATA) && p_40572_.get(DataComponents.CUSTOM_DATA).copyTag().contains("replace")) {
            replace = p_40572_.get(DataComponents.CUSTOM_DATA).copyTag().getBoolean("replace");
        }
        if (replace) {
            components.add(Component.translatable("block.takumicraft.takumiblock.mode.replace").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC, ChatFormatting.BOLD));
            components.add(Component.empty());
            components.add(Component.translatable("block.takumicraft.takumiblock.desc.replace"));
        } else {
            components.add(Component.translatable("block.takumicraft.takumiblock.mode.place").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC, ChatFormatting.BOLD));
        }
        components.add(Component.empty());
        components.add(Component.translatable("block.takumicraft.takumiblock.desc.01"));
        components.add(Component.translatable("block.takumicraft.takumiblock.desc.02"));
        components.add(Component.translatable("block.takumicraft.takumiblock.desc.03"));
        components.add(Component.empty());
        components.add(Component.translatable("block.takumicraft.takumiblock.desc.toggle").withStyle(ChatFormatting.GRAY));
    }
}
