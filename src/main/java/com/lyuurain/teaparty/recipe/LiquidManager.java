package com.lyuurain.teaparty.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lyuurain.teaparty.GemlikeTeaParty;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LiquidManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final LiquidManager INSTANCE = new LiquidManager();

    private Map<ResourceLocation, LiquidDefinition> liquids = new HashMap<>();

    private LiquidManager() {
        super(GSON, "teaparty_liquids");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, LiquidDefinition> newMap = new HashMap<>();
        object.forEach((location, jsonElement) -> {
            try {
                LiquidDefinition.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .resultOrPartial(err -> GemlikeTeaParty.LOGGER.error("Failed to parse liquid json {}: {}", location, err))
                        .ifPresent(def -> newMap.put(location, def.withId(location)));
            } catch (Exception e) {
                GemlikeTeaParty.LOGGER.error("Failed to load liquid json {}: {}", location, e.getMessage());
            }
        });
        this.liquids = newMap;
        GemlikeTeaParty.LOGGER.info("Loaded {} liquids.", this.liquids.size());
    }

    public Map<ResourceLocation, LiquidDefinition> getLiquids() {
        return this.liquids;
    }

    public void setLiquids(Map<ResourceLocation, LiquidDefinition> liquids) {
        this.liquids = liquids;
    }

    public static LiquidDefinition.ItemConversion getConversion(ItemStack stack) {
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (LiquidDefinition def : INSTANCE.liquids.values()) {
            for (LiquidDefinition.ItemConversion conv : def.items()) {
                if (conv.item().equals(itemId)) {
                    if (stack.is(net.minecraft.world.item.Items.POTION)) {
                        net.minecraft.world.item.alchemy.PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                        if (contents != null && contents.is(net.minecraft.world.item.alchemy.Potions.WATER)) {
                            return conv;
                        }
                    } else {
                        return conv;
                    }
                }
            }
        }
        return null;
    }

    public static LiquidDefinition getLiquidFor(ItemStack stack) {
        ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (LiquidDefinition def : INSTANCE.liquids.values()) {
            for (LiquidDefinition.ItemConversion conv : def.items()) {
                if (conv.item().equals(itemId)) {
                    if (stack.is(net.minecraft.world.item.Items.POTION)) {
                        net.minecraft.world.item.alchemy.PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                        if (contents != null && contents.is(net.minecraft.world.item.alchemy.Potions.WATER)) {
                            return def;
                        }
                    } else {
                        return def;
                    }
                }
            }
        }
        return null;
    }
}
