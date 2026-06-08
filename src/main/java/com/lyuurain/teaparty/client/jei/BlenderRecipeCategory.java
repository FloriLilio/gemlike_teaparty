package com.lyuurain.teaparty.client.jei;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
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
import net.minecraft.world.item.ItemStack;

public class BlenderRecipeCategory implements IRecipeCategory<BlenderRecipe> {
    public static final RecipeType<BlenderRecipe> TYPE =
            RecipeType.create("gemlike_teaparty", "blender", BlenderRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BlenderRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 72);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.BLENDER_LIGHT.get()));
    }

    @Override
    public RecipeType<BlenderRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.gemlike_teaparty.blender");
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public int getHeight() {
        return 72;
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
    public void setRecipe(IRecipeLayoutBuilder builder, BlenderRecipe recipe, IFocusGroup focuses) {
        int x = 1;
        for (BlenderRecipe.IngredientSpec spec : recipe.ingredients()) {
            var item = BuiltInRegistries.ITEM.get(spec.item());
            if (item != null) {
                builder.addInputSlot(x, 1).addItemStack(new ItemStack(item));
            }
            x += 18;
        }

        x = 1;
        for (BlenderRecipe.LiquidSpec spec : recipe.liquids()) {
            LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(spec.liquid());
            if (def != null) {
                var iconItem = BuiltInRegistries.ITEM.get(def.icon());
                if (iconItem != null) {
                    builder.addInputSlot(x, 25).addItemStack(new ItemStack(iconItem));
                }
            }
            x += 18;
        }

        if (recipe.output() instanceof BlenderRecipe.ItemOutput itemOut) {
            var item = BuiltInRegistries.ITEM.get(itemOut.item());
            if (item != null) {
                builder.addOutputSlot(130, 19).addItemStack(new ItemStack(item));
            }
        } else if (recipe.output() instanceof BlenderRecipe.LiquidOutput liqOut) {
            LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(liqOut.liquid());
            if (def != null) {
                var iconItem = BuiltInRegistries.ITEM.get(def.icon());
                if (iconItem != null) {
                    builder.addOutputSlot(130, 19).addItemStack(new ItemStack(iconItem));
                }
            }
        }
    }

    @Override
    public void draw(BlenderRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int x = 1;
        for (BlenderRecipe.IngredientSpec spec : recipe.ingredients()) {
            String ratio = String.format("%.0f-%.0f%%", spec.min() * 100, spec.max() * 100);
            guiGraphics.drawString(font, ratio, x, 20, 0x808080);
            x += 18;
        }
    }
}
