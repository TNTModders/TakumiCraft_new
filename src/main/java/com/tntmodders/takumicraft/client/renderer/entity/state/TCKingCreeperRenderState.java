package com.tntmodders.takumicraft.client.renderer.entity.state;

import com.tntmodders.takumicraft.entity.mobs.boss.TCKingCreeper;

public class TCKingCreeperRenderState extends TCCreeperRenderState {
    public TCKingCreeper.EnumTCKingCreeperAttackID id;
    public int swellDir;
    protected TCKingCreeper creeper;

    public TCKingCreeper getCreeper() {
        return creeper;
    }

    public void setCreeper(TCKingCreeper creeper) {
        this.creeper = creeper;
    }
}
