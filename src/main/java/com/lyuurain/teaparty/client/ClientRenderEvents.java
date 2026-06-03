package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.registry.ModDataComponents;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientRenderEvents {
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(com.lyuurain.teaparty.registry.ModBlockEntities.BLENDER_BE.get(), BlenderBlockEntityRenderer::new);
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            addLayer(event.getRenderer(entityType));
        }

        for (var skin : event.getSkins()) {
            addPlayerLayer(event.getSkin(skin));
        }
    }

    public static void onRegisterGuiLayers(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
        event.registerAbove(
                net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.lyuurain.teaparty.GemlikeTeaParty.MODID, "magic_bottle_hud"),
                new MagicBottleHudRenderer()
        );
    }

    public static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.MIXING_CUP.get(),
                    ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "opened"),
                    (stack, level, entity, seed) -> stack.getOrDefault(ModDataComponents.OPENED.get(), false) ? 1.0F : 0.0F
            );
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Entity> void addLayer(EntityRenderer<T> renderer) {
        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new FrozenTintLayer(livingRenderer));
        }
    }

    private static void addPlayerLayer(PlayerRenderer renderer) {
        addPlayerLayerTyped(renderer);
    }

    private static void addPlayerLayerTyped(LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        renderer.addLayer(new FrozenTintLayer<>(renderer));
    }
}
