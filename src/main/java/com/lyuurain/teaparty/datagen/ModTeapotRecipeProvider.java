package com.lyuurain.teaparty.datagen;

import com.google.gson.JsonElement;
import com.lyuurain.teaparty.recipe.TeapotRecipe;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ModTeapotRecipeProvider implements DataProvider {
    private final PackOutput packOutput;

    public ModTeapotRecipeProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    protected abstract void buildRecipes(Map<ResourceLocation, TeapotRecipe> consumer);

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceLocation, TeapotRecipe> map = new HashMap<>();
        buildRecipes(map);

        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            TeapotRecipe recipe = entry.getValue();

            Path path = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(id.getNamespace())
                    .resolve("teaparty_teapot_recipes")
                    .resolve(id.getPath() + ".json");

            JsonElement json = TeapotRecipe.CODEC.encodeStart(JsonOps.INSTANCE, recipe).getOrThrow();
            return DataProvider.saveStable(cachedOutput, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Gem-like Tea Party Teapot Recipes";
    }
}
