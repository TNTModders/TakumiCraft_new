package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.block.entity.TCMonsterBombBlockEntity;
import com.tntmodders.takumicraft.client.renderer.block.TCBEWLRenderer;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.data.loot.TCBlockLoot;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper;
import com.tntmodders.takumicraft.entity.mobs.AbstractTCCreeper.TCCreeperContext;
import com.tntmodders.takumicraft.entity.mobs.TCYukariCreeper;
import com.tntmodders.takumicraft.item.TCBlockItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class TCMonsterBombBlock extends AbstractTCBombBlock implements EntityBlock {
    public static final ToIntFunction<BlockState> LIGHT_TCBOMB = state -> 2;
    private final TCCreeperContext context;

    public TCMonsterBombBlock(TCCreeperContext context) {
        super(Properties.of().strength(0.1f, 0f).mapColor(MapColor.COLOR_LIGHT_GREEN).lightLevel(LIGHT_TCBOMB).noOcclusion(), "monsterbomb_" + context.getRegistryName());
        this.context = context;
    }

    public TCCreeperContext getContext() {
        return context;
    }

    @Override
    public float getPower() {
        return 0f;
    }

    @Override
    public String getEnUSName() {
        return context.getEnUSName() + " Bomb";
    }

    @Override
    public String getJaJPName() {
        return context.getJaJPName() + "式高性能爆弾";
    }

    @Override
    public Supplier<LootTableSubProvider> getBlockLootSubProvider(Block block) {
        return () -> new TCBlockLoot(block, false);
    }

    @Override
    public EnumTCBlockStateModelType getBlockStateModelType() {
        return EnumTCBlockStateModelType.ANIMATED;
    }

    @Override
    protected void explode(Level level, BlockPos pos, float power) {
        if (!level.isClientSide()) {
            var entity = this.context.entityType().create(level);
            if (entity instanceof AbstractTCCreeper creeper) {
                creeper.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                creeper.setInvulnerable(true);
                creeper.setInvisible(true);
                level.addFreshEntity(creeper);
                if (creeper instanceof TCYukariCreeper && level.getBlockState(pos.below()).getDestroySpeed(level, pos.below()) >= 0f) {
                    level.setBlock(pos.below(), TCBlockCore.YUKARI_DUMMY.defaultBlockState(), 3);
                }
                creeper.explodeCreeper();
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new TCMonsterBombBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public TCBlockItem getCustomBlockItem(Block block) {
        return new TCBlockItem(block) {
            @Override
            public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                consumer.accept(new IClientItemExtensions() {
                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return new TCBEWLRenderer();
                    }
                });
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
                super.appendHoverText(stack, level, components, tooltipFlag);
                components.add(Component.translatable("item.takumicraft.monsterbomb_yukari.desc"));
            }
        };
    }
}
