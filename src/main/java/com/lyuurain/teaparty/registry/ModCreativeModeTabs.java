package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GemlikeTeaParty.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GEMLIKE_TEA_PARTY = CREATIVE_MODE_TABS.register("gemlike_teaparty", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gemlike_teaparty"))
            .icon(() -> new ItemStack(ModItems.DREAMY_SKY.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MIXING_CUP.get());
                output.accept(ModItems.MIXING_CUP_CAP.get());
                output.accept(ModItems.STIRRER.get());

                output.accept(ModItems.GLACIER.get());
                output.accept(ModItems.END_VISION.get());
                output.accept(ModItems.DREAMY_SKY.get());
                output.accept(ModItems.SIRENS_DEW.get());
                output.accept(ModItems.UNDERGROUND_SUN.get());
                output.accept(ModItems.STRANGE_DRINK.get());
                output.accept(ModItems.STRANGE_DRINK_GLASS.get());

                output.accept(ModItems.BLUEBERRY_JUICE.get());
                output.accept(ModItems.CHERRY_TEA.get());
                output.accept(ModItems.LEMONADE.get());

                output.accept(ModItems.BLUEBERRY.get());
                output.accept(ModItems.ICE_CUBE.get());
                output.accept(ModItems.LEMON.get());
                output.accept(ModItems.RED_GRAPE.get());
                output.accept(ModItems.SOUL.get());
                output.accept(ModItems.MAGIC_BOTTLE.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
