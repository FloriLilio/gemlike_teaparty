package com.lyuurain.teaparty;

import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.event.GlacierEffectEvents;
import com.lyuurain.teaparty.event.RebornEffectEvents;
import com.lyuurain.teaparty.registry.ModCreativeModeTabs;
import com.lyuurain.teaparty.registry.ModEffects;
import com.lyuurain.teaparty.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(GemlikeTeaParty.MODID)
public class GemlikeTeaParty {
    public static final String MODID = "gemlike_teaparty";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GemlikeTeaParty(IEventBus modEventBus, ModContainer modContainer) {
        ModConfig.load();
        ModItems.register(modEventBus);
        ModEffects.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        NeoForge.EVENT_BUS.register(GlacierEffectEvents.class);
        NeoForge.EVENT_BUS.register(RebornEffectEvents.class);
    }
}
