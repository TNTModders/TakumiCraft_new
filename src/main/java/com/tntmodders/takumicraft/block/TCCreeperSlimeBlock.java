package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Function;

public class TCCreeperSlimeBlock extends SlimeBlock implements ITCBlocks, ITCRecipe {
    public TCCreeperSlimeBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion().explosionResistance(1000000f).setId(TCBlockCore.TCBlockId("creeperslimeblock")));
    }


    @Override
    public void updateEntityMovementAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityMovementAfterFallOn(level, entity);
        } else {
            this.bounceUp(entity);
        }
    }

    private void bounceUp(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double d0 = entity instanceof LivingEntity ? 1.0 : 0.8;
            double y = Math.min(10, -vec3.y * d0 * 2);
            entity.setDeltaMovement(vec3.x, y, vec3.z);
            if (y > 1) {
                TCExplosionUtils.createExplosion(entity.level(), entity, entity.getOnPos(), 0f);
            }
        }
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        return true;
    }

    @Override
    public String getRegistryName() {
        return "creeperslimeblock";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_SLIME).define('#', TCBlockCore.CREEPER_BOMB).define('B', Blocks.SLIME_BLOCK).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Slime Block";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質スライムブロック";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile blockModel = provider.models().withExistingParent(provider.name(this), "takumicraft:block/template_creeperslimeblock").texture("texture", provider.blockTexture(this)).texture("inside", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, provider.name(this) + "_inside")));
        provider.getVariantBuilder(this).partialState().addModels(new ConfiguredModel(blockModel));
        provider.itemModels().withExistingParent(provider.name(this), provider.blockTexture(this));
    }
}
