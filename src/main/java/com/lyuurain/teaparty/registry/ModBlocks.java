package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.BlenderBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GemlikeTeaParty.MODID);

    public static final DeferredBlock<Block> BLENDER = BLOCKS.register("blender",
            () -> new BlenderBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()));

    public static final DeferredBlock<Block> MIXING_CUP = BLOCKS.register("mixing_cup",
            () -> new com.lyuurain.teaparty.block.MixingCupBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(net.minecraft.world.level.block.SoundType.GLASS)
                    .noOcclusion()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
