package com.tntmodders.takumicraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.VillagerDataHolderRenderState;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class TCZombieVillagerCreeperRenderState extends TCZombieCreeperRenderState implements VillagerDataHolderRenderState {
    public VillagerData villagerData = new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);

    @Override
    public VillagerData getVillagerData() {
        return this.villagerData;
    }
}
