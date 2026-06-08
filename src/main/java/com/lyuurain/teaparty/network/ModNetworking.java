package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.config.EndVisionConfigValues;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GemlikeTeaParty.MODID).versioned("1");
        registrar.commonToClient(EndVisionConfigPayload.TYPE, EndVisionConfigPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> EndVisionConfigValues.update(payload)));
        registrar.playToClient(MagicBottleSyncPayload.TYPE, MagicBottleSyncPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> com.lyuurain.teaparty.client.ClientMagicBottleCache.update(payload.count())));
        registrar.playToClient(SyncMagicBottleZeroPayload.TYPE, SyncMagicBottleZeroPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> com.lyuurain.teaparty.client.ClientMagicBottleCache.forceTriggerZero()));

        registrar.playToClient(SyncLiquidsPayload.TYPE, SyncLiquidsPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            java.util.Map<net.minecraft.resources.ResourceLocation, com.lyuurain.teaparty.recipe.LiquidDefinition> map = new java.util.HashMap<>();
            for (com.lyuurain.teaparty.recipe.LiquidDefinition def : payload.liquids()) {
                map.put(def.id(), def);
            }
            com.lyuurain.teaparty.recipe.LiquidManager.INSTANCE.setLiquids(map);
        }));

        registrar.playToClient(SyncRecipesPayload.TYPE, SyncRecipesPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            com.lyuurain.teaparty.recipe.RecipeManager.INSTANCE.setRecipes(payload.recipes());
            com.lyuurain.teaparty.client.jei.TeaPartyJeiPlugin.onMixingCupRecipesSynced(payload.recipes());
        }));

        registrar.playToClient(SyncBlenderRecipesPayload.TYPE, SyncBlenderRecipesPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            com.lyuurain.teaparty.recipe.BlenderRecipeManager.INSTANCE.setRecipes(payload.recipes());
            com.lyuurain.teaparty.client.jei.TeaPartyJeiPlugin.onBlenderRecipesSynced(payload.recipes());
        }));

        registrar.playToClient(SyncTeapotRecipesPayload.TYPE, SyncTeapotRecipesPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            com.lyuurain.teaparty.recipe.TeapotRecipeManager.INSTANCE.setRecipes(payload.recipes());
            com.lyuurain.teaparty.client.jei.TeaPartyJeiPlugin.onTeapotRecipesSynced(payload.recipes());
        }));
    }
}
