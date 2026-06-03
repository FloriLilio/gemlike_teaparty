package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record LiquidDefinition(ResourceLocation id, ResourceLocation icon, String name, List<ItemConversion> items) {

    public static final Codec<LiquidDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(LiquidDefinition::icon),
            Codec.STRING.fieldOf("name").forGetter(LiquidDefinition::name),
            ItemConversion.CODEC.listOf().fieldOf("items").forGetter(LiquidDefinition::items)
    ).apply(instance, (icon, name, items) -> new LiquidDefinition(null, icon, name, items)));

    public static final StreamCodec<FriendlyByteBuf, LiquidDefinition> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, LiquidDefinition::id,
            ResourceLocation.STREAM_CODEC, LiquidDefinition::icon,
            ByteBufCodecs.STRING_UTF8, LiquidDefinition::name,
            ItemConversion.STREAM_CODEC.apply(ByteBufCodecs.list()), LiquidDefinition::items,
            LiquidDefinition::new
    );

    public LiquidDefinition withId(ResourceLocation id) {
        return new LiquidDefinition(id, this.icon, this.name, this.items);
    }

    public record ItemConversion(ResourceLocation item, int bottles, ResourceLocation container) {
        public static final Codec<ItemConversion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(ItemConversion::item),
                Codec.INT.fieldOf("bottles").forGetter(ItemConversion::bottles),
                ResourceLocation.CODEC.fieldOf("container").forGetter(ItemConversion::container)
        ).apply(instance, ItemConversion::new));

        public static final StreamCodec<FriendlyByteBuf, ItemConversion> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, ItemConversion::item,
                ByteBufCodecs.VAR_INT, ItemConversion::bottles,
                ResourceLocation.STREAM_CODEC, ItemConversion::container,
                ItemConversion::new
        );
    }
}
