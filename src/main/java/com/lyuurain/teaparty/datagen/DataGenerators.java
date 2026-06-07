package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new TeapartyLiquidProvider(packOutput));
        generator.addProvider(event.includeServer(), new TeapartyRecipeProvider(packOutput));
        
        // This provider generates the actual datagen objects for dynamic registries like DamageType
        ModDatapackBuiltinEntriesProvider datapackProvider = new ModDatapackBuiltinEntriesProvider(packOutput, lookupProvider);
        CompletableFuture<HolderLookup.Provider> providerCompletableFuture = datapackProvider.getRegistryProvider();
        generator.addProvider(event.includeServer(), datapackProvider);
        
        // We use an empty block tag provider since we only need it to instantiate the ItemTagsProvider
        TagsProvider<Block> blockTagsProvider = new net.minecraft.data.tags.VanillaBlockTagsProvider(packOutput, lookupProvider);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        ModItemTagProvider itemTagProvider = new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper);
        generator.addProvider(event.includeServer(), itemTagProvider);
        generator.addProvider(event.includeServer(), new ModDamageTypeTagProvider(packOutput, providerCompletableFuture, existingFileHelper));
    }
}
