package com.tntmodders.takumicraft.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class TCConfigCore {

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TCCommonConfig.commonSpec);
    }

    public static class TCCommonConfig {
        public static final TCCommonConfig COMMON;
        static final ForgeConfigSpec commonSpec;

        static {
            final Pair<TCCommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                    .configure(TCCommonConfig::new);
            commonSpec = pair.getRight();
            COMMON = pair.getLeft();
        }

        public final ForgeConfigSpec.BooleanValue isDebugMode;

        public TCCommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("TakumiCraft common configuration settings").push("common");

            this.isDebugMode = builder
                    .comment("RECOMMEND TO SET THIS FALSE, this config is only for TakumiCraft debugger.")
                    .translation("takumicraft.config.isdebugmode")
                    .worldRestart()
                    .define("isDebugMode", false);

            builder.pop();
        }
    }

    public static class TCServerConfig {
        public static final TCServerConfig SERVER;
        static final ForgeConfigSpec serverSpec;

        static {
            final Pair<TCServerConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                    .configure(TCServerConfig::new);
            serverSpec = pair.getRight();
            SERVER = pair.getLeft();
        }


        public TCServerConfig(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class TCClientConfig {
        public static final TCClientConfig CLIENT;
        static final ForgeConfigSpec clientSpec;

        static {
            final Pair<TCClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                    .configure(TCClientConfig::new);
            clientSpec = pair.getRight();
            CLIENT = pair.getLeft();
        }


        public TCClientConfig(ForgeConfigSpec.Builder builder) {

        }
    }
}
