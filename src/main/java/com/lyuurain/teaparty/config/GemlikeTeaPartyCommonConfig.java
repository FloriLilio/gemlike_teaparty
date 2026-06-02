package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.annotations.Array;
import net.darkhax.pricklemc.common.api.annotations.RangedDouble;
import net.darkhax.pricklemc.common.api.annotations.RangedFloat;
import net.darkhax.pricklemc.common.api.annotations.RangedInt;
import net.darkhax.pricklemc.common.api.annotations.Value;

public class GemlikeTeaPartyCommonConfig {
    @Value(comment = "The Y level entities are teleported to when Dreamy Sky takes effect.")
    @RangedInt(min = -64, max = 1024)
    public int dreamySkyTeleportHeight = 320;

    @Value(comment = "Whether Dreamy Sky checks the top block at the user's X/Z position and disables itself when that block is unbreakable.")
    public boolean dreamySkyCheckTopBlock = true;

    @Value(comment = "Whether Dreamy Sky spawns ender particles at the user's original position when it takes effect.")
    public boolean showDreamySkyParticles = true;

    @Value(comment = "Dimensions where Dreamy Sky is disabled. Use resource ids like minecraft:the_nether.")
    @Array(allowEmpty = true)
    public String[] dreamySkyDisabledDimensions = {};

    @Value(comment = "Whether Glacier shows its effect radius particles while drinking.")
    public boolean showGlacierRange = true;

    @Value(comment = "The radius used by Gelid and Perfect Frozen to apply Frozen, in blocks.")
    @RangedDouble(min = 0.0D, max = 128.0D)
    public double glacierFreezeRadius = 9.0D;

    @Value(comment = "The Frozen duration applied by Gelid and Perfect Frozen, in ticks.")
    @RangedInt(min = 0, max = 72000)
    public int gelidFrozenDuration = 180;

    @Value(comment = "The Slowness duration applied after Frozen ends, in ticks.")
    @RangedInt(min = 0, max = 72000)
    public int frozenSlownessDuration = 180;

    @Value(comment = "The Gelid or Perfect Frozen duration provided by Glacier, in ticks.")
    @RangedInt(min = 0, max = 72000)
    public int glacierGelidDuration = 1800;

    @Value(comment = "Whether Frozen skeletons can turn into strays when Frozen naturally ends.")
    public boolean allowSkeletonToStrayConversion = true;

    @Value(comment = "Dimensions where Glacier effects are disabled and existing Gelid, Perfect Frozen, or Frozen effects are cleared. Use resource ids like minecraft:the_nether.")
    @Array(allowEmpty = true)
    public String[] disabledGlacierDimensions = {"minecraft:the_nether"};

    @Value(comment = "Entity types immune to Frozen from Gelid and Perfect Frozen. Use resource ids like minecraft:stray.")
    @Array(allowEmpty = true)
    public String[] frozenImmuneEntities = {"minecraft:stray", "minecraft:snow_golem", "minecraft:polar_bear"};

    @Value(comment = "Periodic freeze damage dealt by Frozen to entity types in minecraft:freeze_hurts_extra_types. Vanilla multiplies freeze damage to these entities by 5, so 0.4 displays as about 2 damage.")
    @RangedFloat(min = 0.0F, max = 1000.0F)
    public float frozenDamage = 0.4F;

    @Value(comment = "Whether Glacier names baka, cirno, and ⑨ trigger the Perfect Frozen easter egg.")
    public boolean allowGlacierEasterEgg = true;
}
