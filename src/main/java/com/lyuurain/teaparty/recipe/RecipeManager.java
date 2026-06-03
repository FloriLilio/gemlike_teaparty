package com.lyuurain.teaparty.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lyuurain.teaparty.GemlikeTeaParty;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final RecipeManager INSTANCE = new RecipeManager();

    private List<MixingCupRecipe> recipes = new ArrayList<>();

    private RecipeManager() {
        super(GSON, "teaparty_recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<MixingCupRecipe> newList = new ArrayList<>();
        object.forEach((location, jsonElement) -> {
            try {
                MixingCupRecipe.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .resultOrPartial(err -> GemlikeTeaParty.LOGGER.error("Failed to parse recipe json {}: {}", location, err))
                        .ifPresent(recipe -> newList.add(recipe.withId(location)));
            } catch (Exception e) {
                GemlikeTeaParty.LOGGER.error("Failed to load recipe json {}: {}", location, e.getMessage());
            }
        });
        this.recipes = newList;
        GemlikeTeaParty.LOGGER.info("Loaded {} recipes.", this.recipes.size());
    }

    public List<MixingCupRecipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<MixingCupRecipe> recipes) {
        this.recipes = recipes;
    }

    public static MixingCupRecipe findMatchingRecipe(List<MixingCupProcess> processes) {
        for (MixingCupRecipe recipe : INSTANCE.recipes) {
            if (recipe.matches(processes)) {
                return recipe;
            }
        }
        return null;
    }
}
