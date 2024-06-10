package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TCItemModelProvider extends ItemModelProvider {
    public TCItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        TCLoggingUtils.startRegistry("ItemModel");
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCItems itcItems) {
                itcItems.registerItemModel(this);
                TCLoggingUtils.entryRegistry("ItemModel", itcItems.getRegistryName());
            }
        });
        TCLoggingUtils.completeRegistry("ItemModel");
    }

    public String getFolder() {
        return this.folder;
    }

    public void singleItem(Item item, String base) {
        ResourceLocation name = new ResourceLocation(modid, ((ITCItems) item).getRegistryName());
        singleTexture(name.getPath(), mcLoc(folder + "/" + base), "layer0", new ResourceLocation(name.getNamespace(), folder + "/" + name.getPath()));
    }
}
