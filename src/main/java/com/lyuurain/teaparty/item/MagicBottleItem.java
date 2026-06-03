package com.lyuurain.teaparty.item;

import com.lyuurain.teaparty.registry.ModAttachments;
import com.lyuurain.teaparty.network.MagicBottleSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class MagicBottleItem extends Item {
    public MagicBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int current = level.isClientSide ? com.lyuurain.teaparty.client.ClientMagicBottleCache.count : player.getData(ModAttachments.MAGIC_BOTTLE);
        if (current >= com.lyuurain.teaparty.config.ModConfig.COMMON.maxMagicBottleCount) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setData(ModAttachments.MAGIC_BOTTLE, current + 1);
                PacketDistributor.sendToPlayer(serverPlayer, new MagicBottleSyncPayload(current + 1));
            }
        }
        if (!player.hasInfiniteMaterials()) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
