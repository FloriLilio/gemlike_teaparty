package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class GemlikeTeaPartyClientConfig {
    @Value(comment = "Whether Frozen shows the vanilla first-person powder snow overlay.")
    public boolean showFrozenFirstPersonOverlay = true;

    @Value(comment = "Whether Frozen renders an ice block under frozen entities.")
    public boolean showFrozenIceBlock = true;

    @Value(comment = "Whether Frozen tints frozen players blue.")
    public boolean tintFrozenPlayers = true;
}
