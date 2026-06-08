package com.lyuurain.teaparty.client.jei;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import com.lyuurain.teaparty.recipe.BlenderRecipeManager;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import com.lyuurain.teaparty.recipe.RecipeManager;
import com.lyuurain.teaparty.recipe.TeapotRecipe;
import com.lyuurain.teaparty.recipe.TeapotRecipeManager;
import com.lyuurain.teaparty.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class TeaPartyJeiPlugin implements IModPlugin {

    private static IJeiRuntime jeiRuntime;

    private static final List<BlenderRecipe> pendingBlenderRecipes = new ArrayList<>();
    private static final List<MixingCupRecipe> pendingMixingCupRecipes = new ArrayList<>();
    private static final List<TeapotRecipe> pendingTeapotRecipes = new ArrayList<>();

    public static void onBlenderRecipesSynced(List<BlenderRecipe> recipes) {
        GemlikeTeaParty.LOGGER.info("[JEI] Synced {} blender recipes, runtime={}", recipes.size(), jeiRuntime != null);
        if (jeiRuntime != null) {
            IRecipeManager rm = jeiRuntime.getRecipeManager();
            if (!pendingBlenderRecipes.isEmpty()) {
                rm.hideRecipes(BlenderRecipeCategory.TYPE, new ArrayList<>(pendingBlenderRecipes));
            }
            pendingBlenderRecipes.clear();
            pendingBlenderRecipes.addAll(recipes);
            rm.addRecipes(BlenderRecipeCategory.TYPE, recipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Added {} blender recipes to JEI", recipes.size());
        } else {
            pendingBlenderRecipes.clear();
            pendingBlenderRecipes.addAll(recipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Stored {} blender recipes, waiting for runtime", recipes.size());
        }
    }

    public static void onMixingCupRecipesSynced(List<MixingCupRecipe> recipes) {
        GemlikeTeaParty.LOGGER.info("[JEI] Synced {} mixing cup recipes, runtime={}", recipes.size(), jeiRuntime != null);
        if (jeiRuntime != null) {
            IRecipeManager rm = jeiRuntime.getRecipeManager();
            if (!pendingMixingCupRecipes.isEmpty()) {
                rm.hideRecipes(MixingCupRecipeCategory.TYPE, new ArrayList<>(pendingMixingCupRecipes));
            }
            pendingMixingCupRecipes.clear();
            pendingMixingCupRecipes.addAll(recipes);
            rm.addRecipes(MixingCupRecipeCategory.TYPE, recipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Added {} mixing cup recipes to JEI", recipes.size());
        } else {
            pendingMixingCupRecipes.clear();
            pendingMixingCupRecipes.addAll(recipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Stored {} mixing cup recipes, waiting for runtime", recipes.size());
        }
    }

    public static void onTeapotRecipesSynced(List<TeapotRecipe> recipes) {
        GemlikeTeaParty.LOGGER.info("[JEI] Synced {} teapot recipes, runtime={}", recipes.size(), jeiRuntime != null);
        if (jeiRuntime != null) {
            IRecipeManager rm = jeiRuntime.getRecipeManager();
            if (!pendingTeapotRecipes.isEmpty()) {
                rm.hideRecipes(TeapotRecipeCategory.TYPE, new ArrayList<>(pendingTeapotRecipes));
            }
            pendingTeapotRecipes.clear();
            pendingTeapotRecipes.addAll(recipes);
            rm.addRecipes(TeapotRecipeCategory.TYPE, recipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Added {} teapot recipes to JEI", recipes.size());
        } else {
            pendingTeapotRecipes.clear();
            pendingTeapotRecipes.addAll(recipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Stored {} teapot recipes, waiting for runtime", recipes.size());
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        GemlikeTeaParty.LOGGER.info("[JEI] registerCategories called");
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new BlenderRecipeCategory(guiHelper),
                new MixingCupRecipeCategory(guiHelper),
                new TeapotRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        GemlikeTeaParty.LOGGER.info("[JEI] registerRecipes called");

        List<BlenderRecipe> blenderRecipes = BlenderRecipeManager.INSTANCE.getRecipes();
        GemlikeTeaParty.LOGGER.info("[JEI] Blender recipes at register time: {}", blenderRecipes.size());
        if (!blenderRecipes.isEmpty()) {
            registration.addRecipes(BlenderRecipeCategory.TYPE, blenderRecipes);
            pendingBlenderRecipes.addAll(blenderRecipes);
        }

        List<MixingCupRecipe> recipes = RecipeManager.INSTANCE.getRecipes();
        GemlikeTeaParty.LOGGER.info("[JEI] Mixing cup recipes at register time: {}", recipes.size());
        if (!recipes.isEmpty()) {
            registration.addRecipes(MixingCupRecipeCategory.TYPE, recipes);
            pendingMixingCupRecipes.addAll(recipes);
        }

        List<TeapotRecipe> teapotRecipes = TeapotRecipeManager.INSTANCE.getRecipes();
        GemlikeTeaParty.LOGGER.info("[JEI] Teapot recipes at register time: {}", teapotRecipes.size());
        if (!teapotRecipes.isEmpty()) {
            registration.addRecipes(TeapotRecipeCategory.TYPE, teapotRecipes);
            pendingTeapotRecipes.addAll(teapotRecipes);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        GemlikeTeaParty.LOGGER.info("[JEI] registerRecipeCatalysts called");
        ItemStack blenderLight = new ItemStack(ModItems.BLENDER_LIGHT.get());
        ItemStack blenderDark = new ItemStack(ModItems.BLENDER_DARK.get());
        ItemStack mixingCup = new ItemStack(ModItems.MIXING_CUP.get());
        ItemStack teapot = new ItemStack(ModItems.TEAPOT.get());

        registration.addRecipeCatalyst(blenderLight, BlenderRecipeCategory.TYPE);
        registration.addRecipeCatalyst(blenderDark, BlenderRecipeCategory.TYPE);
        registration.addRecipeCatalyst(mixingCup, MixingCupRecipeCategory.TYPE);
        registration.addRecipeCatalyst(teapot, TeapotRecipeCategory.TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        GemlikeTeaParty.LOGGER.info("[JEI] onRuntimeAvailable called, pending blenders: {}, pending mixing cups: {}, pending teapots: {}",
                pendingBlenderRecipes.size(), pendingMixingCupRecipes.size(), pendingTeapotRecipes.size());
        jeiRuntime = runtime;

        if (!pendingBlenderRecipes.isEmpty()) {
            runtime.getRecipeManager().addRecipes(BlenderRecipeCategory.TYPE, pendingBlenderRecipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Added {} pending blender recipes to JEI", pendingBlenderRecipes.size());
        }
        if (!pendingMixingCupRecipes.isEmpty()) {
            runtime.getRecipeManager().addRecipes(MixingCupRecipeCategory.TYPE, pendingMixingCupRecipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Added {} pending mixing cup recipes to JEI", pendingMixingCupRecipes.size());
        }
        if (!pendingTeapotRecipes.isEmpty()) {
            runtime.getRecipeManager().addRecipes(TeapotRecipeCategory.TYPE, pendingTeapotRecipes);
            GemlikeTeaParty.LOGGER.info("[JEI] Added {} pending teapot recipes to JEI", pendingTeapotRecipes.size());
        }
    }

    @Override
    public void onRuntimeUnavailable() {
        GemlikeTeaParty.LOGGER.info("[JEI] onRuntimeUnavailable called");
        jeiRuntime = null;
        pendingBlenderRecipes.clear();
        pendingMixingCupRecipes.clear();
        pendingTeapotRecipes.clear();
    }
}
