package com.tntmodders.takumicraft.block;

import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.provider.ITCRecipe;
import com.tntmodders.takumicraft.provider.TCBlockStateProvider;
import com.tntmodders.takumicraft.provider.TCLanguageProvider;
import com.tntmodders.takumicraft.provider.TCRecipeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class TCCreeperCarpetBlock extends AbstractTCAntiExplosionBlock implements ITCRecipe {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private final DyeColor color;
    private final Block baseBlock;

    public TCCreeperCarpetBlock(DyeColor colorIn) {
        super(Properties.of().mapColor(colorIn.getMapColor()).instrument(NoteBlockInstrument.GUITAR).strength(0.8F).sound(SoundType.WOOL).setId(TCBlockCore.TCBlockId("creepercarpet_" + colorIn.getName())));
        this.color = colorIn;
        this.baseBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.withDefaultNamespace(this.color.getName() + "_carpet"));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public VoxelShape getShape(BlockState p_152917_, BlockGetter p_152918_, BlockPos p_152919_, CollisionContext p_152920_) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState p_152926_, LevelReader p_367863_, ScheduledTickAccess p_362101_, BlockPos p_152930_, Direction p_152927_, BlockPos p_152931_, BlockState p_152928_, RandomSource p_362637_) {
        return !p_152926_.canSurvive(p_367863_, p_152930_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_152926_, p_367863_, p_362101_, p_152930_, p_152927_, p_152931_, p_152928_, p_362637_);
    }

    @Override
    public boolean canSurvive(BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
        return !p_152923_.isEmptyBlock(p_152924_.below());
    }

    @Override
    public void addRecipes(TCRecipeProvider provider, ItemLike itemLike, RecipeOutput consumer) {
        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.DECORATIONS, TCBlockCore.CREEPER_CARPET_MAP.get(this.color), 8).define('#', TCBlockCore.CREEPER_BOMB).define('B', this.baseBlock).pattern("BBB").pattern("B#B").pattern("BBB").unlockedBy("has_creeperbomb", provider.hasItem(TCBlockCore.CREEPER_BOMB)).group("creepercarpet"));

        provider.saveRecipe(itemLike, consumer, ShapedRecipeBuilder.shaped(provider.items, RecipeCategory.DECORATIONS, TCBlockCore.CREEPER_CARPET_MAP.get(this.color), 3).define('#', TCBlockCore.CREEPER_WOOL_MAP.get(this.color)).pattern("##").unlockedBy("has_creeperwool", provider.hasItem(TCBlockCore.CREEPER_WOOL_MAP.get(this.color))).group("creepercarpet"), "from_creeperwool");
    }

    @Override
    public String getRegistryName() {
        return "creepercarpet_" + this.color.getName();
    }

    @Override
    public String getEnUSName() {
        return TCLanguageProvider.TCEnUSLanguageProvider.TC_ENUS_LANGMAP.get("takumicraft.color." + this.color.getName()) + " Carpet";
    }

    @Override
    public String getJaJPName() {
        return TCLanguageProvider.TCJaJPLanguageProvider.TC_JAJP_LANGMAP.get("takumicraft.color." + this.color.getName()) + "匠式硬質敷布";
    }

    @Override
    public void registerStateAndModel(TCBlockStateProvider provider) {
        ModelFile model = provider.models().withExistingParent(this.getRegistryName(), "block/carpet").texture("wool", provider.blockTexture(TCBlockCore.CREEPER_WOOL_MAP.get(this.getColor())));
        provider.simpleBlock(this, model);
        provider.simpleBlockItem(this, model);
    }
}
