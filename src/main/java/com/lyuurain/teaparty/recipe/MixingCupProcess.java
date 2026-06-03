package com.lyuurain.teaparty.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record MixingCupProcess(List<ItemStack> items, List<LiquidStack> liquids, ProcessAction action) {

    public static final Codec<MixingCupProcess> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(MixingCupProcess::items),
            LiquidStack.CODEC.listOf().fieldOf("liquids").forGetter(MixingCupProcess::liquids),
            ProcessAction.CODEC.fieldOf("action").forGetter(MixingCupProcess::action)
    ).apply(instance, MixingCupProcess::new));

    public static MixingCupProcess empty() {
        return new MixingCupProcess(new ArrayList<>(), new ArrayList<>(), ProcessAction.NONE);
    }

    public boolean isEmpty() {
        return items.isEmpty() && liquids.isEmpty() && action == ProcessAction.NONE;
    }

    public record LiquidStack(ResourceLocation liquid, int bottles) {
        public static final Codec<LiquidStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("liquid").forGetter(LiquidStack::liquid),
                Codec.INT.fieldOf("bottles").forGetter(LiquidStack::bottles)
        ).apply(instance, LiquidStack::new));
    }

    public enum ProcessAction {
        NONE("none"),
        STIR("stir"),
        SHAKE("shake");

        private final String name;

        ProcessAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static final Codec<ProcessAction> CODEC = Codec.STRING.xmap(
                s -> {
                    for (ProcessAction a : values()) {
                        if (a.name.equalsIgnoreCase(s)) {
                            return a;
                        }
                    }
                    return NONE;
                },
                ProcessAction::getName
        );
    }
}
