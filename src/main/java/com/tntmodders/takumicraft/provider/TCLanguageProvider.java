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

import java.util.HashMap;
import java.util.Map;

public abstract class TCLanguageProvider extends LanguageProvider {

    protected TCLanguageProvider(DataGenerator gen, String locale) {
        super(gen.getPackOutput(), TakumiCraftCore.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        this.additionalTranslations();
        TCBlockCore.BLOCKS.forEach(block -> {
            if (block instanceof ITCTranslator) {
                this.add(block, this.getTranslation((ITCTranslator) block));
            }
        });
        TCItemCore.ITEMS.forEach(item -> {
            if (item instanceof ITCTranslator) {
                this.add(item, this.getTranslation((ITCTranslator) item));
            }
        });
        TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
            this.add(TCEntityUtils.getEntityLangCode(context.entityType(), ""), this.getTranslation(context));
            this.add(TCEntityUtils.getEntityLangCode(context.entityType(), ".desc"), this instanceof TCJaJPLanguageProvider ? context.getJaJPDesc() : context.getEnUSDesc());
        });
        TCEnchantmentCore.ENCHANTMENTS.forEach(ench -> this.add(ench, this.getTranslation(ench)));
    }

    abstract String getTranslation(ITCTranslator translator);

    abstract void additionalTranslations();

    public static class TCEnUSLanguageProvider extends TCLanguageProvider {
        public static final Map<String, String> TC_ENUS_LANGMAP = new HashMap<>();

        public TCEnUSLanguageProvider(DataGenerator gen) {
            super(gen, "en_us");
        }

        @Override
        protected void addTranslations() {
            TCLoggingUtils.startRegistry("En-US_Translations");
            super.addTranslations();
            TCEntityCore.ENTITY_CONTEXTS.forEach(context -> {
                if (context.getJaJPRead() != null) {
                    this.add(TCEntityUtils.getEntityLangCode(context.entityType(), ".read"), context.getEnUSName());
                }
            });
            TCLoggingUtils.completeRegistry("En-US_Translations");
        }

        @Override
        String getTranslation(ITCTranslator translator) {
            return translator.getEnUSName();
        }

        @Override
        void additionalTranslations() {
            this.add("takumicraft", "Takumi Craft");
            this.add("item_group.takumicraft", "Takumi Craft");
            this.add("item_group.takumicraft.egg", "Takumi Spawn Eggs");
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

            this.add("takumicraft.color.white", "White");
            this.add("takumicraft.color.orange", "Orange");
            this.add("takumicraft.color.magenta", "Magenta");
            this.add("takumicraft.color.light_blue", "Light Blue");
            this.add("takumicraft.color.yellow", "Yellow");
            this.add("takumicraft.color.lime", "Lime");
            this.add("takumicraft.color.pink", "Pink");
            this.add("takumicraft.color.gray", "Gray");
            this.add("takumicraft.color.light_gray", "Light Gray");
            this.add("takumicraft.color.cyan", "Cyan");
            this.add("takumicraft.color.purple", "Purple");
            this.add("takumicraft.color.blue", "Blue");
            this.add("takumicraft.color.brown", "Brown");
            this.add("takumicraft.color.green", "Green");
            this.add("takumicraft.color.red", "Red");
            this.add("takumicraft.color.black", "Black");

            this.add("item.takumicraft.creepershield.white", "White Creeper Shield");
            this.add("item.takumicraft.creepershield.orange", "Orange Creeper Shield");
            this.add("item.takumicraft.creepershield.magenta", "Magenta Creeper Shield");
            this.add("item.takumicraft.creepershield.light_blue", "Light Blue Creeper Shield");
            this.add("item.takumicraft.creepershield.yellow", "Yellow Creeper Shield");
            this.add("item.takumicraft.creepershield.lime", "Lime Creeper Shield");
            this.add("item.takumicraft.creepershield.pink", "Pink Creeper Shield");
            this.add("item.takumicraft.creepershield.gray", "Gray Creeper Shield");
            this.add("item.takumicraft.creepershield.light_gray", "Light Gray Creeper Shield");
            this.add("item.takumicraft.creepershield.cyan", "Cyan Creeper Shield");
            this.add("item.takumicraft.creepershield.purple", "Purple Creeper Shield");
            this.add("item.takumicraft.creepershield.blue", "Blue Creeper Shield");
            this.add("item.takumicraft.creepershield.brown", "Brown Creeper Shield");
            this.add("item.takumicraft.creepershield.green", "Green Creeper Shield");
            this.add("item.takumicraft.creepershield.red", "Red Creeper Shield");
            this.add("item.takumicraft.creepershield.black", "Black Creeper Shield");

            this.add("advancement.takumicraft.root.title", "Takumi Craft");
            this.add("advancement.takumicraft.root.desc", "Going into Chaotic Creeper World! \n Get damaged from explosion.");
            this.add("advancement.takumicraft.creeperbomb.title", "All You Need Is Bomb");
            this.add("advancement.takumicraft.creeperbomb.desc", "Got a CreeperBomb.");
            this.add("advancement.takumicraft.slay_all.title", "All Complete");
            this.add("advancement.takumicraft.slay_all.desc", "Complete the Creeper Book.");
            this.add("advancement.takumicraft.disarmament.title", "Disarmament");
            this.add("advancement.takumicraft.disarmament.desc", "Disarmament creeper's lighting armor.");
            this.add("advancement.takumicraft.creepershield.title", "Super Shield");
            this.add("advancement.takumicraft.creepershield.desc", "Don't care about explosion.");
            this.add("advancement.takumicraft.rainbowsheep.title", "Rainbow Sheep");
            this.add("advancement.takumicraft.rainbowsheep.desc", "Slay rainbow Sheep Creeper.");
            this.add("advancement.takumicraft.takumialtar.title", "Altar of Creeper");
            this.add("advancement.takumicraft.takumialtar.desc", "Get Altar.");
            this.add("advancement.takumicraft.spmeat.title", "Super Duper Meat");
            this.add("advancement.takumicraft.spmeat.desc", "Get Exploded Meat");

            this.add("item.takumicraft.elementcore.desc", "Available as a smithing template for upgrades.");
            this.add("item.takumicraft.creeperbomb.desc", "Available as a smithing material for upgrades.");
            this.add("item.takumicraft.super_creeperbed.desc", "Texture Change: Use full-cube block.\nChange Reset: Sneaking right-click without item.");
            this.add("item.takumicraft.creepersword.desc", "A Great Sword of Creeper. One Slash, One Discharge.");
            this.add("item.takumicraft.minesweeper_tool.desc", "A Tool of Minesweeper. Made from Bomb, is it Safe?");
            this.add("item.takumicraft.creeperbow.desc", "A Compound Bow of Creeper. One Shot, One Blast.");
            this.add("item.takumicraft.creepershield.desc", "A Great Shield of Creeper. Made from Bomb, it is safe.");
            this.add("item.takumicraft.yukaridummy.desc", "Dummy");
            this.add("item.takumicraft.monsterbomb_yukari.desc", "Bomb");
            this.add("item.takumicraft.creeperaltar.desc.1", "Right Click to Summon Elder Creepers.");
            this.add("item.takumicraft.creeperaltar.desc.2", "----");

            this.add("entity.takumicraft.bedcreeper.message", "%s was blasted your respawn-point by bed creeper!");
            this.add("entity.takumicraft.returncreeper.message", "%s was teleported to your respawn-point by return creeper!");
            this.add("entity.takumicraft.sleepercreeper.message", "%s's respawn-point was borked by sleeper creeper!");
        }

        @Override
        public void add(String key, String value) {
            TC_ENUS_LANGMAP.put(key, value);
            super.add(key, value);
        }
    }

    public static class TCJaJPLanguageProvider extends TCLanguageProvider {
        public static final Map<String, String> TC_JAJP_LANGMAP = new HashMap<>();

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
            this.add("takumicraft", "匠Craft");
            this.add("item_group.takumicraft", "匠Craft");
            this.add("item_group.takumicraft.egg", "匠のスポーンエッグ");
            this.add("key.takumicraft.takumibook", "匠の書");
            this.add("takumicraft.takumibook", "匠の書");
            this.add("takumicraft.takumibook.search", "匠の書 検索");
            this.add("takumicraft.takumibook.comp", "進捗: ");
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
            this.add("takumicraft.message.slay", "が図鑑に登録された。");
            this.add("takumicraft.attr.MD", "魔/壊");
            this.add("takumicraft.attr.M", "魔");
            this.add("takumicraft.attr.D", "壊");
            this.add("takumicraft.config.isdebugmode", "デバッグモードで起動するときのみtrueに設定して下さい。");

            this.add("takumicraft.color.white", "白色");
            this.add("takumicraft.color.orange", "橙色");
            this.add("takumicraft.color.magenta", "赤紫");
            this.add("takumicraft.color.light_blue", "空色");
            this.add("takumicraft.color.yellow", "黄色");
            this.add("takumicraft.color.lime", "黄緑");
            this.add("takumicraft.color.pink", "桃色");
            this.add("takumicraft.color.gray", "灰色");
            this.add("takumicraft.color.light_gray", "灰白");
            this.add("takumicraft.color.cyan", "青緑");
            this.add("takumicraft.color.purple", "紫色");
            this.add("takumicraft.color.blue", "青色");
            this.add("takumicraft.color.brown", "茶色");
            this.add("takumicraft.color.green", "緑色");
            this.add("takumicraft.color.red", "赤色");
            this.add("takumicraft.color.black", "黒色");

            this.add("item.takumicraft.creepershield.white", "匠式防盾[白]");
            this.add("item.takumicraft.creepershield.orange", "匠式防盾[橙]");
            this.add("item.takumicraft.creepershield.magenta", "匠式防盾[赤紫]");
            this.add("item.takumicraft.creepershield.light_blue", "匠式防盾[空]");
            this.add("item.takumicraft.creepershield.yellow", "匠式防盾[黄]");
            this.add("item.takumicraft.creepershield.lime", "匠式防盾[黄緑]");
            this.add("item.takumicraft.creepershield.pink", "匠式防盾[桃]");
            this.add("item.takumicraft.creepershield.gray", "匠式防盾[灰]");
            this.add("item.takumicraft.creepershield.light_gray", "匠式防盾[灰白]");
            this.add("item.takumicraft.creepershield.cyan", "匠式防盾[青緑]");
            this.add("item.takumicraft.creepershield.purple", "匠式防盾[紫]");
            this.add("item.takumicraft.creepershield.blue", "匠式防盾[青]");
            this.add("item.takumicraft.creepershield.brown", "匠式防盾[茶]");
            this.add("item.takumicraft.creepershield.green", "匠式防盾[緑]");
            this.add("item.takumicraft.creepershield.red", "匠式防盾[赤]");
            this.add("item.takumicraft.creepershield.black", "匠式防盾[黒]");

            this.add("advancement.takumicraft.root.title", "匠Craft");
            this.add("advancement.takumicraft.root.desc", "この匠だらけの世界へ!\n\n爆発の洗礼を受ける。");
            this.add("advancement.takumicraft.creeperbomb.title", "爆発物注意");
            this.add("advancement.takumicraft.creeperbomb.desc", "匠式高性能爆弾を入手する\n\n全ては爆弾から。");
            this.add("advancement.takumicraft.slay_all.title", "完全覇者");
            this.add("advancement.takumicraft.slay_all.desc", "匠の書の制覇\n\n王の創る次なる匠を待たれよ。");
            this.add("advancement.takumicraft.disarmament.title", "武装解除");
            this.add("advancement.takumicraft.disarmament.desc", "巨匠化状態を解除する\n\n怖いものなし、匠の狩人。");
            this.add("advancement.takumicraft.creepershield.title", "爆風もへっちゃら");
            this.add("advancement.takumicraft.creepershield.desc", "匠式防盾を入手する\n\n(見た目以外は)完璧な盾だ。");
            this.add("advancement.takumicraft.rainbowsheep.title", "幻の虹色");
            this.add("advancement.takumicraft.rainbowsheep.desc", "虹の羊匠を倒す\n\n見つけたら幸せになれるかも?");
            this.add("advancement.takumicraft.takumialtar.title", "祭壇と篝火");
            this.add("advancement.takumicraft.takumialtar.desc", "王匠の祭壇を入手する\n\n強者を召喚せよ。");
            this.add("advancement.takumicraft.spmeat.title", "匠印の特上肉");
            this.add("advancement.takumicraft.spmeat.desc", "全ての匠式特上肉を食べる\n\n肉!!\n美味すぎるだろ!!\n反省しろ!!");

            this.add("item.takumicraft.elementcore.desc", "鍛冶型として鍛冶台でのアップグレードに利用可能");
            this.add("item.takumicraft.creeperbomb.desc", "材料として鍛冶台でのアップグレードに利用可能");
            this.add("item.takumicraft.super_creeperbed.desc", "テクスチャ変更: 立方体ブロックで右クリック\n変更解除: 手になにも持たずにスニーク右クリック");
            this.add("item.takumicraft.creepersword.desc", "漲る力を与える匠の銘剣。その一振りは秩序を薙ぐ。");
            this.add("item.takumicraft.minesweeper_tool.desc", "爆発物を安全に破壊できるツール。爆弾から作ったけど大丈夫……?");
            this.add("item.takumicraft.creeperbow.desc", "爆ぜる矢を撃つ匠の名弓。その一穿ちは混沌を齎す。");
            this.add("item.takumicraft.creepershield.desc", "爆発を防ぐ匠印の大盾。爆弾から作ったけど大丈夫。");
            this.add("item.takumicraft.yukaridummy.desc", "非爆発性ゆかりさん");
            this.add("item.takumicraft.monsterbomb_yukari.desc", "爆発性ゆかりさん");
            this.add("item.takumicraft.creeperaltar.desc.1", "王者と召人を喚ぶ祭壇。");
            this.add("item.takumicraft.creeperaltar.desc.2", "右クリックで上位匠を召喚できる。");

            this.add("entity.takumicraft.bedcreeper.message", "%sのリスポーン地点が寝台匠に爆破された!");
            this.add("entity.takumicraft.returncreeper.message", "%sは帰匠によってリスポーン地点へ転移した!");
            this.add("entity.takumicraft.sleepercreeper.message", "%sのリスポーン地点は閨匠の悪戯を受けた!");
        }

        @Override
        public void add(String key, String value) {
            TC_JAJP_LANGMAP.put(key, value);
            super.add(key, value);
        }
    }
}
