package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class GemlikeTeaPartyClientConfig {
    @Value(comment = "End Vision outline color as a six-digit hex RGB value.")
    public String endVisionOutlineColor = "fcc9ff";

    @Value(comment = "Whether Frozen shows the vanilla first-person powder snow overlay.")
    public boolean showFrozenFirstPersonOverlay = true;

    @Value(comment = "Whether Frozen renders an ice block under frozen entities.")
    public boolean showFrozenIceBlock = true;

    @Value(comment = "Whether Frozen tints frozen players blue.")
    public boolean tintFrozenPlayers = true;

    @Value(comment = "The HUD display position of the magic bottle count: left, right, or top.")
    public String magicBottleHudPosition = "left";
}
