package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.TeapotRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class TeapotRecipeProvider extends ModTeapotRecipeProvider {
    public TeapotRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Map<ResourceLocation, TeapotRecipe> map) {
        addCherryTea(map);
    }

    private void addCherryTea(Map<ResourceLocation, TeapotRecipe> map) {
        var ingredients = List.of(
                new TeapotRecipe.IngredientSpec(ResourceLocation.withDefaultNamespace("cherry_leaves"), 0.5F, 1.0F),
                new TeapotRecipe.IngredientSpec(ResourceLocation.withDefaultNamespace("sugar"), 0.0F, 0.25F)
        );

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "cherry_tea");
        map.put(id, new TeapotRecipe(
                id,
                ResourceLocation.withDefaultNamespace("water"),
                ingredients,
                ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "cherry_tea"),
                200
        ));
    }
}
