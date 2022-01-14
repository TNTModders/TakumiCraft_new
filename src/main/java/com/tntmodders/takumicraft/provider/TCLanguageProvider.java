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
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> this.add(context.entityType(), this.getTranslation(context)));
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
        }
    }
}
