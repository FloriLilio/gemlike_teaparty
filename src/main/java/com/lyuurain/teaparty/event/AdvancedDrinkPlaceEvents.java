package com.lyuurain.teaparty.event;

import com.lyuurain.teaparty.block.AdvancedDrinkBlock;
import com.lyuurain.teaparty.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class AdvancedDrinkPlaceEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.is(ModTags.Items.ADVANCED_DRINKS)) {
            return;
        }

        Direction clickedFace = event.getFace();
        if (clickedFace == null) {
            return;
        }

        Level level = event.getLevel();
        Block block = BuiltInRegistries.BLOCK.get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        if (!(block instanceof AdvancedDrinkBlock advancedDrinkBlock)) {
            return;
        }

        BlockPos clickedPos = event.getPos();
        BlockHitResult hitResult = event.getHitVec();
        BlockPlaceContext placeContext = new BlockPlaceContext(new UseOnContext(player, event.getHand(), hitResult));
        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos actualPlacePos = clickedPos;
        if (!clickedState.canBeReplaced(placeContext)) {
            actualPlacePos = clickedPos.relative(clickedFace);
            if (!level.getBlockState(actualPlacePos).canBeReplaced(placeContext)) {
                cancel(event, InteractionResult.FAIL);
                return;
            }
        }

        BlockState placementState = advancedDrinkBlock.defaultBlockState()
                .setValue(AdvancedDrinkBlock.FACING, placeContext.getHorizontalDirection().getOpposite());
        CollisionContext collisionContext = CollisionContext.of(player);
        if (!level.isInWorldBounds(actualPlacePos) || !level.isUnobstructed(placementState, actualPlacePos, collisionContext)) {
            cancel(event, InteractionResult.FAIL);
            return;
        }

        if (!level.isClientSide) {
            level.setBlock(actualPlacePos, placementState, 3);
            level.playSound(null, actualPlacePos, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }
        cancel(event, InteractionResult.sidedSuccess(level.isClientSide));
    }

    private static void cancel(PlayerInteractEvent.RightClickBlock event, InteractionResult result) {
        event.setCancellationResult(result);
        event.setCanceled(true);
    }
}
