package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class TCYukariDummyBlock extends Block implements ITCBlocks {
    public TCYukariDummyBlock() {
        super(Properties.of().strength(0f, 0f).lightLevel(state -> 7));
    }

    @Override
    public String getRegistryName() {
        return "yukaridummy";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return null;
    }

    @Override
    public String getJaJPName() {
        return "ゆかり式高性能爆弾";
    }

    @Override
    public String getEnUSName() {
        return "Yukari Creeper Bomb";
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {
            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
                super.appendHoverText(stack, level, components, tooltipFlag);
                components.add(Component.translatable("item.takumicraft.yukaridummy.desc"));
            }
        };
    }
}
