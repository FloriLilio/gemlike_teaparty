package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.block.BlueBerryBushBlock;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

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

        add(ModBlocks.BLUE_BERRY_BUSH.get(), createBlueBerryBushLootTable());

        dropSelf(ModBlocks.LEMON_LOG.get());
        dropSelf(ModBlocks.STRIPPED_LEMON_LOG.get());
        dropSelf(ModBlocks.LEMON_PLANKS.get());
        add(ModBlocks.LEMON_LEAVES.get(), block -> createLeavesDrops(block, ModBlocks.LEMON_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(ModBlocks.LEMON_SAPLING.get());
        add(ModBlocks.LEMON_DOOR.get(), this::createDoorTable);
        dropSelf(ModBlocks.LEMON_TRAPDOOR.get());

        dropSelf(ModBlocks.LEMON_STAIRS.get());
        add(ModBlocks.LEMON_SLAB.get(), this::createSlabItemTable);
        dropSelf(ModBlocks.LEMON_FENCE.get());
        dropSelf(ModBlocks.LEMON_FENCE_GATE.get());
        dropSelf(ModBlocks.LEMON_BUTTON.get());
        dropSelf(ModBlocks.LEMON_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.LEMON_SIGN.get());
        add(ModBlocks.LEMON_WALL_SIGN.get(), block -> LootTable.lootTable());
        dropSelf(ModBlocks.LEMON_HANGING_SIGN.get());
        add(ModBlocks.LEMON_WALL_HANGING_SIGN.get(), block -> LootTable.lootTable());
    }

    private LootTable.Builder createBlueBerryBushLootTable() {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .when(hasSilkTouch())
                                .add(LootItem.lootTableItem(ModBlocks.BLUE_BERRY_BUSH.get()))
                )
                .withPool(
                        LootPool.lootPool()
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.BLUE_BERRY_BUSH.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlueBerryBushBlock.AGE, 3))
                                )
                                .add(
                                        LootItem.lootTableItem(ModItems.BLUEBERRY.get())
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f)))
                                                .apply(ApplyBonusCount.addUniformBonusCount(this.registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE)))
                                )
                );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(sup -> (Block)sup.get())::iterator;
    }
}
