package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record LiquidDefinition(ResourceLocation id, ResourceLocation icon, String name, List<ItemConversion> items, String color, ResourceLocation texture) {

    public static final Codec<LiquidDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("icon").forGetter(LiquidDefinition::icon),
            Codec.STRING.fieldOf("name").forGetter(LiquidDefinition::name),
            ItemConversion.CODEC.listOf().fieldOf("items").forGetter(LiquidDefinition::items),
            Codec.STRING.optionalFieldOf("color", "#FFFFFFFF").forGetter(LiquidDefinition::color),
            ResourceLocation.CODEC.optionalFieldOf("texture", ResourceLocation.parse("minecraft:block/water_still")).forGetter(LiquidDefinition::texture)
    ).apply(instance, (icon, name, items, color, texture) -> new LiquidDefinition(null, icon, name, items, color, texture)));

    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, LiquidDefinition> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, LiquidDefinition::id,
            ResourceLocation.STREAM_CODEC, LiquidDefinition::icon,
            ByteBufCodecs.STRING_UTF8, LiquidDefinition::name,
            ItemConversion.STREAM_CODEC.apply(ByteBufCodecs.list()), LiquidDefinition::items,
            ByteBufCodecs.STRING_UTF8, LiquidDefinition::color,
            ResourceLocation.STREAM_CODEC, LiquidDefinition::texture,
            LiquidDefinition::new
    );

    public LiquidDefinition withId(ResourceLocation id) {
        return new LiquidDefinition(id, this.icon, this.name, this.items, this.color, this.texture);
    }

    public int getColorValue() {
        if (this.color == null) {
            return 0xFFFFFFFF;
        }
        try {
            if (this.color.startsWith("#")) {
                return (int) Long.parseLong(this.color.substring(1), 16);
            } else if (this.color.startsWith("0x") || this.color.startsWith("0X")) {
                return (int) Long.parseLong(this.color.substring(2), 16);
            } else {
                return (int) Long.parseLong(this.color, 16);
            }
        } catch (NumberFormatException e) {
            return 0xFFFFFFFF;
        }
    }

    public record ItemConversion(ResourceLocation item, net.minecraft.core.component.DataComponentPredicate components, int bottles, ResourceLocation container) {
        public static final Codec<ItemConversion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(ItemConversion::item),
                net.minecraft.core.component.DataComponentPredicate.CODEC.optionalFieldOf("components", net.minecraft.core.component.DataComponentPredicate.EMPTY).forGetter(ItemConversion::components),
                Codec.INT.fieldOf("bottles").forGetter(ItemConversion::bottles),
                ResourceLocation.CODEC.fieldOf("container").forGetter(ItemConversion::container)
        ).apply(instance, ItemConversion::new));

        public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ItemConversion> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, ItemConversion::item,
                net.minecraft.core.component.DataComponentPredicate.STREAM_CODEC, ItemConversion::components,
                ByteBufCodecs.VAR_INT, ItemConversion::bottles,
                ResourceLocation.STREAM_CODEC, ItemConversion::container,
                ItemConversion::new
        );
    }
}
