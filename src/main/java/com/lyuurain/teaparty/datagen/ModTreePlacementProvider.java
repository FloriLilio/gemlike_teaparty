package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModTreePlacementProvider {
    public static final ResourceKey<PlacedFeature> LEMON_CHECKED =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemon_checked"));

    public static final ResourceKey<PlacedFeature> TEA_CHECKED =
            ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "tea_checked"));

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(
                context,
                LEMON_CHECKED,
                configuredFeatures.getOrThrow(ModTreeFeatureProvider.LEMON),
                PlacementUtils.filteredByBlockSurvival(ModBlocks.LEMON_SAPLING.get())
        );
        PlacementUtils.register(
                context,
                TEA_CHECKED,
                configuredFeatures.getOrThrow(ModTreeFeatureProvider.TEA),
                PlacementUtils.filteredByBlockSurvival(ModBlocks.TEA_SAPLING.get())
        );
    }
}
