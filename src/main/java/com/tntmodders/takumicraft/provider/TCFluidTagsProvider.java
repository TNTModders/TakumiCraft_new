package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCFluidCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TCFluidTagsProvider extends FluidTagsProvider {
    public TCFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, future, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TCLoggingUtils.startRegistry("Fluid Tag");
        this.tag(TCFluidCore.HOTSPRINGS).add(TCFluidCore.HOTSPRING.get(), TCFluidCore.FLOWING_HOTSPRING.get());
        TCLoggingUtils.completeRegistry("Fluid Tag");
    }
}
