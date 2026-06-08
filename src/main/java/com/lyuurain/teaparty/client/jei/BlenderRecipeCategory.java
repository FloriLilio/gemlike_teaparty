package com.lyuurain.teaparty.client.jei;

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

    private static final int WIDTH = 160;
    private static final int HEIGHT = 110;
    private static final int INPUT_X = 1;
    private static final int TEXT_X = 23;
    private static final int FIRST_ROW_Y = 1;
    private static final int ROW_HEIGHT = 20;
    private static final int OUTPUT_X = 130;
    private static final int OUTPUT_Y = 46;
    private static final int ARROW_X = 112;

    private final IDrawable background;
    private final IDrawable icon;

    public BlenderRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
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
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
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
        int row = 0;
        for (BlenderRecipe.IngredientSpec spec : recipe.ingredients()) {
            var item = BuiltInRegistries.ITEM.get(spec.item());
            if (item != null) {
                builder.addInputSlot(INPUT_X, rowY(row)).addItemStack(new ItemStack(item));
            }
            row++;
        }

        for (BlenderRecipe.LiquidSpec spec : recipe.liquids()) {
            LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(spec.liquid());
            if (def != null) {
                var iconItem = BuiltInRegistries.ITEM.get(def.icon());
                if (iconItem != null) {
                    builder.addInputSlot(INPUT_X, rowY(row)).addItemStack(new ItemStack(iconItem));
                }
            }
            row++;
        }

        if (recipe.output() instanceof BlenderRecipe.ItemOutput itemOut) {
            var item = BuiltInRegistries.ITEM.get(itemOut.item());
            if (item != null) {
                builder.addOutputSlot(OUTPUT_X, OUTPUT_Y).addItemStack(new ItemStack(item));
            }
        } else if (recipe.output() instanceof BlenderRecipe.LiquidOutput liqOut) {
            LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(liqOut.liquid());
            if (def != null) {
                var iconItem = BuiltInRegistries.ITEM.get(def.icon());
                if (iconItem != null) {
                    builder.addOutputSlot(OUTPUT_X, OUTPUT_Y).addItemStack(new ItemStack(iconItem));
                }
            }
        }
    }

    @Override
    public void draw(BlenderRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int row = 0;
        for (BlenderRecipe.IngredientSpec spec : recipe.ingredients()) {
            guiGraphics.drawString(font, formatRatio(spec.min(), spec.max()), TEXT_X, rowY(row) + 5, 0x808080);
            row++;
        }
        for (BlenderRecipe.LiquidSpec spec : recipe.liquids()) {
            guiGraphics.drawString(font, formatRatio(spec.min(), spec.max()), TEXT_X, rowY(row) + 5, 0x808080);
            row++;
        }
        guiGraphics.drawString(font, "=>", ARROW_X, OUTPUT_Y + 5, 0x808080);
    }

    private static int rowY(int row) {
        return FIRST_ROW_Y + row * ROW_HEIGHT;
    }

    private static String formatRatio(float min, float max) {
        return String.format("%.0f-%.0f%%", min * 100, max * 100);
    }
}
