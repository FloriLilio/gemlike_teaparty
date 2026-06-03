package com.lyuurain.teaparty.recipe;

import com.lyuurain.teaparty.registry.ModDataComponents;
import com.lyuurain.teaparty.registry.ModItems;
import com.lyuurain.teaparty.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MixingCupClearRecipe extends CustomRecipe {
    public MixingCupClearRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int count = 0;
        ItemStack targetStack = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.MIXING_CUP.get())) {
                    count++;
                    targetStack = stack;
                } else {
                    return false;
                }
            }
        }
        if (count == 1) {
            boolean hasProcesses = targetStack.has(ModDataComponents.PROCESSES.get());
            boolean hasOutput = targetStack.has(ModDataComponents.OUTPUT.get());
            return hasProcesses || hasOutput;
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack targetStack = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(ModItems.MIXING_CUP.get())) {
                    targetStack = stack;
                    break;
                }
            }
        }
        if (!targetStack.isEmpty()) {
            ItemStack result = new ItemStack(ModItems.MIXING_CUP.get());
            if (targetStack.has(ModDataComponents.OPENED.get())) {
                result.set(ModDataComponents.OPENED.get(), targetStack.get(ModDataComponents.OPENED.get()));
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MIXING_CUP_CLEAR_SERIALIZER.get();
    }
}
