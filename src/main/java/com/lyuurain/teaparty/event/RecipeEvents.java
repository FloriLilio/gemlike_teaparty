package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.network.SyncBlenderRecipesPayload;
import com.lyuurain.teaparty.network.SyncLiquidsPayload;
import com.lyuurain.teaparty.network.SyncRecipesPayload;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import com.lyuurain.teaparty.recipe.BlenderRecipeManager;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import com.lyuurain.teaparty.recipe.RecipeManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class RecipeEvents {

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(LiquidManager.INSTANCE);
        event.addListener(RecipeManager.INSTANCE);
        event.addListener(BlenderRecipeManager.INSTANCE);
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            syncToPlayer(event.getPlayer());
        } else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                syncToPlayer(player);
            }
        }
    }

    private static void syncToPlayer(ServerPlayer player) {
        List<LiquidDefinition> liquids = new ArrayList<>(LiquidManager.INSTANCE.getLiquids().values());
        PacketDistributor.sendToPlayer(player, new SyncLiquidsPayload(liquids));

        List<MixingCupRecipe> recipes = new ArrayList<>(RecipeManager.INSTANCE.getRecipes());
        PacketDistributor.sendToPlayer(player, new SyncRecipesPayload(recipes));

        List<BlenderRecipe> blenderRecipes = new ArrayList<>(BlenderRecipeManager.INSTANCE.getRecipes());
        PacketDistributor.sendToPlayer(player, new SyncBlenderRecipesPayload(blenderRecipes));
    }
}
