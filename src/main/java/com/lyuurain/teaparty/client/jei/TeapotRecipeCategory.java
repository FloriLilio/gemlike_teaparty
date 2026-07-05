package com.lyuurain.teaparty.client.jei;

import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.recipe.TeapotRecipe;
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

public class TeapotRecipeCategory implements IRecipeCategory<TeapotRecipe> {
    public static final RecipeType<TeapotRecipe> TYPE =
            RecipeType.create("gemlike_teaparty", "teapot", TeapotRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 100;
    private static final int INPUT_X = 1;
    private static final int TEXT_X = 23;
    private static final int FIRST_ROW_Y = 1;
    private static final int ROW_HEIGHT = 20;
    private static final int OUTPUT_X = 130;
    private static final int OUTPUT_Y = 40;
    private static final int ARROW_X = 112;

    private final IDrawable background;
    private final IDrawable icon;

    public TeapotRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.TEAPOT.get()));
    }

    @Override
    public RecipeType<TeapotRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.gemlike_teaparty.teapot");
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
    public void setRecipe(IRecipeLayoutBuilder builder, TeapotRecipe recipe, IFocusGroup focuses) {
        LiquidDefinition inputLiquid = LiquidManager.INSTANCE.getLiquids().get(recipe.liquid());
        if (inputLiquid != null) {
            addLiquidInputSlot(builder, INPUT_X, rowY(0), inputLiquid);
        }

        int row = 1;
        for (TeapotRecipe.IngredientSpec spec : recipe.ingredients()) {
            var item = BuiltInRegistries.ITEM.get(spec.item());
            if (item != null) {
                builder.addInputSlot(INPUT_X, rowY(row)).addItemStack(new ItemStack(item));
            }
            row++;
        }

        LiquidDefinition outputLiquid = LiquidManager.INSTANCE.getLiquids().get(recipe.output());
        if (outputLiquid != null) {
            addLiquidOutputSlot(builder, OUTPUT_X, OUTPUT_Y, outputLiquid);
        }
    }

    @Override
    public void draw(TeapotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, Component.translatable("jei.gemlike_teaparty.teapot.liquid"), TEXT_X, rowY(0) + 5, 0x808080);
        int row = 1;
        for (TeapotRecipe.IngredientSpec spec : recipe.ingredients()) {
            guiGraphics.drawString(font, formatRatio(spec.min(), spec.max()), TEXT_X, rowY(row) + 5, 0x808080);
            row++;
        }
        guiGraphics.drawString(font, "=>", ARROW_X, OUTPUT_Y + 5, 0x808080);
    }

    private static void addLiquidInputSlot(IRecipeLayoutBuilder builder, int x, int y, LiquidDefinition liquid) {
        BuiltInRegistries.ITEM.getOptional(liquid.icon()).ifPresent(iconItem ->
                builder.addInputSlot(x, y)
                        .addItemStack(createLiquidIconStack(liquid, iconItem))
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            tooltip.clear();
                            tooltip.add(Component.translatable(liquid.name()));
                        })
        );
    }

    private static void addLiquidOutputSlot(IRecipeLayoutBuilder builder, int x, int y, LiquidDefinition liquid) {
        BuiltInRegistries.ITEM.getOptional(liquid.icon()).ifPresent(iconItem ->
                builder.addOutputSlot(x, y)
                        .addItemStack(createLiquidIconStack(liquid, iconItem))
                        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                            tooltip.clear();
                            tooltip.add(Component.translatable(liquid.name()));
                        })
        );
    }

    private static ItemStack createLiquidIconStack(LiquidDefinition liquid, net.minecraft.world.item.Item iconItem) {
        ItemStack stack = new ItemStack(iconItem);
        liquid.items().stream()
                .filter(item -> item.item().equals(liquid.icon()))
                .findFirst()
                .ifPresent(item -> stack.applyComponents(item.components().asPatch()));
        return stack;
    }

    private static int rowY(int row) {
        return FIRST_ROW_Y + row * ROW_HEIGHT;
    }

    private static String formatRatio(float min, float max) {
        return String.format("%.0f-%.0f%%", min * 100, max * 100);
    }
}
