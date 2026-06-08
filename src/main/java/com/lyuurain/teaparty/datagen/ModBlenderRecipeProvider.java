package com.lyuurain.teaparty.datagen;

import com.google.gson.JsonElement;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ModBlenderRecipeProvider implements DataProvider {
    private final PackOutput packOutput;

    public ModBlenderRecipeProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    protected abstract void buildRecipes(Map<ResourceLocation, BlenderRecipe> consumer);

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceLocation, BlenderRecipe> map = new HashMap<>();
        buildRecipes(map);

        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            BlenderRecipe recipe = entry.getValue();

            Path path = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(id.getNamespace())
                    .resolve("teaparty_blender_recipes")
                    .resolve(id.getPath() + ".json");

            JsonElement json = BlenderRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow();
            return DataProvider.saveStable(cachedOutput, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Gem-like Tea Party Blender Recipes";
    }
}
