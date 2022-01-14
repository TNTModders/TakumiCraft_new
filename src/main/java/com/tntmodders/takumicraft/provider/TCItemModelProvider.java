package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class TCItemModelProvider extends ItemModelProvider {
    public TCItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        TCLoggingUtils.startRegistry("ItemModel");
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCItems) {
                switch (((ITCItems) item).getItemModelType()) {
                    case SIMPLE -> this.simpleItem(item);
                    case SHIELD -> {
                        //shield
                    }
                    case SPAWN_EGG -> this.eggItem(item);
                }
                TCLoggingUtils.entryRegistry("ItemModel_" + ((ITCItems) item).getItemModelType().name(), item.getRegistryName().getPath());
            }
        });
        TCLoggingUtils.completeRegistry("ItemModel");
    }

    private void simpleItem(Item item) {
        ResourceLocation name = Objects.requireNonNull(item.getRegistryName());
        singleTexture(name.getPath(), mcLoc(folder + "/generated"), "layer0", new ResourceLocation(name.getNamespace(), folder + "/" + name.getPath()));
    }

    public void eggItem(Item item) {
        withExistingParent(item.getRegistryName().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
