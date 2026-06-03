package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class BlenderEvents {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.BLENDER_LIGHT.get()) && !state.is(ModBlocks.BLENDER_DARK.get())) {
            return;
        }

        DoubleBlockHalf half = state.getValue(com.lyuurain.teaparty.block.BlenderBlock.HALF);
        BlockPos lowerPos = (half == DoubleBlockHalf.LOWER) ? pos : pos.below();
        BlockState lowerState = level.getBlockState(lowerPos);
        if (!lowerState.is(ModBlocks.BLENDER_LIGHT.get()) && !lowerState.is(ModBlocks.BLENDER_DARK.get())) {
            return;
        }

        double clickY = event.getHitVec().getLocation().y - lowerPos.getY();
        if (clickY < 0.8125D) {
            return;
        }

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        BlockEntity be = level.getBlockEntity(lowerPos);
        if (be instanceof BlenderBlockEntity blender) {
            if (lowerState.getValue(BlenderBlock.POWERED)) {
                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }

            boolean isSneaking = player.isShiftKeyDown();

            if (!stack.isEmpty()) {
                LiquidDefinition.ItemConversion conv = LiquidManager.getConversion(stack);
                LiquidDefinition def = LiquidManager.getLiquidFor(stack);
                if (conv != null && def != null) {
                    boolean canAdd = blender.getLiquidId() == null || blender.getLiquidCount() == 0 ||
                            (blender.getLiquidId().equals(def.id()) && blender.getLiquidCount() + conv.bottles() <= 6);
                    if (canAdd) {
                        if (!level.isClientSide) {
                            blender.setLiquid(def.id(), blender.getLiquidCount() + conv.bottles());
                            boolean isBucket = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("bucket");
                            net.minecraft.sounds.SoundEvent sound = isBucket ? net.minecraft.sounds.SoundEvents.BUCKET_EMPTY : net.minecraft.sounds.SoundEvents.BOTTLE_FILL;
                            level.playSound(null, lowerPos, sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                            if (!player.hasInfiniteMaterials()) {
                                net.minecraft.world.item.Item emptyItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(conv.container());
                                ItemStack emptyStack = new ItemStack(emptyItem);
                                stack.shrink(1);
                                if (stack.isEmpty()) {
                                    player.setItemInHand(hand, emptyStack);
                                } else {
                                    if (!player.getInventory().add(emptyStack)) {
                                        player.drop(emptyStack, false);
                                    }
                                }
                            }
                        }
                        event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide));
                        event.setCanceled(true);
                        return;
                    }
                }

                if (blender.getLiquidCount() > 0 && blender.getLiquidId() != null) {
                    LiquidDefinition currentDef = LiquidManager.INSTANCE.getLiquids().get(blender.getLiquidId());
                    if (currentDef != null) {
                        for (LiquidDefinition.ItemConversion conversion : currentDef.items()) {
                            net.minecraft.resources.ResourceLocation heldItemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
                            if (conversion.container().equals(heldItemId)) {
                                if (blender.getLiquidCount() >= conversion.bottles()) {
                                    if (!level.isClientSide) {
                                        blender.setLiquid(blender.getLiquidId(), blender.getLiquidCount() - conversion.bottles());
                                        boolean isBucket = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("bucket");
                                        net.minecraft.sounds.SoundEvent sound = isBucket ? net.minecraft.sounds.SoundEvents.BUCKET_FILL : net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY;
                                        level.playSound(null, lowerPos, sound, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);

                                        net.minecraft.world.item.Item filledItem = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(conversion.item());
                                        ItemStack filledStack = new ItemStack(filledItem);
                                        if (filledItem == net.minecraft.world.item.Items.POTION && blender.getLiquidId() != null && "gemlike_teaparty:water".equals(blender.getLiquidId().toString())) {
                                            filledStack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new net.minecraft.world.item.alchemy.PotionContents(net.minecraft.world.item.alchemy.Potions.WATER));
                                        }

                                        if (!player.hasInfiniteMaterials()) {
                                            stack.shrink(1);
                                            if (stack.isEmpty()) {
                                                player.setItemInHand(hand, filledStack);
                                            } else {
                                                if (!player.getInventory().add(filledStack)) {
                                                    player.drop(filledStack, false);
                                                }
                                            }
                                        } else {
                                            if (!player.getInventory().add(filledStack)) {
                                                player.drop(filledStack, false);
                                            }
                                        }
                                    }
                                    event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide));
                                    event.setCanceled(true);
                                    return;
                                }
                            }
                        }
                    }
                }

                if (blender.insertItem(stack)) {
                    if (!level.isClientSide) {
                        level.playSound(null, lowerPos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_ADD_ITEM, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide));
                    event.setCanceled(true);
                    return;
                }
            } else {
                if (!blender.isEmpty()) {
                    if (!level.isClientSide) {
                        ItemStack extracted = blender.extractItem();
                        if (!extracted.isEmpty()) {
                            if (!player.getInventory().add(extracted)) {
                                player.drop(extracted, false);
                            }
                            level.playSound(null, lowerPos, net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                    event.setCancellationResult(net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide));
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
