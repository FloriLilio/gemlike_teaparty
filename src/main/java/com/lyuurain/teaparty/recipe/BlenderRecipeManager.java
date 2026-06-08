package com.lyuurain.teaparty.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lyuurain.teaparty.GemlikeTeaParty;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlenderRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final BlenderRecipeManager INSTANCE = new BlenderRecipeManager();

    private List<BlenderRecipe> recipes = new ArrayList<>();

    private BlenderRecipeManager() {
        super(GSON, "teaparty_blender_recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<BlenderRecipe> newList = new ArrayList<>();
        object.forEach((location, jsonElement) -> {
            try {
                BlenderRecipe.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .resultOrPartial(err -> GemlikeTeaParty.LOGGER.error("Failed to parse blender recipe json {}: {}", location, err))
                        .ifPresent(recipe -> newList.add(recipe.withId(location)));
            } catch (Exception e) {
                GemlikeTeaParty.LOGGER.error("Failed to load blender recipe json {}: {}", location, e.getMessage());
            }
        });
        this.recipes = newList;
        GemlikeTeaParty.LOGGER.info("Loaded {} blender recipes.", this.recipes.size());
    }

    public List<BlenderRecipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<BlenderRecipe> recipes) {
        this.recipes = recipes;
    }

    public static BlenderRecipe findMatch(NonNullList<ItemStack> inputItems, ResourceLocation liquidId, int liquidCount) {
        for (BlenderRecipe recipe : INSTANCE.recipes) {
            if (recipe.match(inputItems, liquidId, liquidCount) != null) {
                return recipe;
            }
        }
        return null;
    }
}
