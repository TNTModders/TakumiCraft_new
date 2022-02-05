package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class TCAdvancementProvider extends AdvancementProvider {
    public TCAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        TCLoggingUtils.startRegistry("Advancements");

        Advancement slay_root = Advancement.Builder.advancement()
                /*.display(TCBlockCore.CREEPER_BOMB, new TranslatableComponent("takumicraft.takumibook"),
                        new TranslatableComponent("takumicraft.takumibook"), new ResourceLocation(TakumiCraftCore.MODID, "textures/block/creeperbomb.png"), FrameType.TASK,
                        false, false, true)*/
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay/root"), fileHelper);

        TCEntityCore.ENTITY_TYPES.forEach(type -> Advancement.Builder.advancement().parent(slay_root)
                .display(TCItemCore.TAKUMIBOOK, new TranslatableComponent(type.getDescriptionId()),
                        new TranslatableComponent("takumicraft.message.slay"), null, FrameType.TASK, true, true, true)
                .addCriterion("killed_" + type.getRegistryName().getPath(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(type)))
                .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay/slay_" + type.getRegistryName().getPath()), fileHelper));

        TCLoggingUtils.completeRegistry("Advancements");
    }

    private void registerAdditionalAdvancements(Consumer<Advancement> consumer) {

    }
}
