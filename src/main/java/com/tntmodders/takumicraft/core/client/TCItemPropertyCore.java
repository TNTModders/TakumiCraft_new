package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.core.TCItemCore;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class TCItemPropertyCore {
    public static void register(){
        ItemProperties.register(TCItemCore.CREEPER_SHIELD, new ResourceLocation("blocking"),
                (p_174575_, p_174576_, p_174577_, p_174578_) -> p_174577_ != null && p_174577_.isUsingItem() && p_174577_.getUseItem() == p_174575_ ? 1.0F : 0.0F);
    }
}
