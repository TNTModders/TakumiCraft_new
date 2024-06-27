package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;

public class TCAltarBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {

    public TCAltarBlock() {
        super(Properties.of().strength(0.5f, 1000000f).lightLevel(state -> 7));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState p_60503_, Level level, BlockPos pos, Player p_60506_, BlockHitResult p_60508_) {
        if (!level.isClientSide()) {
            if (level.getBlockState(pos.below()).is(TCBlockCore.CREEPER_BOMB) && summonKing(level, pos)) {
                TCExplosionUtils.createExplosion(level, null, pos, 0f);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 3);
                return InteractionResult.SUCCESS;
            } else {
                if (summonHigh(level, pos)) {
                    TCExplosionUtils.createExplosion(level, null, pos, 0f);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useWithoutItem(p_60503_, level, pos, p_60506_, p_60508_);
    }

    private boolean summonHigh(Level level, BlockPos pos) {
        AbstractTCCreeper.TCCreeperContext context = TCEntityCore.ALTAR_LIST.get(level.getRandom().nextInt(TCEntityCore.ALTAR_LIST.size()));
        var entity = context.entityType().create(level);
        if (entity instanceof AbstractTCCreeper creeper) {
            creeper.setPos(pos.getCenter());
            return level.addFreshEntity(creeper);
        }
        return false;
    }

    private boolean summonKing(Level level, BlockPos pos) {
        AbstractTCCreeper.TCCreeperContext context = TCEntityCore.KING;
        var entity = context.entityType().create(level);
        if (entity instanceof AbstractTCCreeper creeper) {
            creeper.setPos(pos.getCenter());
            return level.addFreshEntity(creeper);
        }
        return false;
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        String name = provider.name(this);
        ModelFile model = provider.models().cubeBottomTop(name, provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, name + "_side")), provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, name + "_bottom")), provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, name + "_top")));
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }

    @Override
    public String getRegistryName() {
        return "takumialtar";
    }

    @Override
    public String getEnUSName() {
        return "Creeper Altar";
    }

    @Override
    public String getJaJPName() {
        return "王匠の祭壇";
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, itemLike).define('#', TCBlockCore.CREEPER_BOMB).define('D', Blocks.DIAMOND_BLOCK).define('E', Blocks.EMERALD_BLOCK).pattern("EEE").pattern("D#D").pattern("###").unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block, new Item.Properties().rarity(Rarity.RARE)) {

            @Override
            public void appendHoverText(ItemStack p_40572_, TooltipContext p_327780_, List<Component> components, TooltipFlag p_40575_) {
                super.appendHoverText(p_40572_, p_327780_, components, p_40575_);
                components.add(Component.translatable("item.takumicraft.creeperaltar.desc.0"));
                components.add(Component.translatable("item.takumicraft.creeperaltar.desc.1"));
                components.add(Component.translatable("item.takumicraft.creeperaltar.desc.2"));
            }
        };
    }
}
