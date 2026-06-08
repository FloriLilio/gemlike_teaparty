package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record BlenderRecipe(
    ResourceLocation id,
    List<IngredientSpec> ingredients,
    List<LiquidSpec> liquids,
    List<List<ResourceLocation>> requireAny,
    Output output,
    Map<ResourceLocation, Integer> speedPerUnit,
    Map<ResourceLocation, Float> yieldPerUnit,
    int maxPerBatch
) {
    public static final Codec<BlenderRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IngredientSpec.CODEC.listOf().fieldOf("ingredients").forGetter(BlenderRecipe::ingredients),
        LiquidSpec.CODEC.listOf().fieldOf("liquids").forGetter(BlenderRecipe::liquids),
        ResourceLocation.CODEC.listOf().listOf().optionalFieldOf("require_any", List.of()).forGetter(BlenderRecipe::requireAny),
        Output.CODEC.fieldOf("output").forGetter(BlenderRecipe::output),
        Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("speed_per_unit").forGetter(BlenderRecipe::speedPerUnit),
        Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).fieldOf("yield_per_unit").forGetter(BlenderRecipe::yieldPerUnit),
        Codec.INT.optionalFieldOf("max_per_batch", 64).forGetter(BlenderRecipe::maxPerBatch)
    ).apply(instance, (ingredients, liquids, requireAny, output, speedPerUnit, yieldPerUnit, maxPerBatch) ->
        new BlenderRecipe(null, ingredients, liquids, requireAny, output, speedPerUnit, yieldPerUnit, maxPerBatch)
    ));

    public BlenderRecipe withId(ResourceLocation id) {
        return new BlenderRecipe(id, this.ingredients, this.liquids, this.requireAny, this.output, this.speedPerUnit, this.yieldPerUnit, this.maxPerBatch);
    }

    public record IngredientSpec(ResourceLocation item, float min, float max) {
        public static final Codec<IngredientSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(IngredientSpec::item),
            Codec.FLOAT.fieldOf("min").forGetter(IngredientSpec::min),
            Codec.FLOAT.fieldOf("max").forGetter(IngredientSpec::max)
        ).apply(instance, IngredientSpec::new));
    }

    public record LiquidSpec(ResourceLocation liquid, float min, float max) {
        public static final Codec<LiquidSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("liquid").forGetter(LiquidSpec::liquid),
            Codec.FLOAT.fieldOf("min").forGetter(LiquidSpec::min),
            Codec.FLOAT.fieldOf("max").forGetter(LiquidSpec::max)
        ).apply(instance, LiquidSpec::new));
    }

    public sealed interface Output {
        Codec<Output> CODEC = Codec.STRING.dispatch(
            "type",
            output -> {
                if (output instanceof ItemOutput) return "item";
                if (output instanceof LiquidOutput) return "liquid";
                throw new IllegalArgumentException("Unknown output type: " + output.getClass());
            },
            type -> switch (type) {
                case "item" -> ItemOutput.MAP_CODEC;
                case "liquid" -> LiquidOutput.MAP_CODEC;
                default -> throw new IllegalArgumentException("Unknown output type: " + type);
            }
        );
    }

    public record ItemOutput(ResourceLocation item) implements Output {
        public static final com.mojang.serialization.MapCodec<ItemOutput> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(ItemOutput::item)
        ).apply(instance, ItemOutput::new));
    }

    public record LiquidOutput(ResourceLocation liquid) implements Output {
        public static final com.mojang.serialization.MapCodec<LiquidOutput> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("liquid").forGetter(LiquidOutput::liquid)
        ).apply(instance, LiquidOutput::new));
    }

    public MatchResult match(NonNullList<ItemStack> inputItems, ResourceLocation blenderLiquidId, int blenderLiquidCount) {
        int totalUnits = blenderLiquidCount;
        for (ItemStack stack : inputItems) {
            if (!stack.isEmpty()) {
                totalUnits += stack.getCount();
            }
        }

        if (totalUnits <= 0) {
            return null;
        }

        Set<ResourceLocation> knownItemIds = new HashSet<>();
        for (IngredientSpec spec : this.ingredients) {
            knownItemIds.add(spec.item);
        }

        Set<ResourceLocation> knownLiquidIds = new HashSet<>();
        for (LiquidSpec spec : this.liquids) {
            knownLiquidIds.add(spec.liquid);
        }

        // Check for extraneous inputs
        for (ItemStack stack : inputItems) {
            if (!stack.isEmpty()) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (!knownItemIds.contains(itemId)) {
                    return null;
                }
            }
        }
        if (blenderLiquidId != null && blenderLiquidCount > 0 && !knownLiquidIds.contains(blenderLiquidId)) {
            return null;
        }

        // Check ingredient ratios
        for (IngredientSpec spec : this.ingredients) {
            int matchedCount = 0;
            for (ItemStack stack : inputItems) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    if (spec.item.equals(itemId)) {
                        matchedCount += stack.getCount();
                    }
                }
            }
            float ratio = matchedCount / (float) totalUnits;
            if (ratio < spec.min - 0.001F || ratio > spec.max + 0.001F) {
                return null;
            }
        }

        // Check liquid ratios
        for (LiquidSpec spec : this.liquids) {
            int matchedCount = 0;
            if (blenderLiquidId != null && spec.liquid.equals(blenderLiquidId)) {
                matchedCount = blenderLiquidCount;
            }
            float ratio = matchedCount / (float) totalUnits;
            if (ratio < spec.min - 0.001F || ratio > spec.max + 0.001F) {
                return null;
            }
        }

        // Check requireAny
        for (List<ResourceLocation> anySet : this.requireAny) {
            boolean found = false;
            for (ResourceLocation id : anySet) {
                if (blenderLiquidId != null && id.equals(blenderLiquidId) && blenderLiquidCount > 0) {
                    found = true;
                    break;
                }
                for (ItemStack stack : inputItems) {
                    if (!stack.isEmpty()) {
                        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                        if (id.equals(itemId)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) break;
            }
            if (!found) {
                return null;
            }
        }

        // Compute speed
        int totalTicks = 0;
        for (Map.Entry<ResourceLocation, Integer> entry : this.speedPerUnit.entrySet()) {
            int count = 0;
            if (blenderLiquidId != null && entry.getKey().equals(blenderLiquidId)) {
                count += blenderLiquidCount;
            }
            for (ItemStack stack : inputItems) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    if (entry.getKey().equals(itemId)) {
                        count += stack.getCount();
                    }
                }
            }
            totalTicks += count * entry.getValue();
        }

        // Compute yield
        float totalYield = 0.0F;
        for (Map.Entry<ResourceLocation, Float> entry : this.yieldPerUnit.entrySet()) {
            int count = 0;
            if (blenderLiquidId != null && entry.getKey().equals(blenderLiquidId)) {
                count += blenderLiquidCount;
            }
            for (ItemStack stack : inputItems) {
                if (!stack.isEmpty()) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    if (entry.getKey().equals(itemId)) {
                        count += stack.getCount();
                    }
                }
            }
            totalYield += count * entry.getValue();
        }

        return new MatchResult(totalTicks, totalYield);
    }
}
