package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.block.entity.MixingCupBlockEntity;
import com.lyuurain.teaparty.registry.ModDataComponents;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public class MixingCupBlock extends BaseEntityBlock {
    public static final MapCodec<MixingCupBlock> CODEC = simpleCodec(MixingCupBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    public static final BooleanProperty HAS_STIRRER = BooleanProperty.create("has_stirrer");
    private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D);

    public MixingCupBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPENED, false).setValue(HAS_STIRRER, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED, HAS_STIRRER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MixingCupBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModItems.STIRRER.get())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MixingCupBlockEntity cupBe) {
                if (!cupBe.isHasStirrer()) {
                    if (!level.isClientSide) {
                        cupBe.setHasStirrer(true);
                        level.setBlock(pos, state.setValue(HAS_STIRRER, true), 3);
                        if (!player.hasInfiniteMaterials()) {
                            stack.shrink(1);
                        }
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MixingCupBlockEntity cupBe) {
            if (cupBe.isHasStirrer()) {
                if (!level.isClientSide) {
                    cupBe.setHasStirrer(false);
                    level.setBlock(pos, state.setValue(HAS_STIRRER, false), 3);
                    ItemStack stirrerStack = new ItemStack(ModItems.STIRRER.get());
                    if (!player.getInventory().add(stirrerStack)) {
                        player.drop(stirrerStack, false);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        if (!level.isClientSide) {
            if (be instanceof MixingCupBlockEntity cupBe2) {
                ItemStack stack = new ItemStack(ModItems.MIXING_CUP.get());
                stack.set(ModDataComponents.OPENED.get(), cupBe2.isOpened());
                if (cupBe2.getProcesses() != null) {
                    stack.set(ModDataComponents.PROCESSES.get(), cupBe2.getProcesses());
                }
                if (cupBe2.getOutput() != null) {
                    stack.set(ModDataComponents.OUTPUT.get(), cupBe2.getOutput());
                }

                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }

                cupBe2.setPickedUp(true);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.removeBlock(pos, false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MixingCupBlockEntity cupBe) {
            if (player.isCreative()) {
                cupBe.setPickedUp(true);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MixingCupBlockEntity cupBe) {
                if (!cupBe.isPickedUp() && !level.isClientSide) {
                    ItemStack stack = new ItemStack(ModItems.MIXING_CUP.get());
                    stack.set(ModDataComponents.OPENED.get(), cupBe.isOpened());
                    if (cupBe.getProcesses() != null) {
                        stack.set(ModDataComponents.PROCESSES.get(), cupBe.getProcesses());
                    }
                    if (cupBe.getOutput() != null) {
                        stack.set(ModDataComponents.OUTPUT.get(), cupBe.getOutput());
                    }
                    popResource(level, pos, stack);
                    if (cupBe.isHasStirrer()) {
                        popResource(level, pos, new ItemStack(ModItems.STIRRER.get()));
                    }
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
