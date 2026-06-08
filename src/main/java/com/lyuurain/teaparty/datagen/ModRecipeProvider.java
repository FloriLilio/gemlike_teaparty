package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.MixingCupClearRecipe;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    private static final TagKey<Item> LEMON_LOGS_ITEM = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemon_logs"));

    public ModRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SpecialRecipeBuilder.special(MixingCupClearRecipe::new)
                .save(recipeOutput, "gemlike_teaparty:mixing_cup_clear");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LEMON_PLANKS.get(), 3)
                .requires(LEMON_LOGS_ITEM)
                .group("planks")
                .unlockedBy("has_log", has(LEMON_LOGS_ITEM))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BAGGED_LEMON_BLOCK.get())
                .pattern("LLL").pattern("LLL").pattern("LLL")
                .define('L', ModItems.LEMON.get())
                .unlockedBy("has_lemon", has(ModItems.LEMON.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BAGGED_BLUEBERRY_BLOCK.get())
                .pattern("BBB").pattern("BBB").pattern("BBB")
                .define('B', ModItems.BLUEBERRY.get())
                .unlockedBy("has_blueberry", has(ModItems.BLUEBERRY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BAGGED_RED_GRAPE_BLOCK.get())
                .pattern("RRR").pattern("RRR").pattern("RRR")
                .define('R', ModItems.RED_GRAPE.get())
                .unlockedBy("has_red_grape", has(ModItems.RED_GRAPE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LEMON_STAIRS.get(), 4)
                .pattern("P  ").pattern("PP ").pattern("PPP")
                .define('P', ModBlocks.LEMON_PLANKS.get())
                .group("wooden_stairs")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.LEMON_SLAB.get(), 6)
                .pattern("PPP")
                .define('P', ModBlocks.LEMON_PLANKS.get())
                .group("wooden_slab")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.LEMON_FENCE.get(), 3)
                .pattern("PSP").pattern("PSP")
                .define('P', ModBlocks.LEMON_PLANKS.get())
                .define('S', Items.STICK)
                .group("wooden_fence")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.LEMON_FENCE_GATE.get())
                .pattern("SPS").pattern("SPS")
                .define('P', ModBlocks.LEMON_PLANKS.get())
                .define('S', Items.STICK)
                .group("wooden_fence_gate")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, ModBlocks.LEMON_BUTTON.get())
                .requires(ModBlocks.LEMON_PLANKS.get())
                .group("wooden_button")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.LEMON_PRESSURE_PLATE.get())
                .pattern("PP")
                .define('P', ModBlocks.LEMON_PLANKS.get())
                .group("wooden_pressure_plate")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.LEMON_SIGN.get(), 3)
                .pattern("PPP").pattern("PPP").pattern(" S ")
                .define('P', ModBlocks.LEMON_PLANKS.get())
                .define('S', Items.STICK)
                .group("wooden_sign")
                .unlockedBy("has_planks", has(ModBlocks.LEMON_PLANKS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.LEMON_HANGING_SIGN.get(), 6)
                .pattern("C C").pattern("PPP").pattern("PPP")
                .define('C', Items.CHAIN)
                .define('P', ModBlocks.STRIPPED_LEMON_LOG.get())
                .group("hanging_sign")
                .unlockedBy("has_stripped_log", has(ModBlocks.STRIPPED_LEMON_LOG.get()))
                .save(recipeOutput);
    }
}
