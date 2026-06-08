package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public static final TagKey<Item> C_DRINKS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "drinks"));
    public static final TagKey<Item> C_FOODS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods"));
    public static final TagKey<Item> C_CROPS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crops"));
    public static final TagKey<Item> C_SEEDS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"));
    public static final TagKey<Item> C_TOOLS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools"));
    public static final TagKey<Item> C_FRUITS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/fruit"));
    public static final TagKey<Item> C_BERRIES = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/berry"));
    public static final TagKey<Item> C_JUICES = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "drinks/juice"));

    public static final TagKey<Item> TEAPARTY_ADVANCED_DRINKS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "advanced_drinks"));
    
    public ModItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagProvider, GemlikeTeaParty.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(C_DRINKS).add(
            ModItems.GLACIER.get(), ModItems.END_VISION.get(), ModItems.DREAMY_SKY.get(),
            ModItems.SIRENS_DEW.get(), ModItems.UNDERGROUND_SUN.get(), ModItems.STRANGE_DRINK.get(),
            ModItems.STRANGE_DRINK_GLASS.get(), ModItems.BLUEBERRY_JUICE.get(), ModItems.CHERRY_TEA.get(),
            ModItems.LEMONADE.get()
        );

        tag(C_FOODS).add(
            ModItems.BLUEBERRY.get(), ModItems.LEMON.get(), ModItems.RED_GRAPE.get()
        );

        tag(C_CROPS).add(
            ModItems.BLUEBERRY.get(), ModItems.LEMON.get(), ModItems.RED_GRAPE.get()
        );

        tag(C_SEEDS).add(ModItems.RED_GRAPE_SEEDS.get());

        tag(C_TOOLS).add(
            ModItems.MIXING_CUP.get(), ModItems.STIRRER.get(), ModItems.BLENDER_LIGHT.get(), ModItems.BLENDER_DARK.get()
        );

        tag(C_FRUITS).add(ModItems.LEMON.get());
        tag(C_BERRIES).add(ModItems.BLUEBERRY.get(), ModItems.RED_GRAPE.get());
        tag(C_JUICES).add(ModItems.BLUEBERRY_JUICE.get());

        tag(TEAPARTY_ADVANCED_DRINKS).add(
            ModItems.GLACIER.get(), ModItems.END_VISION.get(), ModItems.DREAMY_SKY.get(),
            ModItems.SIRENS_DEW.get(), ModItems.UNDERGROUND_SUN.get()
        );

        tag(ItemTags.FOX_FOOD).add(ModItems.BLUEBERRY.get(), ModItems.RED_GRAPE.get());

        tag(ItemTags.LOGS_THAT_BURN).add(
                ModBlocks.LEMON_LOG.asItem(),
                ModBlocks.STRIPPED_LEMON_LOG.asItem()
        );
        tag(ItemTags.PLANKS).add(ModBlocks.LEMON_PLANKS.asItem());
        tag(ItemTags.LEAVES).add(ModBlocks.LEMON_LEAVES.asItem());
        tag(ItemTags.SAPLINGS).add(ModBlocks.LEMON_SAPLING.asItem());
        tag(ItemTags.WOODEN_DOORS).add(ModBlocks.LEMON_DOOR.asItem());
        tag(ItemTags.WOODEN_TRAPDOORS).add(ModBlocks.LEMON_TRAPDOOR.asItem());
        tag(ItemTags.WOODEN_STAIRS).add(ModBlocks.LEMON_STAIRS.asItem());
        tag(ItemTags.WOODEN_SLABS).add(ModBlocks.LEMON_SLAB.asItem());
        tag(ItemTags.WOODEN_FENCES).add(ModBlocks.LEMON_FENCE.asItem());
        tag(ItemTags.WOODEN_BUTTONS).add(ModBlocks.LEMON_BUTTON.asItem());
        tag(ItemTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.LEMON_PRESSURE_PLATE.asItem());
        tag(ItemTags.SIGNS).add(ModBlocks.LEMON_SIGN.asItem());
        tag(ItemTags.HANGING_SIGNS).add(ModBlocks.LEMON_HANGING_SIGN.asItem());
        tag(ItemTags.BOATS).add(ModItems.LEMON_BOAT.get());
        tag(ItemTags.CHEST_BOATS).add(ModItems.LEMON_CHEST_BOAT.get());

        copy(ModBlockTagProvider.LEMON_LOGS, TagKey.create(net.minecraft.core.registries.Registries.ITEM, ModBlockTagProvider.LEMON_LOGS.location()));
    }
}
