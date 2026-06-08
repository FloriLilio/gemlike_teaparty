package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlenderRecipeProvider extends ModBlenderRecipeProvider {
    public BlenderRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Map<ResourceLocation, BlenderRecipe> map) {
        addBlueberryJuice(map);
        addIceCubes(map);
    }

    private void addBlueberryJuice(Map<ResourceLocation, BlenderRecipe> map) {
        var ingredients = List.of(
            new BlenderRecipe.IngredientSpec(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "blueberry"), 0.333F, 0.667F),
            new BlenderRecipe.IngredientSpec(ResourceLocation.withDefaultNamespace("sugar"), 0.0F, 0.167F)
        );
        var liquids = List.of(
            new BlenderRecipe.LiquidSpec(ResourceLocation.withDefaultNamespace("water"), 0.0F, 1.0F)
        );
        var output = new BlenderRecipe.LiquidOutput(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "blueberry_juice"));

        Map<ResourceLocation, Integer> speed = new HashMap<>();
        speed.put(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "blueberry"), 60);

        Map<ResourceLocation, Float> yield = new HashMap<>();
        yield.put(ResourceLocation.withDefaultNamespace("water"), 1.0F);

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "blueberry_juice");
        map.put(id, new BlenderRecipe(id, ingredients, liquids, List.of(), output, speed, yield, 6));
    }

    private void addIceCubes(Map<ResourceLocation, BlenderRecipe> map) {
        var ingredients = List.of(
            new BlenderRecipe.IngredientSpec(ResourceLocation.withDefaultNamespace("ice"), 0.0F, 1.0F),
            new BlenderRecipe.IngredientSpec(ResourceLocation.withDefaultNamespace("packed_ice"), 0.0F, 1.0F),
            new BlenderRecipe.IngredientSpec(ResourceLocation.withDefaultNamespace("blue_ice"), 0.0F, 1.0F)
        );
        var requireAny = List.of(List.of(
            ResourceLocation.withDefaultNamespace("ice"),
            ResourceLocation.withDefaultNamespace("packed_ice"),
            ResourceLocation.withDefaultNamespace("blue_ice")
        ));
        var output = new BlenderRecipe.ItemOutput(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "ice_cube"));

        Map<ResourceLocation, Integer> speed = new HashMap<>();
        speed.put(ResourceLocation.withDefaultNamespace("ice"), 30);
        speed.put(ResourceLocation.withDefaultNamespace("packed_ice"), 90);
        speed.put(ResourceLocation.withDefaultNamespace("blue_ice"), 270);

        Map<ResourceLocation, Float> yield = new HashMap<>();
        yield.put(ResourceLocation.withDefaultNamespace("ice"), 3.0F);
        yield.put(ResourceLocation.withDefaultNamespace("packed_ice"), 6.0F);
        yield.put(ResourceLocation.withDefaultNamespace("blue_ice"), 9.0F);

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "ice_cubes");
        map.put(id, new BlenderRecipe(id, ingredients, List.of(), requireAny, output, speed, yield, 64));
    }
}
