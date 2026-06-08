package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.registry.ModAttachments;
import com.lyuurain.teaparty.network.MagicBottleSyncPayload;
import com.lyuurain.teaparty.registry.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class MagicBottleEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ItemStack stack = event.getItem();
            if (stack.is(ModTags.Items.ADVANCED_DRINKS)) {
                int current = serverPlayer.getData(ModAttachments.MAGIC_BOTTLE);
                int limit = com.lyuurain.teaparty.config.ModConfig.COMMON.maxMagicBottleCount;
                int next = Math.min(limit, current + 1);
                serverPlayer.setData(ModAttachments.MAGIC_BOTTLE, next);
                sync(serverPlayer);

                if (!serverPlayer.getAbilities().instabuild) {
                    ItemStack result = event.getResultStack();
                    if (current < limit) {
                        if (result.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
                            ItemStack shrunken = stack.copy();
                            shrunken.shrink(1);
                            event.setResultStack(shrunken);
                        }
                    } else {
                        ItemStack magicBottle = new ItemStack(com.lyuurain.teaparty.registry.ModItems.MAGIC_BOTTLE.get());
                        if (result.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
                            event.setResultStack(magicBottle);
                        } else {
                            for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
                                ItemStack invStack = serverPlayer.getInventory().getItem(i);
                                if (invStack.is(net.minecraft.world.item.Items.GLASS_BOTTLE)) {
                                    invStack.shrink(1);
                                    break;
                                }
                            }
                            if (!serverPlayer.getInventory().add(magicBottle)) {
                                serverPlayer.drop(magicBottle, false);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void sync(ServerPlayer player) {
        int count = player.getData(ModAttachments.MAGIC_BOTTLE);
        PacketDistributor.sendToPlayer(player, new MagicBottleSyncPayload(count));
    }
}
