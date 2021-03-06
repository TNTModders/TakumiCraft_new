package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEnchantmentCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
import com.tntmodders.takumicraft.utils.TCEntityUtils;
import com.tntmodders.takumicraft.utils.TCLoggingUtils;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class TCLanguageProvider extends LanguageProvider {
    private static final List<ITCTranslator> TRANSLATORS = new ArrayList<>();

    protected TCLanguageProvider(DataGenerator gen, String locale) {
        super(gen, TakumiCraftCore.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCTranslator) {
                this.add(block, this.getTranslation(((ITCTranslator) block)));
            }
        });
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCTranslator) {
                this.add(item, this.getTranslation(((ITCTranslator) item)));
            }
        });
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
            this.add(TCEntityUtils.getEntityLangCode(context.entityType(), ""), this.getTranslation(context));
            this.add(TCEntityUtils.getEntityLangCode(context.entityType(), ".desc"), this instanceof TCJaJPLanguageProvider ? context.getJaJPDesc() : context.getEnUSDesc());
        });
        TCEnchantmentCore.ENCHANTMENTS.forEach(ench -> this.add(ench, this.getTranslation(ench)));
        this.additionalTranslations();
    }

    abstract String getTranslation(ITCTranslator translator);

    abstract void additionalTranslations();

    public static class TCEnUSLanguageProvider extends TCLanguageProvider {

        public TCEnUSLanguageProvider(DataGenerator gen) {
            super(gen, "en_us");
        }

        @Override
        protected void addTranslations() {
            TCLoggingUtils.startRegistry("En-US_Translations");
            super.addTranslations();
            TCLoggingUtils.completeRegistry("En-US_Translations");
        }

        @Override
        String getTranslation(ITCTranslator translator) {
            return translator.getEnUSName();
        }

        @Override
        void additionalTranslations() {
            this.add("takumicraft", "Takumi Craft");
            this.add("itemGroup.takumicraft", "Takumi Craft");
            this.add("itemGroup.takumicraft.egg", "Spawn Eggs");
            this.add("key.takumicraft.takumibook", "Open Takumi Book");
            this.add("takumicraft.takumibook", "Takumi Book");
            this.add("takumicraft.takumibook.search", "Search");
            this.add("takumicraft.takumibook.comp", "Complete: ");
            this.add("takumicraft.elem.takumi", "Takumi");
            this.add("takumicraft.elem.fire", "Fire");
            this.add("takumicraft.elem.grass", "Grass");
            this.add("takumicraft.elem.water", "Water");
            this.add("takumicraft.elem.wind", "Wind");
            this.add("takumicraft.elem.ground", "Ground");
            this.add("takumicraft.elem.normal", "Normal");
            this.add("takumicraft.elem.underfound", "???");
            this.add("takumicraft.elem.magic", "Magic");
            this.add("takumicraft.elem.dest", "Dest");
            this.add("takumicraft.rank.low", "Low");
            this.add("takumicraft.rank.mid", "Mid");
            this.add("takumicraft.rank.high", "High");
            this.add("takumicraft.rank.boss", "Boss");
            this.add("takumicraft.message.slay", "registered to TakumiBook.");
            this.add("takumicraft.attr.MD", "Magic/Dest");
            this.add("takumicraft.attr.M", "Magic");
            this.add("takumicraft.attr.D", "Dest");
            this.add("takumicraft.config.isdebugmode", "RECOMMEND TO SET THIS FALSE, this config is only for TakumiCraft debugger.");

            this.add("advancement.takumicraft.root.title", "Takumi Craft");
            this.add("advancement.takumicraft.root.desc", "Going into Chaotic Creeper World! \n Get damaged from explosion.");
            this.add("advancement.takumicraft.creeperbomb.title", "All You Need Is Bomb");
            this.add("advancement.takumicraft.creeperbomb.desc", "Got a CreeperBomb.");
            this.add("advancement.takumicraft.slay_all.title", "All Complete");
            this.add("advancement.takumicraft.slay_all.desc", "Complete the Creeper Book.");
            this.add("advancement.takumicraft.disarmament.title", "Disarmament");
            this.add("advancement.takumicraft.disarmament.desc", "Disarmament creeper's lighting armor.");
        }
    }

    public static class TCJaJPLanguageProvider extends TCLanguageProvider {

        public TCJaJPLanguageProvider(DataGenerator gen) {
            super(gen, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            TCLoggingUtils.startRegistry("Ja-JP_Translations");
            super.addTranslations();
            TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
                if (context.getJaJPRead() != null) {
                    this.add(TCEntityUtils.getEntityLangCode(context.entityType(), ".read"), context.getJaJPRead());
                }
            });
            TCLoggingUtils.completeRegistry("Ja-JP_Translations");
        }

        @Override
        String getTranslation(ITCTranslator translator) {
            return translator.getJaJPName();
        }

        @Override
        void additionalTranslations() {
            this.add("takumicraft", "???Craft");
            this.add("itemGroup.takumicraft", "???Craft");
            this.add("itemGroup.takumicraft.egg", "?????????????????????");
            this.add("key.takumicraft.takumibook", "?????????");
            this.add("takumicraft.takumibook", "?????????");
            this.add("takumicraft.takumibook.search", "????????? ??????");
            this.add("takumicraft.takumibook.comp", "??????: ");
            this.add("takumicraft.elem.takumi", "?????????");
            this.add("takumicraft.elem.fire", "?????????");
            this.add("takumicraft.elem.grass", "?????????");
            this.add("takumicraft.elem.water", "?????????");
            this.add("takumicraft.elem.wind", "?????????");
            this.add("takumicraft.elem.ground", "?????????");
            this.add("takumicraft.elem.normal", "?????????");
            this.add("takumicraft.elem.underfound", "???");
            this.add("takumicraft.elem.magic", "???");
            this.add("takumicraft.elem.dest", "???");
            this.add("takumicraft.rank.low", "??????");
            this.add("takumicraft.rank.mid", "??????");
            this.add("takumicraft.rank.high", "??????");
            this.add("takumicraft.rank.boss", "??????");
            this.add("takumicraft.message.slay", "??????????????????????????????");
            this.add("takumicraft.attr.MD", "???/???");
            this.add("takumicraft.attr.M", "???");
            this.add("takumicraft.attr.D", "???");
            this.add("takumicraft.config.isdebugmode", "????????????????????????????????????????????????true???????????????????????????");

            this.add("advancement.takumicraft.root.title", "???Craft");
            this.add("advancement.takumicraft.root.desc", "??????????????????????????????! \n ??????????????????????????????");
            this.add("advancement.takumicraft.creeperbomb.title", "???????????????");
            this.add("advancement.takumicraft.creeperbomb.desc", "???????????????????????????????????? \n ????????????????????????");
            this.add("advancement.takumicraft.slay_all.title", "????????????");
            this.add("advancement.takumicraft.slay_all.desc", "??????????????????\n ??????????????????????????????????????????");
            this.add("advancement.takumicraft.disarmament.title", "????????????");
            this.add("advancement.takumicraft.disarmament.desc", "??????????????????????????????\n????????????????????????????????????");
        }
    }
}
