package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class TCCreeperBombBlock extends AbstractTCBombBlock implements ITCRecipe {
    public static final ToIntFunction<BlockState> LIGHT_TCBOMB = (state) -> 2;

    public TCCreeperBombBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE).strength(0.1f, 0f).color(MaterialColor.COLOR_LIGHT_GREEN).lightLevel(LIGHT_TCBOMB), "creeperbomb");
    }

    @Override
    public float getPower() {
        return 5f;
    }

    @Override
    public String getEnUSName() {
        return "Creeper Bomb";
    }

    @Override
    public String getJaJPName() {
        return "匠式高性能爆弾";
    }

    @Override
    public String getRegistryName() {
        return "creeperbomb";
    }

    @Override
    public Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getBlockLoot(@NotNull Block block) {
        return () -> new BlockLoot() {

            @Override
            protected Iterable<Block> getKnownBlocks() {
                return List.of(block);
            }

            @Override
            protected void addTables() {
                this.dropSelf(block);
            }
        };
    }

    @Override
    public NonNullList<RecipeBuilder> addRecipes() {
        return NonNullList.of(ShapedRecipeBuilder.shaped(TCBlockCore.CREEPER_BOMB)
                .define('#', Items.CREEPER_HEAD)
                .pattern(" # ")
                .pattern("###")
                .pattern(" # ")
                .unlockedBy("has_gunpowder", TCRecipeProvider.hasItem(Items.CREEPER_HEAD)));
    }
}
