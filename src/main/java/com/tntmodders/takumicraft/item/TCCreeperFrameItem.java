package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperFrame;
import com.tntmodders.takumicraft.entity.decoration.TCCreeperGlowingFrame;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemFrameItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class TCCreeperFrameItem extends ItemFrameItem implements ITCItems, ITCRecipe {
    private final Item base;
    private final String suffix;
    private final String en;
    private final String ja;

    public TCCreeperFrameItem(EntityType<? extends HangingEntity> type, Item base, String suffix, String enName, String jaName) {
        super(type, new Properties());
        this.base = base;
        this.suffix = suffix;
        this.en = enName;
        this.ja = jaName;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos blockpos1 = blockpos.relative(direction);
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
            return InteractionResult.FAIL;
        } else {
            Level level = context.getLevel();
            HangingEntity hangingentity;
            if (this.base == Items.ITEM_FRAME) {
                hangingentity = new TCCreeperFrame(TCCreeperFrame.ITEM_FRAME, level, blockpos1, direction);
            } else {
                if (this.base != Items.GLOW_ITEM_FRAME) {
                    return InteractionResult.SUCCESS;
                }

                hangingentity = new TCCreeperGlowingFrame(TCCreeperGlowingFrame.GLOWING_FRAME, level, blockpos1, direction);
            }

            CustomData customdata = itemstack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
            if (!customdata.isEmpty()) {
                EntityType.updateCustomEntityTag(level, player, hangingentity, customdata);
            }

            if (hangingentity.survives()) {
                if (!level.isClientSide) {
                    hangingentity.playPlacementSound();
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, hangingentity.position());
                    level.addFreshEntity(hangingentity);
                }

                itemstack.shrink(1);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        }
    }

    @Override
    public String getRegistryName() {
        return "creeperframe" + this.suffix;
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.DECORATIONS, this, 8)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('B', this.base)
                .pattern("BBB")
                .pattern("B#B")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return this.en;
    }

    @Override
    public String getJaJPName() {
        return this.ja;
    }
}
