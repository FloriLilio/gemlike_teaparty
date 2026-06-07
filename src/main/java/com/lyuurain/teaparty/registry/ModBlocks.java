package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.BlueBerryBushBlock;
import com.lyuurain.teaparty.block.BlenderBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GemlikeTeaParty.MODID);

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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
