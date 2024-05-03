package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.item.TCSpawnEggItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Stream;

public class TCSearchTreeCore {

    public static final SearchRegistry.Key<ItemStack> CREEPER_NAMES = new SearchRegistry.Key<>();
    public static final SearchRegistry REGISTRY = Minecraft.getInstance().getSearchTreeManager();

    @OnlyIn(Dist.CLIENT)
    public static void register() {

        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for (Item item : TCItemCore.ITEMS) {
            if (item instanceof TCSpawnEggItem) {
                nonnulllist.add(item.getDefaultInstance());
            }
        }

        REGISTRY.register(CREEPER_NAMES, itemStacks -> new FullTextSearchTree<>(itemStack -> {
            if (itemStack.getItem() instanceof TCSpawnEggItem item) {
                return Stream.of(item.getContext().getEnUSName(), item.getContext().getJaJPName(), item.getContext().getJaJPRead()).filter(s -> s != null && !s.isEmpty());
            }
            return itemStack.getTooltipLines(null, null, TooltipFlag.Default.NORMAL).stream().map(component -> ChatFormatting.stripFormatting(component.getString()).trim()).filter(s -> !s.isEmpty());
        }, itemStack -> Stream.of(ForgeRegistries.ITEMS.getKey(itemStack.getItem())), nonnulllist));

        REGISTRY.populate(CREEPER_NAMES, nonnulllist);
    }
}