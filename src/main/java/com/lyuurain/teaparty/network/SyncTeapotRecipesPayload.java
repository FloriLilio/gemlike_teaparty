package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.TeapotRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncTeapotRecipesPayload(List<TeapotRecipe> recipes) implements CustomPacketPayload {
    public static final Type<SyncTeapotRecipesPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "sync_teapot_recipes"));

    public static final StreamCodec<FriendlyByteBuf, SyncTeapotRecipesPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(TeapotRecipe.CODEC.listOf()),
                    SyncTeapotRecipesPayload::recipes,
                    SyncTeapotRecipesPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
