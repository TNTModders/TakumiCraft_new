package com.tntmodders.takumicraft.core;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class TCConfigCore {

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TCCommonConfig.commonSpec);
    }

    public static void registerSpawnConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TCSpawnConfig.spawnSpec);
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
        public final ForgeConfigSpec.DoubleValue spawnProtectionRadius;

        public TCCommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("TakumiCraft common configuration settings").push("common");

            this.isDebugMode = builder
                    .comment("RECOMMEND TO SET THIS FALSE, this config is only for TakumiCraft debugger.")
                    .translation("takumicraft.config.isdebugmode")
                    .worldRestart()
                    .define("debugmode", false);

            this.spawnProtectionRadius = builder
                    .comment("Config for protect around spawn area. Factor defined to be double, 0 without protection, 1 with full protection on spawn protection set by server.")
                    .translation("takumicraft.config.spawnprotection")
                    .worldRestart()
                    .defineInRange("spawnprotection", 0d, 0d, 1d);

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

    public static class TCSpawnConfig {
        public static final TCSpawnConfig SPAWN;
        static final ForgeConfigSpec spawnSpec;

        static {
            final Pair<TCSpawnConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                    .configure(TCSpawnConfig::new);
            spawnSpec = pair.getRight();
            SPAWN = pair.getLeft();
        }

        public final ForgeConfigSpec.DoubleValue generalSpawnFactor;
        public final Map<EntityType<?>, ForgeConfigSpec.DoubleValue> creeperSpawnFactors = new HashMap<>();

        public TCSpawnConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("TakumiCraft spawn configuration factor").push("spawn");
            this.generalSpawnFactor = builder
                    .comment("Spawn factor for all takumicraft creeper.")
                    .translation("takumicraft.config.server.spawnfactor_general")
                    .worldRestart()
                    .defineInRange("generalSpawnFactor", 1.0, 0, 10);

            TCEntityCore.ENTITY_TYPES.forEach(type -> {
                String name = type.getDescriptionId().replace("entity.takumicraft.", "");
                this.creeperSpawnFactors.put(type, builder
                        .comment("spawn factor for " + name + ".")
                        .translation("takumicraft.config.server.spawnfactor_" + name)
                        .worldRestart()
                        .defineInRange(name + "SpawnFactor", 1.0, 0, 10));

            });
            builder.pop();
        }
    }
}
