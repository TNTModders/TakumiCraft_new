package com.tntmodders.takumicraft.utils;

import static com.tntmodders.takumicraft.TakumiCraftCore.TC_LOGGER;

public class TCLoggingUtils {
    public static void startRegistry(String type) {
        TC_LOGGER.info("TakumiCraft_" + type + "Registry ::");
    }

    public static void completeRegistry(String type) {
        TC_LOGGER.info(":: TakumiCraft_" + type + "Registry Complete");
    }

    public static void entryRegistry(String type, String entryName) {
        TC_LOGGER.info("TC Registered " + type + ": " + entryName);
    }
}
