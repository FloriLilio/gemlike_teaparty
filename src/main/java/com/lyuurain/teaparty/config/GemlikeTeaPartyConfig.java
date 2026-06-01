package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.annotations.RangedInt;
import net.darkhax.pricklemc.common.api.annotations.Value;

public class GemlikeTeaPartyConfig {
    @Value(comment = "The Y level entities are teleported to when the Reborn effect starts.")
    @RangedInt(min = -64, max = 1024)
    public int rebornTeleportHeight = 320;
}
