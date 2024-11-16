package com.tntmodders.takumicraft.block;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.block.entity.TCCreeperCampFireBlockEntity;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCBlockEntityCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.item.TCBlockItem;
import com.tntmodders.takumicraft.provider.ITCBlocks;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class TCCreeperCampFireBlock extends CampfireBlock implements ITCBlocks, ITCRecipe {
    public static final MapCodec<CampfireBlock> CODEC = simpleCodec(TCCreeperCampFireBlock::new);

    public TCCreeperCampFireBlock(Properties properties) {
        super(false, 5, properties);
    }

    public TCCreeperCampFireBlock() {
        this(BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F, 1000000F).sound(SoundType.WOOD).lightLevel(p_50763_ -> p_50763_.getValue(BlockStateProperties.LIT) ? 15 : 0).noOcclusion().ignitedByLava());
    }

    public static void dowse(@Nullable Entity p_152750_, LevelAccessor p_152751_, BlockPos p_152752_, BlockState p_152753_) {
        if (p_152751_.isClientSide()) {
            for (int i = 0; i < 20; i++) {
                makeParticles((Level) p_152751_, p_152752_, p_152753_.getValue(SIGNAL_FIRE), true);
            }
        }

        BlockEntity blockentity = p_152751_.getBlockEntity(p_152752_);
        if (blockentity instanceof TCCreeperCampFireBlockEntity) {
            ((TCCreeperCampFireBlockEntity) blockentity).dowse();
        }

        p_152751_.gameEvent(p_152750_, GameEvent.BLOCK_CHANGE, p_152752_);
    }

    @Override
    public MapCodec<CampfireBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
        if (!level.isClientSide()) {
            if (!state.getValue(LIT)) {
                level.setBlock(pos, state.setValue(LIT, true), 3);
                if (player instanceof ServerPlayer serverplayer) {
                    Function<Component, Packet<?>> function = ClientboundSetTitleTextPacket::new;
                    try {
                        serverplayer.connection.send(function.apply(ComponentUtils.updateForEntity(serverplayer.createCommandSourceStack(), Component.literal("CAMPFIRE LIT").withStyle(ChatFormatting.DARK_RED), serverplayer, 0)));
                        serverplayer.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
                        serverplayer.getAdvancements().award(Objects.requireNonNull(serverplayer.server.getAdvancements().get(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creepercampfire"))), "impossible");
                    } catch (CommandSyntaxException ignored) {
                    }
                }
            }
            if (player instanceof ServerPlayer serverplayer) {
                BlockPos blockPos = pos;
                if (serverplayer.getRespawnDimension() != level.dimension() || !blockPos.equals(serverplayer.getRespawnPosition())) {
                    serverplayer.setRespawnPosition(level.dimension(), blockPos, 0.0F, true, true);
                }
                serverplayer.sendSystemMessage(Component.translatable("item.takumicraft.creeper_campfire.set_spawn"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return state.getBlock().equals(this) && state.getValue(LIT) || super.isPossibleToRespawnInThis(state);
    }

    @Override
    public void animateTick(BlockState p_220918_, Level p_220919_, BlockPos p_220920_, RandomSource p_220921_) {
        if (p_220918_.getValue(LIT)) {
            if (p_220921_.nextInt(5) == 0) {
                SoundEvent sound = SoundEvents.CAMPFIRE_CRACKLE;
                p_220919_.playLocalSound((double) p_220920_.getX() + 0.5, (double) p_220920_.getY() + 0.5, (double) p_220920_.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.5F + p_220921_.nextFloat(), p_220921_.nextFloat() * 0.7F + 0.6F, false);
            }
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult result, Projectile projectile) {
        super.onProjectileHit(level, state, result, projectile);
        if (projectile instanceof ThrownPotion potion && potion.getItem().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER)) {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            level.levelEvent(null, 1009, pos, 0);
            dowse(potion, level, pos, state);
            level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, Boolean.FALSE));
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof TCCreeperCampFireBlockEntity campfireblockentity) {
            ItemStack itemstack = player.getItemInHand(hand);
            Optional<RecipeHolder<CampfireCookingRecipe>> optional = campfireblockentity.getCookableRecipe(serverLevel, itemstack);
            if (optional.isPresent()) {
                if (!level.isClientSide
                        && campfireblockentity.placeFood(player, player.hasInfiniteMaterials() ? itemstack.copy() : itemstack, optional.get().value().cookingTime())) {
                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.CONSUME;
            } else if (itemstack.getItem() instanceof ShovelItem && state.getValue(LIT)) {
                if (!level.isClientSide()) {
                    level.levelEvent(null, 1009, pos, 0);
                }

                dowse(player, level, pos, state);
                BlockState newState = state.setValue(CampfireBlock.LIT, Boolean.FALSE);
                if (!level.isClientSide) {
                    level.setBlock(pos, newState, 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
                    if (player != null) {
                        player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                    }
                }
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_51285_) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof TCCreeperCampFireBlockEntity) {
                Containers.dropContents(level, pos, ((TCCreeperCampFireBlockEntity) blockentity).getItems());
            }
            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverplayer && serverplayer.getRespawnPosition().equals(pos)) {
                    serverplayer.setRespawnPosition(level.dimension(), null, 0f, false, false);

                }
            });
            super.onRemove(state, level, pos, newState, p_51285_);
        }
    }

    @Override
    public boolean placeLiquid(LevelAccessor p_51257_, BlockPos p_51258_, BlockState p_51259_, FluidState p_51260_) {
        if (!p_51259_.getValue(BlockStateProperties.WATERLOGGED) && p_51260_.getType() == Fluids.WATER) {
            boolean flag = p_51259_.getValue(LIT);
            if (flag) {
                if (!p_51257_.isClientSide()) {
                    p_51257_.playSound(null, p_51258_, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                dowse(null, p_51257_, p_51258_, p_51259_);
            }

            p_51257_.setBlock(p_51258_, p_51259_.setValue(WATERLOGGED, Boolean.TRUE).setValue(LIT, Boolean.FALSE), 3);
            p_51257_.scheduleTick(p_51258_, p_51260_.getType(), p_51260_.getType().getTickDelay(p_51257_));
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152755_, BlockState p_152756_, BlockEntityType<T> p_152757_) {
        if (p_152755_.isClientSide) {
            return p_152756_.getValue(LIT) ? createTickerHelper(p_152757_, TCBlockEntityCore.CAMPFIRE, TCCreeperCampFireBlockEntity::particleTick) : null;
        } else {
            return p_152756_.getValue(LIT)
                    ? createTickerHelper(p_152757_, TCBlockEntityCore.CAMPFIRE, TCCreeperCampFireBlockEntity::cookTick)
                    : createTickerHelper(p_152757_, TCBlockEntityCore.CAMPFIRE, TCCreeperCampFireBlockEntity::cooldownTick);
        }
    }

    @Override
    protected void entityInside(BlockState p_51269_, Level p_51270_, BlockPos p_51271_, Entity p_51272_) {
        if (!(p_51272_ instanceof Player player && player.tickCount < 200)) {
            super.entityInside(p_51269_, p_51270_, p_51271_, p_51272_);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_152759_, BlockState p_152760_) {
        return new TCCreeperCampFireBlockEntity(p_152759_, p_152760_);
    }

    @Override
    public String getRegistryName() {
        return "creeper_campfire";
    }

    @Override
    public Function<HolderLookup.Provider, LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return provider -> new TCBlockLoot(provider, block, true);
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.BUILDING_BLOCKS, TCBlockCore.CREEPER_CAMPFIRE)
                .define('#', TCBlockCore.CREEPER_BOMB)
                .define('S', Items.STICK)
                .define('B', TCBlockCore.CREEPER_PLANKS)
                .pattern(" S ")
                .pattern("S#S")
                .pattern("BBB")
                .unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)));
    }

    @Override
    public String getEnUSName() {
        return "Creeper Campfire";
    }

    @Override
    public String getJaJPName() {
        return "匠式硬質篝火";
    }

    @Override
    public List<TagKey<Block>> getBlockTags() {
        return List.of(TCBlockCore.ANTI_EXPLOSION, BlockTags.MINEABLE_WITH_AXE, BlockTags.CAMPFIRES);
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile lit = provider.models().withExistingParent(this.getRegistryName(), "minecraft:block/campfire")
                .texture("fire", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeper_campfire_fire")))
                .texture("log", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeper_campfire_log")))
                .texture("lit_log", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeper_campfire_log_lit")))
                .texture("particle", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeper_campfire_log_lit")))
                .renderType("cutout");
        ModelFile unlit = provider.models().withExistingParent(this.getRegistryName() + "_off", "minecraft:block/campfire_off")
                .texture("log", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeper_campfire_log")))
                .texture("particle", provider.blockFolder(ResourceLocation.tryBuild(TakumiCraftCore.MODID, "creeper_campfire_log")))
                .renderType("cutout");
        provider.getVariantBuilder(this).forAllStatesExcept(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            return ConfiguredModel.builder()
                    .modelFile(state.getValue(LIT) ? lit : unlit)
                    .rotationY(dir.getAxis().isVertical() ? 0 : ((int) dir.toYRot() + 180) % 360)
                    .build();
        }, SIGNAL_FIRE, WATERLOGGED);

        provider.itemModels().basicItem(this.asItem());
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {
            @Override
            public void appendHoverText(ItemStack p_40572_, TooltipContext p_327780_, List<Component> components, TooltipFlag p_40575_) {
                super.appendHoverText(p_40572_, p_327780_, components, p_40575_);
                components.add(Component.translatable("item.takumicraft.creeper_campfire.desc"));
            }
        };
    }
}
