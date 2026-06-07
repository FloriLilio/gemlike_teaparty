package com.lyuurain.teaparty.datagen;

import com.google.gson.JsonElement;
import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ModLiquidProvider implements DataProvider {
    private final PackOutput packOutput;

    public ModLiquidProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    protected abstract void buildLiquids(Map<ResourceLocation, LiquidDefinition> consumer);

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        Map<ResourceLocation, LiquidDefinition> map = new HashMap<>();
        buildLiquids(map);
        
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation id = entry.getKey();
            LiquidDefinition def = entry.getValue();
            
            Path path = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(id.getNamespace()).resolve("teaparty_liquids").resolve(id.getPath() + ".json");
            
            JsonElement json = LiquidDefinition.CODEC.encodeStart(JsonOps.INSTANCE, def).getOrThrow();
            return DataProvider.saveStable(cachedOutput, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Gem-like Tea Party Liquids";
    }
}
