package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.config.ConfigManager;

public class ModConfig {
    public static final GemlikeTeaPartyConfig COMMON = ConfigManager.load("gemlike_teaparty", new GemlikeTeaPartyConfig());

    public static void load() {
    }
}
