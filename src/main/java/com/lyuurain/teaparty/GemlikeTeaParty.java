package com.lyuurain.teaparty;

import com.lyuurain.teaparty.client.ClientFrozenIceBlockEvents;
import com.lyuurain.teaparty.client.ClientInputEvents;
import com.lyuurain.teaparty.client.ClientRenderEvents;
import com.lyuurain.teaparty.client.EndVisionRenderEvents;
import com.lyuurain.teaparty.client.GemlikeTeaPartyConfigScreen;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.lyuurain.teaparty.event.EndVisionEffectEvents;
import com.lyuurain.teaparty.event.FusionEffectEvents;
import com.lyuurain.teaparty.event.GlacierEffectEvents;
import com.lyuurain.teaparty.event.LiesRhymeEffectEvents;
import com.lyuurain.teaparty.event.RebornEffectEvents;
import com.lyuurain.teaparty.network.ModNetworking;
import com.lyuurain.teaparty.registry.ModCreativeModeTabs;
import com.lyuurain.teaparty.registry.ModEffects;
import com.lyuurain.teaparty.registry.ModItems;
import com.lyuurain.teaparty.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(GemlikeTeaParty.MODID)
public class GemlikeTeaParty {
    public static final String MODID = "gemlike_teaparty";
    public static final Logger LOGGER = LogUtils.getLogger();

    public GemlikeTeaParty(IEventBus modEventBus, ModContainer modContainer) {
        ModConfig.load();
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        com.lyuurain.teaparty.registry.ModRecipes.register(modEventBus);
        com.lyuurain.teaparty.registry.ModAttachments.register(modEventBus);
        com.lyuurain.teaparty.registry.ModDataComponents.register(modEventBus);
        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        modEventBus.addListener(ModNetworking::register);
        NeoForge.EVENT_BUS.register(GlacierEffectEvents.class);
        NeoForge.EVENT_BUS.register(RebornEffectEvents.class);
        NeoForge.EVENT_BUS.register(EndVisionEffectEvents.class);
        NeoForge.EVENT_BUS.register(FusionEffectEvents.class);
        NeoForge.EVENT_BUS.register(LiesRhymeEffectEvents.class);
        NeoForge.EVENT_BUS.register(com.lyuurain.teaparty.event.MagicBottleEvents.class);
        NeoForge.EVENT_BUS.register(com.lyuurain.teaparty.event.RecipeEvents.class);
        NeoForge.EVENT_BUS.register(com.lyuurain.teaparty.command.ModCommands.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> new GemlikeTeaPartyConfigScreen(parent));
            modEventBus.addListener(ClientRenderEvents::onAddLayers);
            modEventBus.addListener(ClientRenderEvents::onRegisterRenderers);
            modEventBus.addListener(com.lyuurain.teaparty.client.ClientRenderEvents::onRegisterGuiLayers);
            modEventBus.addListener(ClientRenderEvents::onClientSetup);
            NeoForge.EVENT_BUS.register(ClientFrozenIceBlockEvents.class);
            NeoForge.EVENT_BUS.register(ClientInputEvents.class);
            NeoForge.EVENT_BUS.register(EndVisionRenderEvents.class);
        }
    }
}
