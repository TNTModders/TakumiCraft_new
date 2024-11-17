package com.tntmodders.takumicraft.mixin;

import com.tntmodders.takumicraft.client.renderer.entity.TCCreeperFrameRenderer;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(BlockStateModelLoader.class)
public class TCMixinBlockStateModelLoader {

    @Inject(method = "definitionLocationToBlockMapper", at = @At(value = "HEAD"))
    private static void mixinDefinitionLocationToBlockMapper(CallbackInfoReturnable<Function<ResourceLocation, StateDefinition<Block, BlockState>>> cir) {
        //TCLoggingUtils.info("TC_MIXEDIN");
    }

    @ModifyVariable(method = "definitionLocationToBlockMapper", at = @At("STORE"), ordinal = 0)
    private static Map<ResourceLocation, StateDefinition<Block, BlockState>> mixinMapInjected(Map value) {
        return new HashMap<>(TCCreeperFrameRenderer.STATIC_DEFINITIONS);
    }
}
