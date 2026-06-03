package com.lyuurain.teaparty.config;

public enum EndVisionFilterMode {
    WHITELIST("whitelist"),
    BLACKLIST("blacklist");

    private final String configValue;

    EndVisionFilterMode(String configValue) {
        this.configValue = configValue;
    }

    public String configValue() {
        return this.configValue;
    }

    public static EndVisionFilterMode fromConfig(String value, EndVisionFilterMode fallback) {
        for (EndVisionFilterMode mode : values()) {
            if (mode.configValue.equalsIgnoreCase(value)) {
                return mode;
            }
        }

        return fallback;
    }
}
