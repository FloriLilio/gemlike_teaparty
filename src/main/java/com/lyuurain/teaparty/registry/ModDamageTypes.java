package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> FUSION_SUICIDE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "fusion_suicide"));
    public static final ResourceKey<DamageType> SIREN_SCREAM = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "siren_scream"));
    public static final ResourceKey<DamageType> LEMON = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemon"));
}
