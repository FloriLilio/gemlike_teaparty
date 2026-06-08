package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public static final TagKey<Block> LEMON_LOGS = TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemon_logs"));

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, GemlikeTeaParty.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(LEMON_LOGS).add(ModBlocks.LEMON_LOG.get(), ModBlocks.STRIPPED_LEMON_LOG.get());

        tag(BlockTags.LOGS_THAT_BURN).addTag(LEMON_LOGS);

        tag(BlockTags.MINEABLE_WITH_AXE).add(
                ModBlocks.LEMON_LOG.get(),
                ModBlocks.STRIPPED_LEMON_LOG.get(),
                ModBlocks.LEMON_PLANKS.get(),
                ModBlocks.LEMON_DOOR.get(),
                ModBlocks.LEMON_TRAPDOOR.get(),
                ModBlocks.LEMON_STAIRS.get(),
                ModBlocks.LEMON_SLAB.get(),
                ModBlocks.LEMON_FENCE.get(),
                ModBlocks.LEMON_FENCE_GATE.get(),
                ModBlocks.LEMON_BUTTON.get(),
                ModBlocks.LEMON_PRESSURE_PLATE.get(),
                ModBlocks.LEMON_SIGN.get(),
                ModBlocks.LEMON_HANGING_SIGN.get()
        );

        tag(BlockTags.MINEABLE_WITH_HOE).add(
                ModBlocks.LEMON_LEAVES.get(),
                ModBlocks.LEMON_CROP.get(),
                ModBlocks.RED_GRAPE_VINE.get(),
                ModBlocks.BAGGED_LEMON_BLOCK.get(),
                ModBlocks.BAGGED_BLUEBERRY_BLOCK.get(),
                ModBlocks.BAGGED_RED_GRAPE_BLOCK.get()
        );

        tag(BlockTags.CROPS).add(ModBlocks.LEMON_CROP.get(), ModBlocks.RED_GRAPE_VINE.get());
        tag(BlockTags.CLIMBABLE).add(ModBlocks.RED_GRAPE_VINE.get());
        tag(BlockTags.LEAVES).add(ModBlocks.LEMON_LEAVES.get());
        tag(BlockTags.PLANKS).add(ModBlocks.LEMON_PLANKS.get());
        tag(BlockTags.SAPLINGS).add(ModBlocks.LEMON_SAPLING.get());

        tag(BlockTags.WOODEN_DOORS).add(ModBlocks.LEMON_DOOR.get());
        tag(BlockTags.DOORS).add(ModBlocks.LEMON_DOOR.get());

        tag(BlockTags.WOODEN_TRAPDOORS).add(ModBlocks.LEMON_TRAPDOOR.get());
        tag(BlockTags.TRAPDOORS).add(ModBlocks.LEMON_TRAPDOOR.get());

        tag(BlockTags.STAIRS).add(ModBlocks.LEMON_STAIRS.get());
        tag(BlockTags.WOODEN_STAIRS).add(ModBlocks.LEMON_STAIRS.get());
        tag(BlockTags.SLABS).add(ModBlocks.LEMON_SLAB.get());
        tag(BlockTags.WOODEN_SLABS).add(ModBlocks.LEMON_SLAB.get());
        tag(BlockTags.FENCES).add(ModBlocks.LEMON_FENCE.get());
        tag(BlockTags.WOODEN_FENCES).add(ModBlocks.LEMON_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(ModBlocks.LEMON_FENCE_GATE.get());
        tag(BlockTags.WOODEN_BUTTONS).add(ModBlocks.LEMON_BUTTON.get());
        tag(BlockTags.BUTTONS).add(ModBlocks.LEMON_BUTTON.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(ModBlocks.LEMON_PRESSURE_PLATE.get());
        tag(BlockTags.PRESSURE_PLATES).add(ModBlocks.LEMON_PRESSURE_PLATE.get());

        tag(BlockTags.STANDING_SIGNS).add(ModBlocks.LEMON_SIGN.get());
        tag(BlockTags.WALL_SIGNS).add(ModBlocks.LEMON_WALL_SIGN.get());
        tag(BlockTags.CEILING_HANGING_SIGNS).add(ModBlocks.LEMON_HANGING_SIGN.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(ModBlocks.LEMON_WALL_HANGING_SIGN.get());

        tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.LEMON_SAPLING.get());
    }
}
