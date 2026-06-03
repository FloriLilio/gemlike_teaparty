package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.config.EndVisionFilterMode;
import com.lyuurain.teaparty.config.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public record EndVisionConfigPayload(int radius, String blockFilterMode, String[] blockFilter, boolean showEntities, String entityFilterMode, String[] entityFilter) implements CustomPacketPayload {
    public static final Type<EndVisionConfigPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "end_vision_config"));
    public static final StreamCodec<FriendlyByteBuf, EndVisionConfigPayload> STREAM_CODEC = CustomPacketPayload.codec(EndVisionConfigPayload::write, EndVisionConfigPayload::read);

    public EndVisionConfigPayload {
        blockFilter = Arrays.copyOf(blockFilter, blockFilter.length);
        entityFilter = Arrays.copyOf(entityFilter, entityFilter.length);
    }

    public static EndVisionConfigPayload fromCommonConfig() {
        return new EndVisionConfigPayload(
                ModConfig.COMMON.endVisionRadius,
                EndVisionFilterMode.fromConfig(ModConfig.COMMON.endVisionBlockFilterMode, EndVisionFilterMode.WHITELIST).configValue(),
                ModConfig.COMMON.endVisionBlockFilter,
                ModConfig.COMMON.endVisionShowEntities,
                EndVisionFilterMode.fromConfig(ModConfig.COMMON.endVisionEntityFilterMode, EndVisionFilterMode.BLACKLIST).configValue(),
                ModConfig.COMMON.endVisionEntityFilter
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public String[] blockFilter() {
        return Arrays.copyOf(this.blockFilter, this.blockFilter.length);
    }

    @Override
    public String[] entityFilter() {
        return Arrays.copyOf(this.entityFilter, this.entityFilter.length);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.radius);
        buffer.writeUtf(this.blockFilterMode);
        writeStringArray(buffer, this.blockFilter);
        buffer.writeBoolean(this.showEntities);
        buffer.writeUtf(this.entityFilterMode);
        writeStringArray(buffer, this.entityFilter);
    }

    private static EndVisionConfigPayload read(FriendlyByteBuf buffer) {
        return new EndVisionConfigPayload(
                buffer.readVarInt(),
                buffer.readUtf(32),
                readStringArray(buffer),
                buffer.readBoolean(),
                buffer.readUtf(32),
                readStringArray(buffer)
        );
    }

    private static void writeStringArray(FriendlyByteBuf buffer, String[] values) {
        buffer.writeVarInt(values.length);

        for (String value : values) {
            buffer.writeUtf(value);
        }
    }

    private static String[] readStringArray(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        String[] values = new String[Math.clamp(size, 0, 1024)];

        for (int i = 0; i < values.length; i++) {
            values[i] = buffer.readUtf();
        }

        return values;
    }
}
