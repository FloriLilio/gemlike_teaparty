package com.lyuurain.teaparty.item;

import com.lyuurain.teaparty.registry.ModAttachments;
import com.lyuurain.teaparty.registry.ModDataComponents;
import com.lyuurain.teaparty.registry.ModItems;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.recipe.MixingCupProcess;
import com.lyuurain.teaparty.recipe.MixingCupOutput;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.RecipeManager;
import com.lyuurain.teaparty.recipe.MixingCupRecipe;
import com.lyuurain.teaparty.network.MagicBottleSyncPayload;
import com.lyuurain.teaparty.network.SyncMagicBottleZeroPayload;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class MixingCupItem extends Item {
    public MixingCupItem(Properties properties) {
        super(properties);
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            Level level = context.getLevel();
            BlockPos clickedPos = context.getClickedPos();
            net.minecraft.core.Direction clickedFace = context.getClickedFace();
            BlockPos placePos = clickedPos.relative(clickedFace);

            net.minecraft.world.item.context.BlockPlaceContext placeContext = new net.minecraft.world.item.context.BlockPlaceContext(context);
            BlockState clickedState = level.getBlockState(clickedPos);
            BlockPos actualPlacePos = clickedPos;
            if (clickedState.canBeReplaced(placeContext)) {
                actualPlacePos = clickedPos;
            } else {
                actualPlacePos = placePos;
                if (!level.getBlockState(placePos).canBeReplaced(placeContext)) {
                    return net.minecraft.world.InteractionResult.FAIL;
                }
            }

            ItemStack stack = context.getItemInHand();
            boolean isOpened = stack.getOrDefault(ModDataComponents.OPENED.get(), false);
            Direction facing = context.getHorizontalDirection().getOpposite();
            BlockState placementState = ModBlocks.MIXING_CUP.get().defaultBlockState()
                    .setValue(com.lyuurain.teaparty.block.MixingCupBlock.FACING, facing)
                    .setValue(com.lyuurain.teaparty.block.MixingCupBlock.OPENED, isOpened);

            net.minecraft.world.phys.shapes.CollisionContext collisionContext = net.minecraft.world.phys.shapes.CollisionContext.of(player);
            if (!level.isInWorldBounds(actualPlacePos) || !level.isUnobstructed(placementState, actualPlacePos, collisionContext)) {
                return net.minecraft.world.InteractionResult.FAIL;
            }

            if (!level.isClientSide) {
                level.setBlock(actualPlacePos, placementState, 3);
                net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(actualPlacePos);
                if (be instanceof com.lyuurain.teaparty.block.entity.MixingCupBlockEntity cupBe) {
                    cupBe.setOpened(isOpened);
                    if (stack.has(ModDataComponents.PROCESSES.get())) {
                        cupBe.setProcesses(stack.get(ModDataComponents.PROCESSES.get()));
                    }
                    if (stack.has(ModDataComponents.OUTPUT.get())) {
                        cupBe.setOutput(stack.get(ModDataComponents.OUTPUT.get()));
                    }
                }

                level.playSound(null, actualPlacePos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (!player.hasInfiniteMaterials()) {
                    stack.shrink(1);
                }
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (context.getHand() != InteractionHand.MAIN_HAND) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        InteractionResultHolder<ItemStack> holder = this.use(context.getLevel(), player, context.getHand());
        return holder.getResult();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        boolean opened = stack.getOrDefault(ModDataComponents.OPENED.get(), false);
        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        MixingCupOutput output = stack.get(ModDataComponents.OUTPUT.get());

        if (output != null && output.bottles() > 0) {
            if (!opened) {
                if (player.isShiftKeyDown() && offhandStack.isEmpty()) {
                    if (!level.isClientSide) {
                        stack.set(ModDataComponents.OPENED.get(), true);
                        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.MIXING_CUP_CAP.get()));
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
            } else {
                if (offhandStack.is(ModItems.MIXING_CUP_CAP.get())) {
                    if (!level.isClientSide) {
                        stack.remove(ModDataComponents.OPENED.get());
                        offhandStack.shrink(1);
                        player.setItemInHand(InteractionHand.OFF_HAND, offhandStack);
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }

                if (offhandStack.isEmpty()) {
                    retrieveOutput(stack, level, player);
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
            }
            return InteractionResultHolder.pass(stack);
        }

        if (!opened) {
            if (player.isShiftKeyDown() && offhandStack.isEmpty()) {
                if (!level.isClientSide) {
                    stack.set(ModDataComponents.OPENED.get(), true);
                    player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.MIXING_CUP_CAP.get()));
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        } else {
            if (offhandStack.is(ModItems.MIXING_CUP_CAP.get())) {
                if (!level.isClientSide) {
                    stack.remove(ModDataComponents.OPENED.get());
                    offhandStack.shrink(1);
                    player.setItemInHand(InteractionHand.OFF_HAND, offhandStack);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        if (!opened) {
            if (!player.isShiftKeyDown()) {
                List<MixingCupProcess> processes = getProcessesList(stack);
                MixingCupProcess activeProcess = processes.get(processes.size() - 1);
                if (activeProcess.isEmpty()) {
                    return InteractionResultHolder.pass(stack);
                }
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.pass(stack);
        }

        if (offhandStack.is(ModItems.STIRRER.get())) {
            List<MixingCupProcess> processes = getProcessesList(stack);
            MixingCupProcess activeProcess = processes.get(processes.size() - 1);
            if (activeProcess.isEmpty()) {
                return InteractionResultHolder.pass(stack);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        List<MixingCupProcess> processes = getProcessesList(stack);

        if (!offhandStack.isEmpty() && processes.size() == 1) {
            MixingCupProcess process = processes.get(0);
            ResourceLocation singleLiquidId = getSingleLiquidId(process);
            if (singleLiquidId != null) {
                LiquidDefinition liquidDef = LiquidManager.INSTANCE.getLiquids().get(singleLiquidId);
                if (liquidDef != null) {
                    LiquidDefinition.ItemConversion matchingConv = getConversionForContainer(liquidDef, offhandStack);
                    if (matchingConv != null) {
                        int currentBottles = 0;
                        int liquidIndex = -1;
                        for (int i = 0; i < process.liquids().size(); i++) {
                            MixingCupProcess.LiquidStack ls = process.liquids().get(i);
                            if (ls.liquid().equals(singleLiquidId)) {
                                currentBottles = ls.bottles();
                                liquidIndex = i;
                                break;
                            }
                        }
                        if (currentBottles >= matchingConv.bottles()) {
                            if (!level.isClientSide) {
                                List<MixingCupProcess.LiquidStack> activeLiquids = new ArrayList<>(process.liquids());
                                if (currentBottles == matchingConv.bottles()) {
                                    activeLiquids.remove(liquidIndex);
                                } else {
                                    activeLiquids.set(liquidIndex, new MixingCupProcess.LiquidStack(singleLiquidId, currentBottles - matchingConv.bottles()));
                                }
                                processes.set(0, new MixingCupProcess(process.items(), activeLiquids, process.action()));
                                stack.set(ModDataComponents.PROCESSES.get(), processes);

                                boolean isBucket = BuiltInRegistries.ITEM.getKey(offhandStack.getItem()).getPath().contains("bucket");
                                net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_EMPTY;
                                level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);

                                Item filledItem = BuiltInRegistries.ITEM.get(matchingConv.item());
                                ItemStack filledStack = new ItemStack(filledItem);

                                if (!player.hasInfiniteMaterials()) {
                                    if (offhandStack.getCount() == 1) {
                                        player.setItemInHand(InteractionHand.OFF_HAND, filledStack);
                                    } else {
                                        offhandStack.shrink(1);
                                        giveOrDropItem(player, filledStack);
                                    }
                                } else {
                                    giveOrDropItem(player, filledStack);
                                }
                            }
                            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                        }
                    }
                }
            }
        }

        if (offhandStack.isEmpty()) {
            if (processes.size() >= 2) {
                if (!level.isClientSide) {
                    MixingCupRecipe matched = RecipeManager.findMatchingRecipe(processes);
                    MixingCupOutput newOutput;
                    if (matched != null) {
                        Item resultItem = BuiltInRegistries.ITEM.get(matched.result());
                        newOutput = new MixingCupOutput(new ItemStack(resultItem), matched.bottles());
                    } else {
                        newOutput = new MixingCupOutput(new ItemStack(ModItems.STRANGE_DRINK.get()), 1);
                    }
                    stack.set(ModDataComponents.OUTPUT.get(), newOutput);
                    retrieveOutput(stack, level, player);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            } else if (player.isShiftKeyDown()) {
                MixingCupProcess process = processes.get(0);
                boolean hasLiquids = false;
                for (MixingCupProcess.LiquidStack ls : process.liquids()) {
                    if (ls.bottles() > 0) {
                        hasLiquids = true;
                        break;
                    }
                }
                if (!hasLiquids && !process.items().isEmpty()) {
                    if (!level.isClientSide) {
                        List<ItemStack> activeItems = new ArrayList<>(process.items());
                        ItemStack lastItem = activeItems.remove(activeItems.size() - 1);
                        processes.set(0, new MixingCupProcess(activeItems, process.liquids(), process.action()));
                        stack.set(ModDataComponents.PROCESSES.get(), processes);

                        player.setItemInHand(InteractionHand.OFF_HAND, lastItem);
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
            }
        }

        if (!offhandStack.isEmpty()) {
            if (offhandStack.is(ModItems.MIXING_CUP_CAP.get()) ||
                offhandStack.is(ModItems.STIRRER.get()) ||
                offhandStack.is(ModItems.MIXING_CUP.get()) ||
                isEmptyContainer(offhandStack)) {
                return InteractionResultHolder.pass(stack);
            }

            LiquidDefinition.ItemConversion conv = LiquidManager.getConversion(offhandStack);
            LiquidDefinition def = LiquidManager.getLiquidFor(offhandStack);
            if (conv != null && def != null) {
                if (!level.isClientSide) {
                    int lastIdx = processes.size() - 1;
                    MixingCupProcess activeProcess = processes.get(lastIdx);
                    List<MixingCupProcess.LiquidStack> activeLiquids = new ArrayList<>(activeProcess.liquids());
                    boolean found = false;
                    for (int i = 0; i < activeLiquids.size(); i++) {
                        MixingCupProcess.LiquidStack ls = activeLiquids.get(i);
                        if (ls.liquid().equals(def.id())) {
                            activeLiquids.set(i, new MixingCupProcess.LiquidStack(def.id(), ls.bottles() + conv.bottles()));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        activeLiquids.add(new MixingCupProcess.LiquidStack(def.id(), conv.bottles()));
                    }

                    processes.set(lastIdx, new MixingCupProcess(activeProcess.items(), activeLiquids, activeProcess.action()));
                    stack.set(ModDataComponents.PROCESSES.get(), processes);

                    boolean isBucket = BuiltInRegistries.ITEM.getKey(offhandStack.getItem()).getPath().contains("bucket");
                    net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_FILL;
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);

                    Item emptyItem = BuiltInRegistries.ITEM.get(conv.container());
                    ItemStack emptyStack = new ItemStack(emptyItem);

                    if (!player.hasInfiniteMaterials()) {
                        if (offhandStack.getCount() == 1) {
                            player.setItemInHand(InteractionHand.OFF_HAND, emptyStack);
                        } else {
                            offhandStack.shrink(1);
                            giveOrDropItem(player, emptyStack);
                        }
                    }
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            } else {
                if (!level.isClientSide) {
                    int lastIdx = processes.size() - 1;
                    MixingCupProcess activeProcess = processes.get(lastIdx);
                    List<ItemStack> activeItems = new ArrayList<>(activeProcess.items());
                    ItemStack ingredient = offhandStack.copy();
                    ingredient.setCount(1);
                    activeItems.add(ingredient);

                    processes.set(lastIdx, new MixingCupProcess(activeItems, activeProcess.liquids(), activeProcess.action()));
                    stack.set(ModDataComponents.PROCESSES.get(), processes);

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);

                    if (!player.hasInfiniteMaterials()) {
                        offhandStack.shrink(1);
                    }
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    private void retrieveOutput(ItemStack stack, Level level, Player player) {
        MixingCupOutput output = stack.get(ModDataComponents.OUTPUT.get());
        if (output == null || output.bottles() <= 0) {
            return;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            int magicBottles = serverPlayer.getData(ModAttachments.MAGIC_BOTTLE);
            if (serverPlayer.hasInfiniteMaterials() || magicBottles > 0) {
                if (!serverPlayer.hasInfiniteMaterials()) {
                    serverPlayer.setData(ModAttachments.MAGIC_BOTTLE, magicBottles - 1);
                    PacketDistributor.sendToPlayer(serverPlayer, new MagicBottleSyncPayload(magicBottles - 1));
                }

                ItemStack result = output.resultItem().copy();
                result.setCount(1);
                giveOrDropItem(serverPlayer, result);

                int newBottles = output.bottles() - 1;
                if (newBottles <= 0) {
                    stack.remove(ModDataComponents.OUTPUT.get());
                    stack.remove(ModDataComponents.PROCESSES.get());
                } else {
                    stack.set(ModDataComponents.OUTPUT.get(), new MixingCupOutput(output.resultItem(), newBottles));
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            } else {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncMagicBottleZeroPayload());
            }
        }
    }

    private static List<MixingCupProcess> getProcessesList(ItemStack stack) {
        List<MixingCupProcess> list = stack.get(ModDataComponents.PROCESSES.get());
        if (list == null || list.isEmpty()) {
            List<MixingCupProcess> newList = new ArrayList<>();
            newList.add(MixingCupProcess.empty());
            return newList;
        }
        return new ArrayList<>(list);
    }

    private static ResourceLocation getSingleLiquidId(MixingCupProcess process) {
        ResourceLocation singleId = null;
        for (MixingCupProcess.LiquidStack ls : process.liquids()) {
            if (ls.bottles() > 0) {
                if (singleId != null) {
                    return null;
                }
                singleId = ls.liquid();
            }
        }
        return singleId;
    }

    private static LiquidDefinition.ItemConversion getConversionForContainer(LiquidDefinition def, ItemStack containerStack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(containerStack.getItem());
        for (LiquidDefinition.ItemConversion conv : def.items()) {
            if (conv.container().equals(itemId)) {
                return conv;
            }
        }
        return null;
    }

    private boolean isEmptyContainer(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (LiquidDefinition def : LiquidManager.INSTANCE.getLiquids().values()) {
            for (LiquidDefinition.ItemConversion conv : def.items()) {
                if (conv.container().equals(itemId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void giveOrDropItem(Player player, ItemStack itemStack) {
        if (!player.getInventory().add(itemStack)) {
            player.drop(itemStack, false);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        boolean opened = stack.getOrDefault(ModDataComponents.OPENED.get(), false);
        return opened ? com.lyuurain.teaparty.config.ModConfig.COMMON.stirTime : com.lyuurain.teaparty.config.ModConfig.COMMON.shakeTime;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!level.isClientSide) {
            boolean opened = stack.getOrDefault(ModDataComponents.OPENED.get(), false);
            int elapsed = getUseDuration(stack, entity) - count;
            if (opened) {
                if (elapsed % 4 == 0) {
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F + level.random.nextFloat() * 0.4F);
                }
            } else {
                if (elapsed % 4 == 0) {
                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 0.8F, 1.6F + level.random.nextFloat() * 0.4F);
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            boolean opened = stack.getOrDefault(ModDataComponents.OPENED.get(), false);
            List<MixingCupProcess> processes = getProcessesList(stack);
            int lastIdx = processes.size() - 1;
            MixingCupProcess activeProcess = processes.get(lastIdx);

            MixingCupProcess.ProcessAction nextAction = opened ? MixingCupProcess.ProcessAction.STIR : MixingCupProcess.ProcessAction.SHAKE;
            MixingCupProcess updatedProcess = new MixingCupProcess(activeProcess.items(), activeProcess.liquids(), nextAction);
            processes.set(lastIdx, updatedProcess);
            processes.add(MixingCupProcess.empty());

            stack.set(ModDataComponents.PROCESSES.get(), processes);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        MixingCupOutput output = stack.get(ModDataComponents.OUTPUT.get());
        if (output != null) {
            Component craftableText = Component.translatable("tooltip.gemlike_teaparty.mixing_cup.craftable",
                    output.resultItem().getHoverName(), output.bottles()).withStyle(ChatFormatting.GOLD);
            tooltipComponents.add(craftableText);
        } else {
            List<MixingCupProcess> processes = stack.getOrDefault(ModDataComponents.PROCESSES.get(), List.of());
            if (!processes.isEmpty()) {
                MixingCupRecipe matched = RecipeManager.findMatchingRecipe(processes);
                if (matched != null) {
                    Item resultItem = BuiltInRegistries.ITEM.get(matched.result());
                    if (resultItem != null) {
                        Component craftableText = Component.translatable("tooltip.gemlike_teaparty.mixing_cup.craftable",
                                resultItem.getDescription(), matched.bottles()).withStyle(ChatFormatting.GOLD);
                        tooltipComponents.add(craftableText);
                    }
                }
            }
        }

        List<MixingCupProcess> processes = stack.getOrDefault(ModDataComponents.PROCESSES.get(), List.of());
        if (processes.isEmpty() || (processes.size() == 1 && processes.get(0).isEmpty())) {
            tooltipComponents.add(Component.translatable("tooltip.gemlike_teaparty.mixing_cup.empty").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            for (int i = 0; i < processes.size(); i++) {
                MixingCupProcess process = processes.get(i);
                if (process.isEmpty()) {
                    continue;
                }

                List<Component> ingredientsList = new ArrayList<>();
                for (ItemStack item : process.items()) {
                    ingredientsList.add(item.getHoverName());
                }
                for (MixingCupProcess.LiquidStack liquid : process.liquids()) {
                    if (liquid.bottles() > 0) {
                        LiquidDefinition liquidDef = LiquidManager.INSTANCE.getLiquids().get(liquid.liquid());
                        Component liquidName = liquidDef != null ?
                                Component.translatable(liquidDef.name()) :
                                Component.literal(liquid.liquid().getPath());
                        ingredientsList.add(Component.translatable("tooltip.gemlike_teaparty.mixing_cup.liquid_entry",
                                liquidName, liquid.bottles()));
                    }
                }

                if (ingredientsList.isEmpty() && process.action() == MixingCupProcess.ProcessAction.NONE) {
                    continue;
                }

                MutableComponent ingredientsComp = Component.literal("");
                for (int j = 0; j < ingredientsList.size(); j++) {
                    ingredientsComp.append(ingredientsList.get(j));
                    if (j < ingredientsList.size() - 1) {
                        ingredientsComp.append(Component.literal(", "));
                    }
                }

                Component line;
                if (process.action() != MixingCupProcess.ProcessAction.NONE) {
                    String actionKey = "tooltip.gemlike_teaparty.mixing_cup.action." + process.action().getName();
                    Component actionComp = Component.translatable(actionKey).withStyle(ChatFormatting.YELLOW);
                    line = Component.translatable("tooltip.gemlike_teaparty.mixing_cup.step_with_action",
                            ingredientsComp, actionComp).withStyle(ChatFormatting.GRAY);
                } else {
                    line = Component.translatable("tooltip.gemlike_teaparty.mixing_cup.step_no_action",
                            ingredientsComp).withStyle(ChatFormatting.GRAY);
                }
                tooltipComponents.add(line);
            }
        }
    }
}
