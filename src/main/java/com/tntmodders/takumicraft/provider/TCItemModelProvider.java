package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TCItemModelProvider extends ItemModelProvider {
    public TCItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TakumiCraftCore.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        TCLoggingUtils.startRegistry("ItemModel");
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCItems) {
                switch (((ITCItems) item).getItemModelType()) {
                    case SIMPLE -> this.simpleItem(item);
                    case SP -> {
                        //shield
                    }
                    case SPAWN_EGG -> this.eggItem(item);
                    case HANDHELD -> this.handheldItem(item);
                    case BOW -> this.bowItem(item);
                }
                TCLoggingUtils.entryRegistry("ItemModel_" + ((ITCItems) item).getItemModelType().name(), ((ITCItems) item).getRegistryName());
            }
        });
        TCLoggingUtils.completeRegistry("ItemModel");
    }

    private void simpleItem(Item item) {
        ResourceLocation name = new ResourceLocation(modid, ((ITCItems) item).getRegistryName());
        singleTexture(name.getPath(), mcLoc(folder + "/generated"), "layer0", new ResourceLocation(name.getNamespace(), folder + "/" + name.getPath()));
    }

    private void bowItem(Item item) {
        ResourceLocation[] loc = new ResourceLocation[3];
        String name;
        for (int i = 0; i < 3; i++) {
            name = ((ITCItems) item).getRegistryName() + "_pulling_" + i;
            loc[i] = new ResourceLocation(TakumiCraftCore.MODID, folder + "/" + name);
            withExistingParent(name, mcLoc("bow_pulling_" + i)).texture("layer0", loc[0]);
        }
        name = ((ITCItems) item).getRegistryName();
        withExistingParent(name, mcLoc("bow")).texture("layer0", new ResourceLocation(TakumiCraftCore.MODID, folder + "/" + name))
                .override().predicate(new ResourceLocation("pulling"), 1f).model(new ModelFile.ExistingModelFile(loc[0], this.existingFileHelper)).end()
                .override().predicate(new ResourceLocation("pulling"), 1f).predicate(new ResourceLocation("pull"), 0.65f).model(new ModelFile.ExistingModelFile(loc[1], this.existingFileHelper)).end()
                .override().predicate(new ResourceLocation("pulling"), 1f).predicate(new ResourceLocation("pull"), 0.9f).model(new ModelFile.ExistingModelFile(loc[2], this.existingFileHelper));
    }

    private void handheldItem(Item item) {
        ResourceLocation name = new ResourceLocation(modid, ((ITCItems) item).getRegistryName());
        singleTexture(name.getPath(), mcLoc(folder + "/handheld"), "layer0", new ResourceLocation(name.getNamespace(), folder + "/" + name.getPath()));
    }

    public void eggItem(Item item) {
        withExistingParent(((ITCItems) item).getRegistryName(), mcLoc("item/template_spawn_egg"));
    }
}
