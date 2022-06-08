package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Stream;

public class TCSearchTreeCore {

    public static final SearchRegistry.Key<ItemStack> CREEPER_NAMES = new SearchRegistry.Key<>();

    @OnlyIn(Dist.CLIENT)
    public static void register() {

        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (Item item : Registry.ITEM) {
            if (item instanceof TCSpawnEggItem) {
                item.fillItemCategory(CreativeModeTab.TAB_SEARCH, nonnulllist);
            }
        }

        Minecraft.getInstance().getSearchTreeManager().register(CREEPER_NAMES, (itemStacks) -> new FullTextSearchTree<>(itemStack -> {
            if (itemStack.getItem() instanceof TCSpawnEggItem item) {
                return Stream.of(item.getContext().getEnUSName(), item.getContext().getJaJPName(), item.getContext().getJaJPRead()).filter(s -> s != null && !s.isEmpty());
            }
            return itemStack.getTooltipLines(null, TooltipFlag.Default.NORMAL).stream().map((component) -> ChatFormatting.stripFormatting(component.getString()).trim()).filter((s) -> !s.isEmpty());
        }, (itemStack) -> Stream.of(Registry.ITEM.getKey(itemStack.getItem())), nonnulllist));
    }
}
