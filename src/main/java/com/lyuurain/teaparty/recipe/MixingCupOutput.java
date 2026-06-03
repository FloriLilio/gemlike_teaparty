package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record MixingCupOutput(ItemStack resultItem, int bottles) {

    public static final Codec<MixingCupOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("result_item").forGetter(MixingCupOutput::resultItem),
            Codec.INT.fieldOf("bottles").forGetter(MixingCupOutput::bottles)
    ).apply(instance, MixingCupOutput::new));
}
