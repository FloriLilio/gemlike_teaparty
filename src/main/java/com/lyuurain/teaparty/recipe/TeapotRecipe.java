package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record TeapotRecipe(
        ResourceLocation id,
        ResourceLocation liquid,
        List<IngredientSpec> ingredients,
        ResourceLocation output,
        int brewTime
) {
    public static final Codec<TeapotRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("liquid").forGetter(TeapotRecipe::liquid),
            IngredientSpec.CODEC.listOf().fieldOf("ingredients").forGetter(TeapotRecipe::ingredients),
            ResourceLocation.CODEC.fieldOf("output").forGetter(TeapotRecipe::output),
            Codec.INT.optionalFieldOf("brew_time", 200).forGetter(TeapotRecipe::brewTime)
    ).apply(instance, (liquid, ingredients, output, brewTime) -> new TeapotRecipe(null, liquid, ingredients, output, brewTime)));

    public TeapotRecipe withId(ResourceLocation id) {
        return new TeapotRecipe(id, this.liquid, this.ingredients, this.output, this.brewTime);
    }

    public record IngredientSpec(ResourceLocation item, float min, float max) {
        public static final Codec<IngredientSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(IngredientSpec::item),
                Codec.FLOAT.fieldOf("min").forGetter(IngredientSpec::min),
                Codec.FLOAT.fieldOf("max").forGetter(IngredientSpec::max)
        ).apply(instance, IngredientSpec::new));
    }

    public MatchResult match(NonNullList<ItemStack> inputItems, ResourceLocation liquidId, int liquidCount) {
        if (liquidId == null || liquidCount <= 0 || !this.liquid.equals(liquidId)) {
            return null;
        }

        Set<ResourceLocation> knownItemIds = new HashSet<>();
        for (IngredientSpec spec : this.ingredients) {
            knownItemIds.add(spec.item());
        }

        for (ItemStack stack : inputItems) {
            if (!stack.isEmpty()) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (!knownItemIds.contains(itemId)) {
                    return null;
                }
            }
        }

        for (IngredientSpec spec : this.ingredients) {
            int matchedCount = 0;
            for (ItemStack stack : inputItems) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    if (spec.item().equals(itemId)) {
                        matchedCount += stack.getCount();
                    }
                }
            }
            float ratio = matchedCount / (float) liquidCount;
            if (ratio < spec.min() - 0.001F || ratio > spec.max() + 0.001F) {
                return null;
            }
        }

        return new MatchResult(Math.max(this.brewTime, 1), liquidCount);
    }
}
