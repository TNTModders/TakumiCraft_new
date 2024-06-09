package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.item.TCTakumiSpecialMeatItem;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TCAdvancementProvider extends ForgeAdvancementProvider {
    public TCAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper, List<AdvancementGenerator> subProviders) {
        super(output, registries, existingFileHelper, subProviders);
    }

    public static class TCAdvancementGenerator implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer,
                             ExistingFileHelper fileHelper) {
            TCLoggingUtils.startRegistry("Advancements");

            AdvancementHolder slay_root = Advancement.Builder.advancement()
                    /*.display(TCBlockCore.CREEPER_BOMB, new TranslatableContents("takumicraft.takumibook"),
                            new TranslatableContents("takumicraft.takumibook"), new ResourceLocation(TakumiCraftCore.MODID, "textures/block/creeperbomb.png"), AdvancementType.TASK,
                            false, false, true)*/
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay/root"));

            TCEntityCore.ENTITY_TYPES.forEach(type -> Advancement.Builder.advancement().parent(slay_root)
                    .display(new ItemStack(TCItemCore.TAKUMIBOOK), Component.translatable(type.getDescriptionId()),
                            Component.translatable("takumicraft.message.slay"), null, AdvancementType.TASK, true, true, true)
                    .addCriterion("killed_" + type.toShortString(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(type)))
                    .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay/slay_" + type.toShortString())));

            this.registerAdditionalAdvancements(consumer, fileHelper);

            TCLoggingUtils.completeRegistry("Advancements");
        }

        private void registerAdditionalAdvancements(Consumer<AdvancementHolder> consumer, ExistingFileHelper fileHelper) {
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(new ItemStack(Items.CREEPER_HEAD), Component.translatable("advancement.takumicraft.root.title"),
                            Component.translatable("advancement.takumicraft.root.desc"), new ResourceLocation(TakumiCraftCore.MODID, "textures/block/gunore.png"),
                            AdvancementType.TASK, true, false, true)
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "root"));

            AdvancementHolder creeperbomb = Advancement.Builder.advancement()
                    .display(new ItemStack(TCBlockCore.CREEPER_BOMB), Component.translatable("advancement.takumicraft.creeperbomb.title"),
                            Component.translatable("advancement.takumicraft.creeperbomb.desc"), null,
                            AdvancementType.TASK, true, true, false)
                    .addCriterion("creeperbomb", InventoryChangeTrigger.TriggerInstance.hasItems(TCBlockCore.CREEPER_BOMB))
                    .parent(root).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "creeperbomb"));

            Advancement.Builder slay_all_builder = Advancement.Builder.advancement()
                    .display(new ItemStack(TCItemCore.TAKUMIBOOK), Component.translatable("advancement.takumicraft.slay_all.title"),
                            Component.translatable("advancement.takumicraft.slay_all.desc"), null,
                            AdvancementType.GOAL, true, true, false).parent(root);
            TCEntityCore.ENTITY_CONTEXTS.forEach(context ->
                    slay_all_builder.addCriterion("slay_" + context.getRegistryName(), KilledTrigger.TriggerInstance.playerKilledEntity(new EntityPredicate.Builder().of(context.entityType()))));
            AdvancementHolder slay_all = slay_all_builder.save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "slay_all"));

            AdvancementHolder creepershield = Advancement.Builder.advancement()
                    .display(new ItemStack(TCItemCore.CREEPER_SHIELD), Component.translatable("advancement.takumicraft.creepershield.title"),
                            Component.translatable("advancement.takumicraft.creepershield.desc"), null,
                            AdvancementType.TASK, true, true, false)
                    .addCriterion("creepershield", InventoryChangeTrigger.TriggerInstance.hasItems(TCItemCore.CREEPER_SHIELD))
                    .parent(creeperbomb).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "creepershield"));

            AdvancementHolder disarmament = Advancement.Builder.advancement()
                    .display(new ItemStack(TCItemCore.CREEPER_SWORD), Component.translatable("advancement.takumicraft.disarmament.title"),
                            Component.translatable("advancement.takumicraft.disarmament.desc"), null,
                            AdvancementType.TASK, true, true, false)
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .parent(creepershield).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "disarmament"));

            AdvancementHolder rainbowsheep = Advancement.Builder.advancement()
                    .display(new ItemStack(TCBlockCore.CREEPER_WOOL_MAP.get(DyeColor.WHITE)), Component.translatable("advancement.takumicraft.rainbowsheep.title"),
                            Component.translatable("advancement.takumicraft.rainbowsheep.desc"), null,
                            AdvancementType.GOAL, true, true, true)
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .parent(disarmament).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "rainbowsheep"));

            AdvancementHolder takumialtar = Advancement.Builder.advancement()
                    .display(new ItemStack(TCBlockCore.TAKUMI_ALTAR), Component.translatable("advancement.takumicraft.takumialtar.title"),
                            Component.translatable("advancement.takumicraft.takumialtar.desc"), null,
                            AdvancementType.TASK, true, true, false)
                    .addCriterion("takumialtar", InventoryChangeTrigger.TriggerInstance.hasItems(TCBlockCore.TAKUMI_ALTAR))
                    .parent(creepershield).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "takumialtar"));

            Advancement.Builder specialmeat_builder = Advancement.Builder.advancement()
                    .display(new ItemStack(TCItemCore.SPMEAT_BEEF), Component.translatable("advancement.takumicraft.spmeat.title"),
                            Component.translatable("advancement.takumicraft.spmeat.desc"), null,
                            AdvancementType.TASK, true, true, false).parent(creepershield);
            TCTakumiSpecialMeatItem.MEAT_LIST.forEach(meatItem -> specialmeat_builder.addCriterion(meatItem.getRegistryName(), ConsumeItemTrigger.TriggerInstance.usedItem(meatItem)));
            AdvancementHolder specialmeat = specialmeat_builder.save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "spmeat"));

            AdvancementHolder kingslayer = Advancement.Builder.advancement()
                    .display(new ItemStack(TCItemCore.KING_CORE), Component.translatable("advancement.takumicraft.kingslayer.title"),
                            Component.translatable("advancement.takumicraft.kingslayer.desc"), null,
                            AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("kingslayer", KilledTrigger.TriggerInstance.playerKilledEntity(new EntityPredicate.Builder().of(TCEntityCore.KING.entityType()))).parent(takumialtar).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "kingslayer"));
            ItemStack barrel = new ItemStack(TCBlockCore.CREEPER_BARREL);
            barrel.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(898));
            AdvancementHolder creeperbarrel = Advancement.Builder.advancement()
                    .display(barrel, Component.translatable("advancement.takumicraft.creeperbarrel.title"),
                            Component.translatable("advancement.takumicraft.creeperbarrel.desc"), null,
                            AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .parent(creeperbomb).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "creeperbarrel"));
            AdvancementHolder creepercampfire = Advancement.Builder.advancement()
                    .display(new ItemStack(TCBlockCore.CREEPER_CAMPFIRE), Component.translatable("advancement.takumicraft.creepercampfire.title"),
                            Component.translatable("advancement.takumicraft.creepercampfire.desc"), null,
                            AdvancementType.TASK, true, true, false)
                    .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                    .parent(creeperbomb).save(consumer, new ResourceLocation(TakumiCraftCore.MODID, "creepercampfire"));
        }
    }
}