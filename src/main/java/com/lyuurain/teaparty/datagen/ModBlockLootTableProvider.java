package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Collections;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    protected void generate() {
        add(ModBlocks.BLENDER_LIGHT.get(), block -> createSinglePropConditionTable(block, BlenderBlock.HALF, DoubleBlockHalf.LOWER));
        add(ModBlocks.BLENDER_DARK.get(), block -> createSinglePropConditionTable(block, BlenderBlock.HALF, DoubleBlockHalf.LOWER));
        
        dropSelf(ModBlocks.MIXING_CUP.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(sup -> (Block)sup.get())::iterator;
    }
}
