package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record MixingCupRecipe(ResourceLocation id, ResourceLocation result, int bottles, List<RecipeStep> steps) {

    public static final Codec<MixingCupRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("result").forGetter(MixingCupRecipe::result),
            Codec.INT.fieldOf("bottles").forGetter(MixingCupRecipe::bottles),
            RecipeStep.CODEC.listOf().fieldOf("steps").forGetter(MixingCupRecipe::steps)
    ).apply(instance, (result, bottles, steps) -> new MixingCupRecipe(null, result, bottles, steps)));

    public MixingCupRecipe withId(ResourceLocation id) {
        return new MixingCupRecipe(id, this.result, this.bottles, this.steps);
    }

    public boolean matches(List<MixingCupProcess> cupProcesses) {
        List<RecipeStep> rSteps = new ArrayList<>(this.steps);
        while (!rSteps.isEmpty() && isRecipeStepEmpty(rSteps.get(rSteps.size() - 1))) {
            rSteps.remove(rSteps.size() - 1);
        }

        List<MixingCupProcess> cSteps = new ArrayList<>(cupProcesses);
        while (!cSteps.isEmpty() && cSteps.get(cSteps.size() - 1).isEmpty()) {
            cSteps.remove(cSteps.size() - 1);
        }

        if (rSteps.size() != cSteps.size()) {
            return false;
        }

        for (int i = 0; i < rSteps.size(); i++) {
            if (!stepMatches(rSteps.get(i), cSteps.get(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean isRecipeStepEmpty(RecipeStep step) {
        return step.items().isEmpty() && step.liquids().isEmpty() && step.action() == MixingCupProcess.ProcessAction.NONE;
    }

    private boolean stepMatches(RecipeStep recipeStep, MixingCupProcess cupProcess) {
        if (recipeStep.action() != cupProcess.action()) {
            return false;
        }

        List<ResourceLocation> recipeItems = new ArrayList<>(recipeStep.items());
        List<ItemStack> cupItems = new ArrayList<>(cupProcess.items());

        if (recipeItems.size() != cupItems.size()) {
            return false;
        }

        for (ResourceLocation reqItem : recipeItems) {
            boolean found = false;
            for (int i = 0; i < cupItems.size(); i++) {
                ItemStack stack = cupItems.get(i);
                ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (reqItem.equals(itemId)) {
                    cupItems.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        List<MixingCupProcess.LiquidStack> recipeLiquids = new ArrayList<>(recipeStep.liquids());
        List<MixingCupProcess.LiquidStack> cupLiquids = new ArrayList<>(cupProcess.liquids());

        cupLiquids.removeIf(l -> l.bottles() == 0);
        recipeLiquids.removeIf(l -> l.bottles() == 0);

        if (recipeLiquids.size() != cupLiquids.size()) {
            return false;
        }

        for (MixingCupProcess.LiquidStack reqLiq : recipeLiquids) {
            boolean found = false;
            for (int i = 0; i < cupLiquids.size(); i++) {
                MixingCupProcess.LiquidStack cupLiq = cupLiquids.get(i);
                if (reqLiq.liquid().equals(cupLiq.liquid()) && reqLiq.bottles() == cupLiq.bottles()) {
                    cupLiquids.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    public record RecipeStep(List<ResourceLocation> items, List<MixingCupProcess.LiquidStack> liquids, MixingCupProcess.ProcessAction action) {
        public static final Codec<RecipeStep> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().fieldOf("items").forGetter(RecipeStep::items),
                MixingCupProcess.LiquidStack.CODEC.listOf().fieldOf("liquids").forGetter(RecipeStep::liquids),
                MixingCupProcess.ProcessAction.CODEC.fieldOf("action").forGetter(RecipeStep::action)
        ).apply(instance, RecipeStep::new));
    }
}
