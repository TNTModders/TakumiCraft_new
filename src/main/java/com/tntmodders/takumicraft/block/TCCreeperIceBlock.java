package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TCCreeperIceBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {
    public TCCreeperIceBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).friction(0.989F).strength(0.5F).sound(SoundType.GLASS).noOcclusion().isRedstoneConductor((p_61036_, p_61037_, p_61038_) -> false).setId(TCBlockCore.TCBlockId("creeperice")));
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return entity instanceof LivingEntity ? 1.075f : entity instanceof ItemEntity ? 1f : super.getFriction(state, level, pos, entity);
    }

    @Override
    public String getRegistryName() {
        return "creeperice";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Ice";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質氷";
    }

    @Override
    public void drop(Block block, TCBlockLoot loot) {
        loot.dropWhenSilkTouch(block);
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS,
                        TCBlockCore.CREEPER_ICE, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', Blocks.BLUE_ICE)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }
}
