package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.item.BlueberryJuiceItem;
import com.lyuurain.teaparty.item.DreamySkyItem;
import com.lyuurain.teaparty.item.EffectDrinkItem;
import com.lyuurain.teaparty.item.EndVisionItem;
import com.lyuurain.teaparty.item.GlacierItem;
import com.lyuurain.teaparty.item.IceCubeItem;
import com.lyuurain.teaparty.item.LemonItem;
import com.lyuurain.teaparty.item.MixingCupItem;
import com.lyuurain.teaparty.item.SirensDewItem;
import com.lyuurain.teaparty.item.StrangeDrinkItem;
import com.lyuurain.teaparty.item.TooltipItem;
import com.lyuurain.teaparty.item.UndergroundSunItem;
import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GemlikeTeaParty.MODID);
    private static final FoodProperties ICE_CUBE_FOOD = new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build();
    private static final FoodProperties LEMON_FOOD = new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).alwaysEdible().build();

    public static final DeferredItem<Item> MIXING_CUP = ITEMS.registerItem("mixing_cup", properties -> new MixingCupItem(properties.stacksTo(1)));
    public static final DeferredItem<Item> MIXING_CUP_CAP = ITEMS.registerItem("mixing_cup_cap", properties -> new Item(properties.stacksTo(1)));
    public static final DeferredItem<Item> STIRRER = ITEMS.registerItem("stirrer", properties -> new Item(properties.stacksTo(1)));
    public static final DeferredItem<BlockItem> BLENDER = ITEMS.registerSimpleBlockItem("blender", ModBlocks.BLENDER);

    public static final DeferredItem<Item> GLACIER = ITEMS.registerItem("glacier", properties -> new GlacierItem(properties,
            tooltip("tooltip.gemlike_teaparty.glacier.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> END_VISION = ITEMS.registerItem("end_vision", properties -> new EndVisionItem(properties,
            tooltip("tooltip.gemlike_teaparty.end_vision.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> DREAMY_SKY = ITEMS.registerItem("dreamy_sky", properties -> new DreamySkyItem(properties,
            tooltip("tooltip.gemlike_teaparty.dreamy_sky.0", ChatFormatting.GRAY),
            tooltip("tooltip.gemlike_teaparty.dreamy_sky.1", ChatFormatting.DARK_RED, ChatFormatting.BOLD)));
    public static final DeferredItem<Item> SIRENS_DEW = ITEMS.registerItem("sirens_dew", properties -> new SirensDewItem(properties,
            tooltip("tooltip.gemlike_teaparty.sirens_dew.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> UNDERGROUND_SUN = ITEMS.registerItem("underground_sun", properties -> new UndergroundSunItem(properties,
            tooltip("tooltip.gemlike_teaparty.underground_sun.0", ChatFormatting.RED)));
    public static final DeferredItem<Item> STRANGE_DRINK = ITEMS.registerItem("strange_drink", properties -> new StrangeDrinkItem(properties,
            tooltip("tooltip.gemlike_teaparty.strange_drink.0", ChatFormatting.GRAY)));
    public static final DeferredItem<Item> STRANGE_DRINK_GLASS = ITEMS.registerItem("strange_drink_glass", properties -> new StrangeDrinkItem(properties,
            tooltip("tooltip.gemlike_teaparty.strange_drink_glass.0", ChatFormatting.GRAY)));

    public static final DeferredItem<Item> BLUEBERRY_JUICE = ITEMS.registerItem("blueberry_juice", BlueberryJuiceItem::new);
    public static final DeferredItem<Item> CHERRY_TEA = ITEMS.registerItem("cherry_tea", properties -> new EffectDrinkItem(properties,
            () -> new MobEffectInstance(MobEffects.LEVITATION, 200, 0, false, true, true)));
    public static final DeferredItem<Item> LEMONADE = ITEMS.registerItem("lemonade", properties -> new EffectDrinkItem(properties,
            () -> new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0, false, true, true)));

    public static final DeferredItem<Item> BLUEBERRY = ITEMS.registerItem("blueberry", properties -> new TooltipItem(properties.food(Foods.SWEET_BERRIES)));
    public static final DeferredItem<Item> ICE_CUBE = ITEMS.registerItem("ice_cube", properties -> new IceCubeItem(properties.food(ICE_CUBE_FOOD)));
    public static final DeferredItem<Item> LEMON = ITEMS.registerItem("lemon", properties -> new LemonItem(properties.food(LEMON_FOOD)));
    public static final DeferredItem<Item> RED_GRAPE = ITEMS.registerItem("red_grape", properties -> new TooltipItem(properties.food(Foods.SWEET_BERRIES)));
    public static final DeferredItem<Item> SOUL = ITEMS.registerSimpleItem("soul");
    public static final DeferredItem<Item> MAGIC_BOTTLE = ITEMS.registerItem("magic_bottle", com.lyuurain.teaparty.item.MagicBottleItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static TooltipItem.TooltipLine tooltip(String key, ChatFormatting... styles) {
        return new TooltipItem.TooltipLine(key, styles);
    }
}
