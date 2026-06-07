package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDamageTypeProvider {

    public static final ResourceKey<DamageType> FUSION_SUICIDE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "fusion_suicide"));
    public static final ResourceKey<DamageType> LEMON = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemon"));
    public static final ResourceKey<DamageType> SIREN_SCREAM = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "siren_scream"));

    public static void bootstrap(net.minecraft.data.worldgen.BootstrapContext<DamageType> context) {
        context.register(FUSION_SUICIDE, new DamageType("fusion_suicide", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(LEMON, new DamageType("lemon", 0.1F));
        context.register(SIREN_SCREAM, new DamageType("siren_scream", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
    }
}
