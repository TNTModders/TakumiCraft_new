package com.tntmodders.takumicraft.entity.ai.boss.king;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;

public abstract class AbstractKingCreeperAttack {

    public abstract void serverInit(TCKingCreeper creeper);

    public abstract void serverTick(TCKingCreeper creeper, int fuse);

    public abstract void clientTick(TCKingCreeper creeper, int fuse, PoseStack pose, float renderTick);

    public abstract void serverExp(TCKingCreeper creeper);

    public void clientExp(TCKingCreeper creeper, int fuse, PoseStack pose, float renderTick) {
    }

    public TCKingCreeper.EnumTCKingCreeperAttackID nextAttackID() {
        return TCKingCreeper.EnumTCKingCreeperAttackID.NONE;
    }
}
