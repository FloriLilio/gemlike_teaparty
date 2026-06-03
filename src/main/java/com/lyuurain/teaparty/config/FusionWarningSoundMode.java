package com.lyuurain.teaparty.config;

public enum FusionWarningSoundMode {
    OFF("off"),
    ONCE("once"),
    LOOP("loop");

    private final String configValue;

    FusionWarningSoundMode(String configValue) {
        this.configValue = configValue;
    }

    public String configValue() {
        return this.configValue;
    }

    public static FusionWarningSoundMode fromConfig(String value) {
        for (FusionWarningSoundMode mode : values()) {
            if (mode.configValue.equalsIgnoreCase(value)) {
                return mode;
            }
        }

        return LOOP;
    }
}
