package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.config.ConfigManager;

public class ModConfig {
    public static final ConfigManager<GemlikeTeaPartyCommonConfig> COMMON_MANAGER = ConfigManager.init("gemlike_teaparty", new GemlikeTeaPartyCommonConfig());
    public static final ConfigManager<GemlikeTeaPartyClientConfig> CLIENT_MANAGER = ConfigManager.init("gemlike_teaparty-client", new GemlikeTeaPartyClientConfig());
    public static final GemlikeTeaPartyCommonConfig COMMON = COMMON_MANAGER.get();
    public static final GemlikeTeaPartyClientConfig CLIENT = CLIENT_MANAGER.get();

    public static void load() {
        COMMON_MANAGER.load();
        CLIENT_MANAGER.load();
    }

    public static void saveCommon() {
        COMMON_MANAGER.save();
    }

    public static void saveClient() {
        CLIENT_MANAGER.save();
    }

    public static void saveAll() {
        saveCommon();
        saveClient();
    }
}
