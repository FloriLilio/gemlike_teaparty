package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.block.BlueBerryBushBlock;
import com.lyuurain.teaparty.block.LemonCropBlock;
import com.lyuurain.teaparty.block.RedGrapeVineBlock;
import com.lyuurain.teaparty.block.TeaCropBlock;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import java.util.stream.Stream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    protected void generate() {
        add(Blocks.ICE, createSingleItemTable(ModItems.ICE_CUBE.get()));

        add(ModBlocks.BLENDER_LIGHT.get(), block -> createSinglePropConditionTable(block, BlenderBlock.HALF, DoubleBlockHalf.LOWER));
        add(ModBlocks.BLENDER_DARK.get(), block -> createSinglePropConditionTable(block, BlenderBlock.HALF, DoubleBlockHalf.LOWER));

        dropSelf(ModBlocks.MIXING_CUP.get());
        dropSelf(ModBlocks.TEAPOT.get());
        add(ModBlocks.GLACIER.get(), createSingleItemTable(ModItems.GLACIER.get()));
        add(ModBlocks.END_VISION.get(), createSingleItemTable(ModItems.END_VISION.get()));
        add(ModBlocks.DREAMY_SKY.get(), createSingleItemTable(ModItems.DREAMY_SKY.get()));
        add(ModBlocks.SIRENS_DEW.get(), createSingleItemTable(ModItems.SIRENS_DEW.get()));
        add(ModBlocks.UNDERGROUND_SUN.get(), createSingleItemTable(ModItems.UNDERGROUND_SUN.get()));

        add(ModBlocks.BLUE_BERRY_BUSH.get(), createBlueBerryBushLootTable());
        add(ModBlocks.LEMON_CROP.get(), createLemonCropLootTable());
        add(ModBlocks.TEA_CROP.get(), createTeaCropLootTable());
        add(ModBlocks.RED_GRAPE_VINE.get(), createRedGrapeVineLootTable());

        dropSelf(ModBlocks.BAGGED_LEMON_BLOCK.get());
        dropSelf(ModBlocks.BAGGED_BLUEBERRY_BLOCK.get());
        dropSelf(ModBlocks.BAGGED_RED_GRAPE_BLOCK.get());

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

        dropSelf(ModBlocks.TEA_LOG.get());
        dropSelf(ModBlocks.STRIPPED_TEA_LOG.get());
        dropSelf(ModBlocks.TEA_PLANKS.get());
        add(ModBlocks.TEA_LEAVES.get(), block -> createLeavesDrops(block, ModBlocks.TEA_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(ModBlocks.TEA_SAPLING.get());
        add(ModBlocks.TEA_DOOR.get(), this::createDoorTable);
        dropSelf(ModBlocks.TEA_TRAPDOOR.get());
        dropSelf(ModBlocks.TEA_STAIRS.get());
        add(ModBlocks.TEA_SLAB.get(), this::createSlabItemTable);
        dropSelf(ModBlocks.TEA_FENCE.get());
        dropSelf(ModBlocks.TEA_FENCE_GATE.get());
        dropSelf(ModBlocks.TEA_BUTTON.get());
        dropSelf(ModBlocks.TEA_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.TEA_SIGN.get());
        add(ModBlocks.TEA_WALL_SIGN.get(), block -> LootTable.lootTable());
        dropSelf(ModBlocks.TEA_HANGING_SIGN.get());
        add(ModBlocks.TEA_WALL_HANGING_SIGN.get(), block -> LootTable.lootTable());
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

    private LootTable.Builder createLemonCropLootTable() {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.LEMON_CROP.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(LemonCropBlock.AGE, 2))
                                )
                                .add(LootItem.lootTableItem(ModItems.LEMON.get()))
                );
    }

    private LootTable.Builder createTeaCropLootTable() {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.TEA_CROP.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TeaCropBlock.AGE, 2))
                                )
                                .add(LootItem.lootTableItem(ModItems.TEA_LEAF.get()))
                );
    }

    private LootTable.Builder createRedGrapeVineLootTable() {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.RED_GRAPE_SEEDS.get()))
                )
                .withPool(
                        LootPool.lootPool()
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.RED_GRAPE_VINE.get())
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RedGrapeVineBlock.AGE, 3))
                                )
                                .add(
                                        LootItem.lootTableItem(ModItems.RED_GRAPE.get())
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 3.0f)))
                                )
                );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.concat(ModBlocks.BLOCKS.getEntries().stream().map(sup -> (Block)sup.get()), Stream.of(Blocks.ICE))::iterator;
    }
}
