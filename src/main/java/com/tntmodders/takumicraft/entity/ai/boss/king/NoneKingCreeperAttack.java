package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NoneKingCreeperAttack extends AbstractKingCreeperAttack {
    @Override
    public void serverInit(TCKingCreeper creeper) {
    }

    @Override
    public void serverTick(TCKingCreeper creeper, int swell) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(TCKingCreeper creeper, int swell, PoseStack pose, MultiBufferSource bufferSource, float renderTick) {

    }

    @Override
    public void serverExp(TCKingCreeper creeper) {
    }
}
