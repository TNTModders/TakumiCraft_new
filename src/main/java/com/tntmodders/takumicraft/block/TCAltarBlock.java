package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TCAltarBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {

    public TCAltarBlock() {
        super(Properties.of().strength(0.5f, 1000000f).lightLevel(state -> 7));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide()) {
            if (level.getBlockState(pos.below()).is(TCBlockCore.CREEPER_BOMB)) {

            } else {
                if (summonHigh(level, pos)) {
                    TCExplosionUtils.createExplosion(level, null, pos, 0f);
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.use(state, level, pos, player, hand, result);
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

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.SIDE;
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
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, itemLike)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('D', Blocks.DIAMOND_BLOCK)
                .define('E', Blocks.EMERALD_BLOCK)
                .pattern("EEE")
                .pattern("D#D")
                .pattern("###")
                .unlockedBy("has_creeperbomb", TCRecipeProvider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }
}
