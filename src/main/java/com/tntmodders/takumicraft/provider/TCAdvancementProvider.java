package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
                /*.display(TCBlockCore.CREEPER_BOMB, new TranslatableContents("takumicraft.takumibook"),
                        new TranslatableContents("takumicraft.takumibook"), new ResourceLocation(TakumiCraftCore.MODID, "textures/block/creeperbomb.png"), FrameType.TASK,
                        false, false, true)*/
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay/root"), fileHelper);

        TCEntityCore.ENTITY_TYPES.forEach(type -> Advancement.Builder.advancement().parent(slay_root)
                .display(new ItemStack(TCItemCore.TAKUMIBOOK), Component.translatable(type.getDescriptionId()),
                        Component.translatable("takumicraft.message.slay"), null, FrameType.TASK, true, true, true)
                .addCriterion("killed_" + type.toShortString(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(type)))
                .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay/slay_" + type.toShortString()), fileHelper));

        this.registerAdditionalAdvancements(consumer, fileHelper);

        TCLoggingUtils.completeRegistry("Advancements");
    }

    private void registerAdditionalAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(new ItemStack(Items.CREEPER_HEAD), Component.translatable("advancement.takumicraft.root.title"),
                        Component.translatable("advancement.takumicraft.root.desc"), new ResourceLocation(TakumiCraftCore.MODID, "textures/block/gunore.png"),
                        FrameType.TASK, true, false, true)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "root"), fileHelper);

        Advancement.Builder.advancement()
                .display(new ItemStack(TCBlockCore.CREEPER_BOMB), Component.translatable("advancement.takumicraft.creeperbomb.title"),
                        Component.translatable("advancement.takumicraft.creeperbomb.desc"), null,
                        FrameType.TASK, true, true, false)
                .addCriterion("creeperbomb", InventoryChangeTrigger.TriggerInstance.hasItems(TCBlockCore.CREEPER_BOMB))
                .parent(root).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "creeperbomb"), fileHelper);

        Advancement.Builder slay_all = Advancement.Builder.advancement()
                .display(new ItemStack(TCItemCore.TAKUMIBOOK), Component.translatable("advancement.takumicraft.slay_all.title"),
                        Component.translatable("advancement.takumicraft.slay_all.desc"), null,
                        FrameType.GOAL, true, true, true)
                .requirements(RequirementsStrategy.AND).parent(root);

        TCEntityCore.ENTITY_CONTEXTS.forEach(context ->
                slay_all.addCriterion("slay_" + context.getRegistryName(), KilledTrigger.TriggerInstance.playerKilledEntity(new EntityPredicate.Builder().of(context.entityType()))));

        slay_all.save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay_all"), fileHelper);
    }
}