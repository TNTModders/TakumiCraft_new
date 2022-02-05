package com.tntmodders.takumicraft.provider;

import com.tntmodders.takumicraft.TakumiCraftCore;
import com.tntmodders.takumicraft.core.TCBlockCore;
import com.tntmodders.takumicraft.core.TCEntityCore;
import com.tntmodders.takumicraft.core.TCItemCore;
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
            this.add(context.entityType(), this.getTranslation(context));
            this.add(context.entityType().getDescriptionId() + ".desc", this instanceof TCJaJPLanguageProvider ? context.getJaJPDesc() : context.getEnUSDesc());
        });
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
            this.add("itemGroup.takumicraft", "Takumi Craft");
            this.add("itemGroup.takumicraft.egg", "Spawn Eggs");
            this.add("key.takumicraft.takumibook", "Open Takumi Book");
            this.add("takumicraft.takumibook", "Takumi Book");
            this.add("takumicraft.takumibook.search", "Search");
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
                    this.add(context.entityType().getDescriptionId() + ".read", context.getJaJPRead());
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
            this.add("itemGroup.takumicraft", "匠Craft");
            this.add("itemGroup.takumicraft.egg", "スポーンエッグ");
            this.add("key.takumicraft.takumibook", "匠の書");
            this.add("takumicraft.takumibook", "匠の書");
            this.add("takumicraft.takumibook.search", "匠の書 検索");
            this.add("takumicraft.elem.takumi", "匠属性");
            this.add("takumicraft.elem.fire", "火属性");
            this.add("takumicraft.elem.grass", "草属性");
            this.add("takumicraft.elem.water", "水属性");
            this.add("takumicraft.elem.wind", "風属性");
            this.add("takumicraft.elem.ground", "地属性");
            this.add("takumicraft.elem.normal", "無属性");
            this.add("takumicraft.elem.underfound", "???");
            this.add("takumicraft.elem.magic", "魔");
            this.add("takumicraft.elem.dest", "壊");
            this.add("takumicraft.rank.low", "下位");
            this.add("takumicraft.rank.mid", "中位");
            this.add("takumicraft.rank.high", "上位");
            this.add("takumicraft.rank.boss", "王位");
        }
    }
}
