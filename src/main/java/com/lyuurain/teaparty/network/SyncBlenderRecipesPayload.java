package com.lyuurain.teaparty.network;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncBlenderRecipesPayload(List<BlenderRecipe> recipes) implements CustomPacketPayload {
    public static final Type<SyncBlenderRecipesPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "sync_blender_recipes"));

    public static final StreamCodec<FriendlyByteBuf, SyncBlenderRecipesPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(BlenderRecipe.CODEC.listOf()),
                    SyncBlenderRecipesPayload::recipes,
                    SyncBlenderRecipesPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
