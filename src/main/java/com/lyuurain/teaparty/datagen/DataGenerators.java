package com.lyuurain.teaparty.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new TeapartyLiquidProvider(packOutput));
        generator.addProvider(event.includeServer(), new TeapartyRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), new BlenderRecipeProvider(packOutput));
        
        // This provider generates the actual datagen objects for dynamic registries like DamageType
        ModDatapackBuiltinEntriesProvider datapackProvider = new ModDatapackBuiltinEntriesProvider(packOutput, lookupProvider);
        CompletableFuture<HolderLookup.Provider> providerCompletableFuture = datapackProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), datapackProvider);
        
        // Block tags and item tags
        ModBlockTagProvider blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        ModItemTagProvider itemTagProvider = new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper);
        generator.addProvider(event.includeServer(), itemTagProvider);
        generator.addProvider(event.includeServer(), new ModDamageTypeTagProvider(packOutput, providerCompletableFuture, existingFileHelper));
    }
}
