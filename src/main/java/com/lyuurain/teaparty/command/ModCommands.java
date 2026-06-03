package com.lyuurain.teaparty.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.lyuurain.teaparty.registry.ModAttachments;
import com.lyuurain.teaparty.network.MagicBottleSyncPayload;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Collections;

public class ModCommands {
    private static final com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> SUGGEST_LIQUID = (context, builder) -> {
        return net.minecraft.commands.SharedSuggestionProvider.suggestResource(
            LiquidManager.INSTANCE.getLiquids().keySet(), builder
        );
    };

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
            Commands.literal("gemlike_teaparty")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("magic_bottle")
                    .then(Commands.literal("query")
                        .executes(context -> query(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("targets", EntityArgument.players())
                            .executes(context -> query(context.getSource(), EntityArgument.getPlayers(context, "targets")))
                        )
                    )
                    .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                            .executes(context -> set(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), IntegerArgumentType.getInteger(context, "amount")))
                            .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount")))
                            )
                        )
                    )
                    .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(context -> add(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), IntegerArgumentType.getInteger(context, "amount")))
                            .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> add(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount")))
                            )
                        )
                    )
                )
                .then(Commands.literal("liquid")
                    .then(Commands.argument("liquid_id", ResourceLocationArgument.id())
                        .suggests(SUGGEST_LIQUID)
                        .executes(context -> queryLiquid(context.getSource(), ResourceLocationArgument.getId(context, "liquid_id")))
                    )
                )
        );
    }

    private static int query(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            int count = player.getData(ModAttachments.MAGIC_BOTTLE);
            source.sendSuccess(() -> Component.translatable("commands.gemlike_teaparty.magic_bottle.query", player.getScoreboardName(), count), false);
        }
        return targets.size();
    }

    private static int set(CommandSourceStack source, Collection<ServerPlayer> targets, int amount) {
        int limit = com.lyuurain.teaparty.config.ModConfig.COMMON.maxMagicBottleCount;
        int clampedAmount = Math.min(limit, amount);
        for (ServerPlayer player : targets) {
            player.setData(ModAttachments.MAGIC_BOTTLE, clampedAmount);
            PacketDistributor.sendToPlayer(player, new MagicBottleSyncPayload(clampedAmount));
            source.sendSuccess(() -> Component.translatable("commands.gemlike_teaparty.magic_bottle.set", player.getScoreboardName(), clampedAmount), true);
        }
        return targets.size();
    }

    private static int add(CommandSourceStack source, Collection<ServerPlayer> targets, int amount) {
        int limit = com.lyuurain.teaparty.config.ModConfig.COMMON.maxMagicBottleCount;
        for (ServerPlayer player : targets) {
            int current = player.getData(ModAttachments.MAGIC_BOTTLE);
            int newAmount = Math.clamp(current + amount, 0, limit);
            player.setData(ModAttachments.MAGIC_BOTTLE, newAmount);
            PacketDistributor.sendToPlayer(player, new MagicBottleSyncPayload(newAmount));
            source.sendSuccess(() -> Component.translatable("commands.gemlike_teaparty.magic_bottle.add", player.getScoreboardName(), amount, newAmount), true);
        }
        return targets.size();
    }

    private static int queryLiquid(CommandSourceStack source, ResourceLocation liquidId) {
        LiquidDefinition def = LiquidManager.INSTANCE.getLiquids().get(liquidId);
        if (def == null) {
            source.sendFailure(Component.translatable("commands.gemlike_teaparty.liquid.not_found", liquidId));
            return 0;
        }

        Item iconItem = BuiltInRegistries.ITEM.get(def.icon());
        Component iconName = iconItem != null ? iconItem.getDescription() : Component.literal(def.icon().toString());
        Component liquidName = Component.translatable(def.name());

        source.sendSuccess(() -> Component.translatable("commands.gemlike_teaparty.liquid.header", iconName, liquidName, liquidId.toString()).withStyle(ChatFormatting.GOLD), false);

        for (LiquidDefinition.ItemConversion conv : def.items()) {
            Item inputItem = BuiltInRegistries.ITEM.get(conv.item());
            Item containerItem = BuiltInRegistries.ITEM.get(conv.container());
            Component inputName = inputItem != null ? inputItem.getDescription() : Component.literal(conv.item().toString());
            Component containerName = containerItem != null ? containerItem.getDescription() : Component.literal(conv.container().toString());

            source.sendSuccess(() -> Component.translatable("commands.gemlike_teaparty.liquid.conversion_rule", inputName, containerName, conv.bottles()), false);
        }

        return 1;
    }
}
