package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncMagicBottleZeroPayload() implements CustomPacketPayload {
    public static final Type<SyncMagicBottleZeroPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "sync_magic_bottle_zero"));

    public static final StreamCodec<FriendlyByteBuf, SyncMagicBottleZeroPayload> STREAM_CODEC = StreamCodec.unit(new SyncMagicBottleZeroPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
