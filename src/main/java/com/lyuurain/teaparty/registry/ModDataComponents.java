package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.MixingCupOutput;
import com.lyuurain.teaparty.recipe.MixingCupProcess;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModDataComponents {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, GemlikeTeaParty.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> OPENED = DATA_COMPONENT_TYPES.register(
            "opened",
            () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<MixingCupProcess>>> PROCESSES = DATA_COMPONENT_TYPES.register(
            "processes",
            () -> DataComponentType.<List<MixingCupProcess>>builder()
                    .persistent(Codec.list(MixingCupProcess.CODEC))
                    .build()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MixingCupOutput>> OUTPUT = DATA_COMPONENT_TYPES.register(
            "output",
            () -> DataComponentType.<MixingCupOutput>builder()
                    .persistent(MixingCupOutput.CODEC)
                    .build()
    );

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
