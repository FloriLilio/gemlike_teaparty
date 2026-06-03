package com.lyuurain.teaparty.config;

public enum SirenHealthReductionMode {
    FIXED("fixed"),
    PERCENTAGE("percentage");

    private final String configValue;

    SirenHealthReductionMode(String configValue) {
        this.configValue = configValue;
    }

    public String configValue() {
        return this.configValue;
    }

    public static SirenHealthReductionMode fromConfig(String value) {
        for (SirenHealthReductionMode mode : values()) {
            if (mode.configValue.equalsIgnoreCase(value)) {
                return mode;
            }
        }

        return FIXED;
    }
}
