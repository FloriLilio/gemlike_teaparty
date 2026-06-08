package com.lyuurain.teaparty.client.jei;

import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.recipe.MixingCupProcess;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import com.lyuurain.teaparty.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;

public class MixingCupRecipeCategory implements IRecipeCategory<MixingCupRecipe> {
    public static final RecipeType<MixingCupRecipe> TYPE =
            RecipeType.create("gemlike_teaparty", "mixing_cup", MixingCupRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public MixingCupRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 90);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.MIXING_CUP.get()));
    }

    @Override
    public RecipeType<MixingCupRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.gemlike_teaparty.mixing_cup");
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 90;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MixingCupRecipe recipe, IFocusGroup focuses) {
        Set<ResourceLocation> allItems = new LinkedHashSet<>();
        Set<ResourceLocation> allLiquids = new LinkedHashSet<>();
        for (MixingCupRecipe.RecipeStep step : recipe.steps()) {
            allItems.addAll(step.items());
            for (MixingCupProcess.LiquidStack ls : step.liquids()) {
                allLiquids.add(ls.liquid());
            }
        }

        int x = 1;
        for (ResourceLocation itemId : allItems) {
            var item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                builder.addInputSlot(x, 1).addItemStack(new ItemStack(item));
                x += 18;
            }
        }

        x = 1;
        for (ResourceLocation liquidId : allLiquids) {
            LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(liquidId);
            if (def != null) {
                var iconItem = BuiltInRegistries.ITEM.get(def.icon());
                if (iconItem != null) {
                    builder.addInputSlot(x, 23).addItemStack(new ItemStack(iconItem));
                    x += 18;
                }
            }
        }

        var resultItem = BuiltInRegistries.ITEM.get(recipe.result());
        if (resultItem != null) {
            builder.addOutputSlot(130, 37).addItemStack(new ItemStack(resultItem));
        }
    }

    @Override
    public void draw(MixingCupRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int y = 50;
        for (int i = 0; i < recipe.steps().size(); i++) {
            MixingCupRecipe.RecipeStep step = recipe.steps().get(i);
            String stepDesc = (i + 1) + ". ";
            if (step.action() != MixingCupProcess.ProcessAction.NONE) {
                stepDesc += step.action().getName();
            }
            guiGraphics.drawString(font, stepDesc, 1, y, 0x808080);
            y += 10;
        }

        String bottles = recipe.bottles() + "x";
        guiGraphics.drawString(font, bottles, 148, 20, 0xFFFFFF);
    }
}
