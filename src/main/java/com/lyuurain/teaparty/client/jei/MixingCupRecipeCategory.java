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
import net.minecraft.world.item.ItemStack;

public class MixingCupRecipeCategory implements IRecipeCategory<MixingCupRecipe> {
    public static final RecipeType<MixingCupRecipe> TYPE =
            RecipeType.create("gemlike_teaparty", "mixing_cup", MixingCupRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 110;
    private static final int STEP_LABEL_X = 1;
    private static final int SLOT_START_X = 18;
    private static final int FIRST_ROW_Y = 1;
    private static final int ROW_HEIGHT = 30;
    private static final int SLOT_GAP = 18;
    private static final int ACTION_X = 82;
    private static final int ARROW_X = 112;
    private static final int OUTPUT_X = 130;

    private final IDrawable background;
    private final IDrawable icon;

    public MixingCupRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
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
    public void setRecipe(IRecipeLayoutBuilder builder, MixingCupRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.steps().size(); i++) {
            MixingCupRecipe.RecipeStep step = recipe.steps().get(i);
            int rowY = rowY(i);
            int slotX = SLOT_START_X;

            for (var itemId : step.items()) {
                var item = BuiltInRegistries.ITEM.get(itemId);
                if (item != null) {
                    builder.addInputSlot(slotX, rowY).addItemStack(new ItemStack(item));
                    slotX += SLOT_GAP;
                }
            }

            for (MixingCupProcess.LiquidStack liquid : step.liquids()) {
                LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(liquid.liquid());
                if (def != null && addLiquidInputSlot(builder, slotX, rowY, def)) {
                    slotX += SLOT_GAP;
                }
            }
        }

        var resultItem = BuiltInRegistries.ITEM.get(recipe.result());
        if (resultItem != null) {
            builder.addOutputSlot(OUTPUT_X, outputY(recipe)).addItemStack(new ItemStack(resultItem));
        }
    }

    @Override
    public void draw(MixingCupRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        for (int i = 0; i < recipe.steps().size(); i++) {
            MixingCupRecipe.RecipeStep step = recipe.steps().get(i);
            int rowY = rowY(i);
            guiGraphics.drawString(font, (i + 1) + ".", STEP_LABEL_X, rowY + 5, 0x808080);

            drawLiquidBottleCounts(step, guiGraphics, rowY);

            if (step.action() != MixingCupProcess.ProcessAction.NONE) {
                String actionKey = "tooltip.gemlike_teaparty.mixing_cup.action." + step.action().getName();
                guiGraphics.drawString(font, Component.translatable(actionKey).getString(), ACTION_X, rowY + 5, 0x808080);
            }

            if (i < recipe.steps().size() - 1) {
                guiGraphics.drawString(font, "v", ARROW_X, rowY + 18, 0x808080);
            } else {
                guiGraphics.drawString(font, "=>", ARROW_X, rowY + 5, 0x808080);
            }
        }

        guiGraphics.drawString(font, recipe.bottles() + "x", OUTPUT_X + 11, outputY(recipe) + 10, 0xFFFFFF);
    }

    private static void drawLiquidBottleCounts(MixingCupRecipe.RecipeStep step, GuiGraphics guiGraphics, int rowY) {
        var font = Minecraft.getInstance().font;
        int slotX = SLOT_START_X;
        for (var itemId : step.items()) {
            var item = BuiltInRegistries.ITEM.get(itemId);
            if (item != null) {
                slotX += SLOT_GAP;
            }
        }
        for (MixingCupProcess.LiquidStack liquid : step.liquids()) {
            LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(liquid.liquid());
            if (def != null && hasLiquidIcon(def)) {
                guiGraphics.drawString(font, liquid.bottles() + "x", slotX + 8, rowY + 10, 0xFFFFFF);
                slotX += SLOT_GAP;
            }
        }
    }

    private static boolean addLiquidInputSlot(IRecipeLayoutBuilder builder, int x, int y, LiquidDefinition liquid) {
        return BuiltInRegistries.ITEM.getOptional(liquid.icon()).map(iconItem -> {
            builder.addInputSlot(x, y)
                    .addItemStack(createLiquidIconStack(liquid, iconItem))
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.clear();
                        tooltip.add(Component.translatable(liquid.name()));
                    });
            return true;
        }).orElse(false);
    }

    private static boolean hasLiquidIcon(LiquidDefinition liquid) {
        return BuiltInRegistries.ITEM.getOptional(liquid.icon()).isPresent();
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

    private static int outputY(MixingCupRecipe recipe) {
        return rowY(Math.max(0, recipe.steps().size() - 1));
    }
}
