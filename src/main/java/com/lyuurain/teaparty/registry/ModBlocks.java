package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.BlueBerryBushBlock;
import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.block.LemonCropBlock;
import com.lyuurain.teaparty.block.LemonLeavesBlock;
import com.lyuurain.teaparty.block.LemonLogBlock;
import com.lyuurain.teaparty.block.RedGrapeVineBlock;
import com.lyuurain.teaparty.block.StrippedLemonLogBlock;
import com.lyuurain.teaparty.datagen.ModTreeFeatureProvider;
import java.util.Optional;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GemlikeTeaParty.MODID);

    public static final TreeGrower LEMON_TREE_GROWER = new TreeGrower(
            "lemon",
            Optional.empty(),
            Optional.of(ModTreeFeatureProvider.LEMON),
            Optional.empty()
    );


    public static final WoodType LEMON_WOOD_TYPE = WoodType.register(
            new WoodType("lemon", BlockSetType.OAK));


    public static final DeferredBlock<Block> BLENDER_LIGHT = BLOCKS.register("blender_light",
            () -> new BlenderBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BLENDER_DARK = BLOCKS.register("blender_dark",
            () -> new BlenderBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()));

    public static final DeferredBlock<Block> MIXING_CUP = BLOCKS.register("mixing_cup",
            () -> new com.lyuurain.teaparty.block.MixingCupBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));

    public static final DeferredBlock<Block> BLUE_BERRY_BUSH = BLOCKS.register("blue_berry_bush",
            () -> new BlueBerryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));

    public static final DeferredBlock<LemonCropBlock> LEMON_CROP = BLOCKS.register("lemon_crop",
            () -> new LemonCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));


    public static final DeferredBlock<RedGrapeVineBlock> RED_GRAPE_VINE = BLOCKS.register("red_grape_vine",
            () -> new RedGrapeVineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE).randomTicks()));

    public static final DeferredBlock<Block> BAGGED_LEMON_BLOCK = BLOCKS.register("bagged_lemon_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)));

    public static final DeferredBlock<Block> BAGGED_BLUEBERRY_BLOCK = BLOCKS.register("bagged_blueberry_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)));

    public static final DeferredBlock<Block> BAGGED_RED_GRAPE_BLOCK = BLOCKS.register("bagged_red_grape_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)));

    public static final DeferredBlock<Block> LEMON_LOG = BLOCKS.register("lemon_log",
            () -> new LemonLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));

    public static final DeferredBlock<Block> STRIPPED_LEMON_LOG = BLOCKS.register("stripped_lemon_log",
            () -> new StrippedLemonLogBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)));

    public static final DeferredBlock<Block> LEMON_PLANKS = BLOCKS.register("lemon_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final DeferredBlock<LemonLeavesBlock> LEMON_LEAVES = BLOCKS.register("lemon_leaves",
            () -> new LemonLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));

    public static final DeferredBlock<Block> LEMON_SAPLING = BLOCKS.register("lemon_sapling",
            () -> new SaplingBlock(LEMON_TREE_GROWER, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> LEMON_DOOR = BLOCKS.register("lemon_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)));

    public static final DeferredBlock<Block> LEMON_TRAPDOOR = BLOCKS.register("lemon_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final DeferredBlock<Block> LEMON_STAIRS = BLOCKS.register("lemon_stairs",
            () -> new StairBlock(LEMON_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)));

    public static final DeferredBlock<Block> LEMON_SLAB = BLOCKS.register("lemon_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)));

    public static final DeferredBlock<Block> LEMON_FENCE = BLOCKS.register("lemon_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));

    public static final DeferredBlock<Block> LEMON_FENCE_GATE = BLOCKS.register("lemon_fence_gate",
            () -> new FenceGateBlock(LEMON_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final DeferredBlock<Block> LEMON_BUTTON = BLOCKS.register("lemon_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));

    public static final DeferredBlock<Block> LEMON_PRESSURE_PLATE = BLOCKS.register("lemon_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final DeferredBlock<Block> LEMON_SIGN = BLOCKS.register("lemon_sign",
            () -> new StandingSignBlock(LEMON_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));

    public static final DeferredBlock<Block> LEMON_WALL_SIGN = BLOCKS.register("lemon_wall_sign",
            () -> new WallSignBlock(LEMON_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN)));

    public static final DeferredBlock<Block> LEMON_HANGING_SIGN = BLOCKS.register("lemon_hanging_sign",
            () -> new CeilingHangingSignBlock(LEMON_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));

    public static final DeferredBlock<Block> LEMON_WALL_HANGING_SIGN = BLOCKS.register("lemon_wall_hanging_sign",
            () -> new WallHangingSignBlock(LEMON_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)));
















    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
