package com.tntmodders.takumicraft.core.client;

import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class TCItemPropertyCore {
    public static void register() {
        ItemProperties.register(TCItemCore.CREEPER_SHIELD, ResourceLocation.withDefaultNamespace("blocking"),
                (p_174575_, p_174576_, p_174577_, p_174578_) -> p_174577_ != null && p_174577_.isUsingItem() && p_174577_.getUseItem() == p_174575_ ? 1.0F : 0.0F);
        ItemProperties.register(TCItemCore.CREEPER_BOW, ResourceLocation.withDefaultNamespace("pull"), (p_174635_, p_174636_, p_174637_, p_174638_) -> {
            if (p_174637_ == null) {
                return 0.0F;
            } else if (p_174637_ instanceof TCKingCreeper kingCreeper) {
                return p_174637_.getUseItem() != p_174635_ ? 0.0F : (float) kingCreeper.getSwell() / kingCreeper.maxSwell;
            } else {
                return p_174637_.getUseItem() != p_174635_ ? 0.0F : (float) (p_174635_.getUseDuration(p_174637_) - p_174637_.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        ItemProperties.register(TCItemCore.CREEPER_BOW, ResourceLocation.withDefaultNamespace("pulling"), (p_174630_, p_174631_, p_174632_, p_174633_) -> {
            if (p_174632_ instanceof TCKingCreeper) {
                return 1f;
            }
            return p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F;
        });
    }
}
