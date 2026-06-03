package com.lyuurain.teaparty.config;

import net.darkhax.pricklemc.common.api.annotations.Array;
import net.darkhax.pricklemc.common.api.annotations.RangedDouble;
import net.darkhax.pricklemc.common.api.annotations.RangedFloat;
import net.darkhax.pricklemc.common.api.annotations.RangedInt;
import net.darkhax.pricklemc.common.api.annotations.Value;

public class GemlikeTeaPartyCommonConfig {
    @Value(comment = "The End Vision duration provided by End Vision, in ticks.")
    @RangedInt(min = 0, max = 72000)
    public int endVisionDuration = 1200;

    @Value(comment = "The End Vision outline radius, in blocks.")
    @RangedInt(min = 0, max = 64)
    public int endVisionRadius = 32;

    @Value(comment = "End Vision block filter mode: whitelist or blacklist.")
    public String endVisionBlockFilterMode = "whitelist";

    @Value(comment = "Block ids controlled by the End Vision block filter mode. Use resource ids like minecraft:chest.")
    @Array(allowEmpty = true)
    public String[] endVisionBlockFilter = {"minecraft:chest", "minecraft:barrel", "minecraft:trapped_chest", "minecraft:iron_ore", "minecraft:deepslate_iron_ore", "minecraft:gold_ore", "minecraft:deepslate_gold_ore", "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore", "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore", "minecraft:ancient_debris"};

    @Value(comment = "Whether End Vision outlines living entities.")
    public boolean endVisionShowEntities = true;

    @Value(comment = "End Vision entity filter mode: whitelist or blacklist.")
    public String endVisionEntityFilterMode = "blacklist";

    @Value(comment = "Entity type ids controlled by the End Vision entity filter mode. Use resource ids like minecraft:zombie.")
    @Array(allowEmpty = true)
    public String[] endVisionEntityFilter = {};

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

    @Value(comment = "The Fusion duration provided by Underground Sun, in ticks.")
    @RangedInt(min = 0, max = 72000)
    public int undergroundSunFusionDuration = 600;

    @Value(comment = "The explosion radius when a Fusion holder breaks a block.")
    @RangedFloat(min = 0.0F, max = 128.0F)
    public float fusionBlockExplosionRadius = 8.0F;

    @Value(comment = "The explosion radius when a Fusion holder attacks a living entity.")
    @RangedFloat(min = 0.0F, max = 128.0F)
    public float fusionAttackExplosionRadius = 12.0F;

    @Value(comment = "The explosion radius when Fusion naturally expires.")
    @RangedFloat(min = 0.0F, max = 128.0F)
    public float fusionSelfExplosionRadius = 8.0F;

    @Value(comment = "Fusion warning sound mode: off, once, or loop.")
    public String fusionWarningSoundMode = "loop";

    @Value(comment = "The Lies Rhyme duration provided by Siren's Dew, in ticks.")
    @RangedInt(min = 0, max = 72000)
    public int liesRhymeDuration = 600;

    @Value(comment = "The radius used by Lies Rhyme to grant ally buffs after drinking Siren's Dew, in blocks.")
    @RangedDouble(min = 0.0D, max = 128.0D)
    public double sirensDewBuffRadius = 8.0D;

    @Value(comment = "The radius used by Lies Rhyme to damage and debuff entities when it ends, in blocks.")
    @RangedDouble(min = 0.0D, max = 128.0D)
    public double liesRhymeEndDebuffRadius = 8.0D;

    @Value(comment = "The time before Lies Rhyme ends when the warning radius is shown, in ticks. Use -1 to show it for the full duration.")
    @RangedInt(min = -1, max = 72000)
    public int liesRhymeWarningRangeDisplayTime = 100;

    @Value(comment = "Lies Rhyme max health reduction mode: fixed or percentage.")
    public String liesRhymeHealthReductionMode = "fixed";

    @Value(comment = "The max health percentage removed by Lies Rhyme when the mode is percentage. 0.3 means 30%.")
    @RangedDouble(min = 0.0D, max = 1.0D)
    public double liesRhymeHealthReductionPercentage = 0.3D;

    @Value(comment = "The max health amount removed by Lies Rhyme when the mode is fixed.")
    @RangedDouble(min = 0.0D, max = 1024.0D)
    public double liesRhymeHealthReductionAmount = 6.0D;

    @Value(comment = "The damage dealt by Lies Rhyme when it ends.")
    @RangedFloat(min = 0.0F, max = 1000.0F)
    public float liesRhymeEndDamage = 10.0F;

    @Value(comment = "The maximum magic bottle count a player can hold.")
    @RangedInt(min = 1, max = 1000000)
    public int maxMagicBottleCount = 640;

    @Value(comment = "The time required to shake the mixing cup, in ticks.")
    @RangedInt(min = 1, max = 72000)
    public int shakeTime = 40;

    @Value(comment = "The time required to stir the mixing cup, in ticks.")
    @RangedInt(min = 1, max = 72000)
    public int stirTime = 40;
}
