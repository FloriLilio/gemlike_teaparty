package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MagicBottleSyncPayload(int count) implements CustomPacketPayload {
    public static final Type<MagicBottleSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "magic_bottle_sync"));
    public static final StreamCodec<FriendlyByteBuf, MagicBottleSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MagicBottleSyncPayload::count,
            MagicBottleSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
