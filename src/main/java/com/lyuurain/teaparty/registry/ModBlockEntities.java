package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GemlikeTeaParty.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlenderBlockEntity>> BLENDER_BE =
            BLOCK_ENTITIES.register("blender", () -> BlockEntityType.Builder.of(BlenderBlockEntity::new, ModBlocks.BLENDER_LIGHT.get(), ModBlocks.BLENDER_DARK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.lyuurain.teaparty.block.entity.MixingCupBlockEntity>> MIXING_CUP_BE =
            BLOCK_ENTITIES.register("mixing_cup", () -> BlockEntityType.Builder.of(com.lyuurain.teaparty.block.entity.MixingCupBlockEntity::new, ModBlocks.MIXING_CUP.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
