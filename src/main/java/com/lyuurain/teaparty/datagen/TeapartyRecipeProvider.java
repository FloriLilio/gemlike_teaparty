package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.MixingCupProcess.LiquidStack;
import com.lyuurain.teaparty.recipe.MixingCupProcess.ProcessAction;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import com.lyuurain.teaparty.recipe.MixingCupRecipe.RecipeStep;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class TeapartyRecipeProvider extends ModTeapartyRecipeProvider {
    public TeapartyRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Map<ResourceLocation, MixingCupRecipe> map) {
        addRecipe(map, "end_vision", "gemlike_teaparty:end_vision", 3, List.of(
            new RecipeStep(
                List.of(ResourceLocation.withDefaultNamespace("ender_eye")),
                List.of(
                    new LiquidStack(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemonade"), 1),
                    new LiquidStack(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "cherry_tea"), 1)
                ),
                ProcessAction.STIR
            ),
            new RecipeStep(
                List.of(),
                List.of(new LiquidStack(ResourceLocation.withDefaultNamespace("honey"), 1)),
                ProcessAction.NONE
            )
        ));

        addRecipe(map, "glacier", "gemlike_teaparty:glacier", 3, List.of(
            new RecipeStep(
                List.of(
                    ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "ice_cube"),
                    ResourceLocation.withDefaultNamespace("packed_ice")
                ),
                List.of(new LiquidStack(ResourceLocation.withDefaultNamespace("water"), 3)),
                ProcessAction.STIR
            ),
            new RecipeStep(
                List.of(ResourceLocation.withDefaultNamespace("blue_ice")),
                List.of(),
                ProcessAction.SHAKE
            )
        ));
    }

    private void addRecipe(Map<ResourceLocation, MixingCupRecipe> map, String id, String resultId, int bottles, List<RecipeStep> steps) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, id);
        map.put(rl, new MixingCupRecipe(rl, ResourceLocation.parse(resultId), bottles, steps));
    }
}
