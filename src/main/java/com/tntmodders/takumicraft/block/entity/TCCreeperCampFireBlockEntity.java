package com.tntmodders.takumicraft.block.entity;

import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import com.tntmodders.takumicraft.item.TCTakumiSpecialMeatItem;
import com.tntmodders.takumicraft.utils.TCExplosionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public class TCCreeperCampFireBlockEntity extends BlockEntity implements Clearable {
    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[4];
    private final int[] cookingTime = new int[4];
    private final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public TCCreeperCampFireBlockEntity(BlockPos p_155301_, BlockState p_155302_) {
        super(TCBlockEntityCore.CAMPFIRE, p_155301_, p_155302_);
    }

    public static void cookTick(Level level, BlockPos pos, BlockState state, TCCreeperCampFireBlockEntity campfire) {
        boolean flag = false;

        for (int i = 0; i < campfire.items.size(); i++) {
            ItemStack itemstack = campfire.items.get(i);
            if (!itemstack.isEmpty() && level instanceof ServerLevel serverLevel) {
                flag = true;
                campfire.cookingProgress[i]++;
                if (campfire.cookingProgress[i] >= campfire.cookingTime[i]) {
                    SingleRecipeInput container = new SingleRecipeInput(itemstack);
                    ItemStack itemstack1 = campfire.quickCheck
                            .getRecipeFor(container, serverLevel)
                            .map(p_327303_ -> p_327303_.value().assemble(container, level.registryAccess()))
                            .orElse(itemstack);
                    if (itemstack1.isItemEnabled(level.enabledFeatures())) {
                        if (TCTakumiSpecialMeatItem.BASE_LIST.contains(itemstack1.getItem())) {
                            itemstack1 = TCTakumiSpecialMeatItem.getSpecialMeat(itemstack1);
                        }
                        TCExplosionUtils.createExplosion(level, null, pos, 0f);
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemstack1);
                        campfire.items.set(i, ItemStack.EMPTY);
                        level.sendBlockUpdated(pos, state, state, 3);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
                    }
                }
            }
        }

        if (flag) {
            setChanged(level, pos, state);
        }
    }

    public static void cooldownTick(Level p_155314_, BlockPos p_155315_, BlockState p_155316_, TCCreeperCampFireBlockEntity p_155317_) {
        boolean flag = false;

        for (int i = 0; i < p_155317_.items.size(); i++) {
            if (p_155317_.cookingProgress[i] > 0) {
                flag = true;
                p_155317_.cookingProgress[i] = Mth.clamp(p_155317_.cookingProgress[i] - 2, 0, p_155317_.cookingTime[i]);
            }
        }

        if (flag) {
            setChanged(p_155314_, p_155315_, p_155316_);
        }
    }

    public static void particleTick(Level p_155319_, BlockPos p_155320_, BlockState p_155321_, TCCreeperCampFireBlockEntity p_155322_) {
        RandomSource randomsource = p_155319_.random;
        if (randomsource.nextFloat() < 0.11F) {
            for (int i = 0; i < randomsource.nextInt(2) + 2; i++) {
                CampfireBlock.makeParticles(p_155319_, p_155320_, p_155321_.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        int l = p_155321_.getValue(CampfireBlock.FACING).get2DDataValue();

        for (int j = 0; j < p_155322_.items.size(); j++) {
            if (!p_155322_.items.get(j).isEmpty() && randomsource.nextFloat() < 0.2F) {
                Direction direction = Direction.from2DDataValue(Math.floorMod(j + l, 4));
                float f = 0.3125F;
                double d0 = (double) p_155320_.getX()
                        + 0.5
                        - (double) ((float) direction.getStepX() * 0.3125F)
                        + (double) ((float) direction.getClockWise().getStepX() * 0.3125F);
                double d1 = (double) p_155320_.getY() + 0.5;
                double d2 = (double) p_155320_.getZ()
                        + 0.5
                        - (double) ((float) direction.getStepZ() * 0.3125F)
                        + (double) ((float) direction.getClockWise().getStepZ() * 0.3125F);

                for (int k = 0; k < 4; k++) {
                    p_155319_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void loadAdditional(CompoundTag p_333177_, HolderLookup.Provider p_333564_) {
        super.loadAdditional(p_333177_, p_333564_);
        this.items.clear();
        ContainerHelper.loadAllItems(p_333177_, this.items, p_333564_);
        if (p_333177_.contains("CookingTimes", 11)) {
            int[] aint = p_333177_.getIntArray("CookingTimes");
            System.arraycopy(aint, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, aint.length));
        }

        if (p_333177_.contains("CookingTotalTimes", 11)) {
            int[] aint1 = p_333177_.getIntArray("CookingTotalTimes");
            System.arraycopy(aint1, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, aint1.length));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag p_187486_, HolderLookup.Provider p_336279_) {
        super.saveAdditional(p_187486_, p_336279_);
        ContainerHelper.saveAllItems(p_187486_, this.items, true, p_336279_);
        p_187486_.putIntArray("CookingTimes", this.cookingProgress);
        p_187486_.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_329092_) {
        CompoundTag compoundtag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundtag, this.items, true, p_329092_);
        return compoundtag;
    }

    public Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ServerLevel serverLevel, ItemStack p_59052_) {
        return this.items.stream().noneMatch(ItemStack::isEmpty)
                ? Optional.empty()
                : this.quickCheck.getRecipeFor(new SingleRecipeInput(p_59052_), serverLevel);
    }

    public boolean placeFood(@Nullable Entity p_238285_, ItemStack p_238286_, int p_238287_) {
        for (int i = 0; i < this.items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty()) {
                this.cookingTime[i] = p_238287_ / 2;
                this.cookingProgress[i] = 0;
                this.items.set(i, p_238286_.split(1));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(p_238285_, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.markUpdated();
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput p_333862_) {
        super.applyImplicitComponents(p_333862_);
        p_333862_.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder p_333455_) {
        super.collectImplicitComponents(p_333455_);
        p_333455_.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag p_331425_) {
        p_331425_.remove("Items");
    }
}
