package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncLiquidsPayload(List<LiquidDefinition> liquids) implements CustomPacketPayload {
    public static final Type<SyncLiquidsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "sync_liquids"));

    public static final StreamCodec<FriendlyByteBuf, SyncLiquidsPayload> STREAM_CODEC = StreamCodec.composite(
            LiquidDefinition.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncLiquidsPayload::liquids,
            SyncLiquidsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
