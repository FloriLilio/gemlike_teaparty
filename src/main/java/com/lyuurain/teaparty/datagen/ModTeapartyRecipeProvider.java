package com.lyuurain.teaparty.datagen;

import com.google.gson.JsonElement;
import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.MixingCupProcess;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import com.lyuurain.teaparty.recipe.MixingCupRecipe.RecipeStep;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ModTeapartyRecipeProvider implements DataProvider {
    private final PackOutput packOutput;

    public ModTeapartyRecipeProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    protected abstract void buildRecipes(Map<ResourceLocation, MixingCupRecipe> consumer);

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceLocation, MixingCupRecipe> map = new HashMap<>();
        buildRecipes(map);
        
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            MixingCupRecipe def = entry.getValue();
            
            Path path = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(id.getNamespace()).resolve("teaparty_recipes").resolve(id.getPath() + ".json");
            
            JsonElement json = MixingCupRecipe.CODEC.encodeStart(JsonOps.INSTANCE, def).getOrThrow();
            return DataProvider.saveStable(cachedOutput, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Gem-like Tea Party Custom Recipes";
    }
}
