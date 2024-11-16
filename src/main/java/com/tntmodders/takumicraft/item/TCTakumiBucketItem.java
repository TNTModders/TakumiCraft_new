package com.tntmodders.takumicraft.item;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.provider.ITCItems;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.function.Supplier;

public class TCTakumiBucketItem extends BucketItem implements ITCItems, ITCRecipe {
    private final String registryName;
    private final String enName;
    private final String jpName;

    public TCTakumiBucketItem(Supplier<? extends Fluid> supplier, String registryName, String enName, String jpName) {
        super(supplier, new Item.Properties().craftRemainder(TCItemCore.TAKUMI_BUCKET).stacksTo(supplier.get() == Fluids.EMPTY ? 16 : 1));
        this.registryName = registryName;
        this.enName = enName;
        this.jpName = jpName;
    }

    public static ItemStack getEmptySuccessItem(ItemStack stack, Player player) {
        return !player.hasInfiniteMaterials() ? new ItemStack(TCItemCore.TAKUMI_BUCKET) : stack;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public String getEnUSName() {
        return enName;
    }

    @Override
    public String getJaJPName() {
        return jpName;
    }

    @Override
    public List<TagKey<Item>> getItemTags() {
        return List.of(TCItemCore.TAKUMI_BUCKETS);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, this.getFluid() == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        InteractionResult ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(player, level, itemstack, blockhitresult);
        if (ret != null) return ret;
        if (blockhitresult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            Direction direction = blockhitresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (!level.mayInteract(player, blockpos) || !player.mayUseItemAt(blockpos1, direction, itemstack)) {
                return InteractionResult.FAIL;
            } else if (this.getFluid() == Fluids.EMPTY) {
                BlockState blockstate1 = level.getBlockState(blockpos);
                if (blockstate1.getBlock() instanceof BucketPickup bucketpickup) {
                    ItemStack itemstack3 = bucketpickup.pickupBlock(player, level, blockpos, blockstate1);
                    if (!itemstack3.isEmpty() && itemstack3.is(TCItemCore.TAKUMI_BUCKETS)) {
                        player.awardStat(Stats.ITEM_USED.get(this));
                        bucketpickup.getPickupSound(blockstate1).ifPresent(p_150709_ -> player.playSound(p_150709_, 1.0F, 1.0F));
                        level.gameEvent(player, GameEvent.FLUID_PICKUP, blockpos);
                        ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack3);
                        if (!level.isClientSide) {
                            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack3);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }

                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = level.getBlockState(blockpos);
                BlockPos blockpos2 = canBlockContainFluid(level, blockpos, blockstate) ? blockpos : blockpos1;
                if (this.emptyContents(player, level, blockpos2, blockhitresult, itemstack)) {
                    this.checkExtraContent(player, level, itemstack, blockpos2);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos2, itemstack);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, getEmptySuccessItem(itemstack, player));
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        if (this.getFluid() == Fluids.EMPTY) {
            provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.TOOLS, itemLike).define('#', Items.EMERALD).define('B', TCBlockCore.CREEPER_BOMB).pattern("#B#").pattern(" # ").unlockedBy("has_emerald", provider.hasItem(Items.EMERALD)));
        }
    }
}
