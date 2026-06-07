package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidDefinition.ItemConversion;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;
import java.util.Map;

public class TeapartyLiquidProvider extends ModLiquidProvider {
    public TeapartyLiquidProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildLiquids(Map<ResourceLocation, LiquidDefinition> map) {
        addModLiquid(map, "blueberry_juice", "item.gemlike_teaparty.blueberry_juice", "#442288", "minecraft:block/water_still",
                List.of(new ItemConversion(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "blueberry_juice"), DataComponentPredicate.EMPTY, 1, ResourceLocation.withDefaultNamespace("glass_bottle"))));

        addModLiquid(map, "cherry_tea", "item.gemlike_teaparty.cherry_tea", "#dd6677", "minecraft:block/water_still",
                List.of(new ItemConversion(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "cherry_tea"), DataComponentPredicate.EMPTY, 1, ResourceLocation.withDefaultNamespace("glass_bottle"))));

        addModLiquid(map, "lemonade", "item.gemlike_teaparty.lemonade", "#eecc55", "minecraft:block/water_still",
                List.of(new ItemConversion(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "lemonade"), DataComponentPredicate.EMPTY, 1, ResourceLocation.withDefaultNamespace("glass_bottle"))));

        addModLiquid(map, "strange_drink_glass", "item.gemlike_teaparty.strange_drink_glass", "#9966ff", "minecraft:block/water_still",
                List.of(new ItemConversion(ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "strange_drink_glass"), DataComponentPredicate.EMPTY, 1, ResourceLocation.withDefaultNamespace("glass_bottle"))));

        addVanillaLiquid(map, "honey", "item.minecraft.honey_bottle", "#ffaa00", "minecraft:block/water_still",
                List.of(new ItemConversion(ResourceLocation.withDefaultNamespace("honey_bottle"), DataComponentPredicate.EMPTY, 1, ResourceLocation.withDefaultNamespace("glass_bottle"))));

        addVanillaLiquid(map, "milk", "item.minecraft.milk_bucket", "#ffffff", "minecraft:block/water_still",
                List.of(new ItemConversion(ResourceLocation.withDefaultNamespace("milk_bucket"), DataComponentPredicate.EMPTY, 3, ResourceLocation.withDefaultNamespace("bucket"))));

        addVanillaLiquid(map, "water", "block.minecraft.water", "#3f76e4", "minecraft:block/water_still",
                List.of(
                    new ItemConversion(ResourceLocation.withDefaultNamespace("water_bucket"), DataComponentPredicate.EMPTY, 3, ResourceLocation.withDefaultNamespace("bucket")),
                    new ItemConversion(ResourceLocation.withDefaultNamespace("potion"), DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER)).build(), 1, ResourceLocation.withDefaultNamespace("glass_bottle"))
                ));
    }

    private void addModLiquid(Map<ResourceLocation, LiquidDefinition> map, String id, String name, String color, String texture, List<ItemConversion> items) {
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, id);
        map.put(rl, new LiquidDefinition(rl, rl, name, items, color, ResourceLocation.parse(texture)));
    }

    private void addVanillaLiquid(Map<ResourceLocation, LiquidDefinition> map, String id, String name, String color, String texture, List<ItemConversion> items) {
        ResourceLocation rl = ResourceLocation.withDefaultNamespace(id);
        map.put(rl, new LiquidDefinition(rl, rl, name, items, color, ResourceLocation.parse(texture)));
    }
}
