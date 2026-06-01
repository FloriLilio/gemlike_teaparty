package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, GemlikeTeaParty.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BAKA = SOUNDS.register("baka", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "baka")));
    public static final DeferredHolder<SoundEvent, SoundEvent> FROZEN = SOUNDS.register("frozen", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "frozen")));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
