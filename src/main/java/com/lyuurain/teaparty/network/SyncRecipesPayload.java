package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncRecipesPayload(List<MixingCupRecipe> recipes) implements CustomPacketPayload {
    public static final Type<SyncRecipesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "sync_recipes"));

    public static final StreamCodec<FriendlyByteBuf, SyncRecipesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(MixingCupRecipe.CODEC.listOf()), SyncRecipesPayload::recipes,
            SyncRecipesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
