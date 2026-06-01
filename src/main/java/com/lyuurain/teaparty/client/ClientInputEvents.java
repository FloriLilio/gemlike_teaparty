package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.item.GlacierItem;
import com.lyuurain.teaparty.registry.ModEffects;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

public class ClientInputEvents {
    @SubscribeEvent
    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        Player player = event.getEntity();

        if (GlacierItem.isDrinkingGlacier(player) || player.hasEffect(ModEffects.FROZEN)) {
            clearInput(event.getInput());
        }
    }

    private static void clearInput(Input input) {
        input.leftImpulse = 0.0F;
        input.forwardImpulse = 0.0F;
        input.up = false;
        input.down = false;
        input.left = false;
        input.right = false;
        input.jumping = false;
    }
}
