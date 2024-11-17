package com.tntmodders.takumicraft.mixin;

import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class TCMixinCreeper {
    @Inject(method = "explodeCreeper", at = @At(value = "HEAD"))
    public void mixinExplodeCreeper(CallbackInfo ci) {
        //TCLoggingUtils.info("TC_MIXEDIN_CREEPER");
    }
}
