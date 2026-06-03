package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.block.entity.MixingCupBlockEntity;
import com.lyuurain.teaparty.registry.ModDataComponents;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public class MixingCupBlock extends BaseEntityBlock {
    public static final MapCodec<MixingCupBlock> CODEC = simpleCodec(MixingCupBlock::new);
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 8.0D, 11.0D);

    public MixingCupBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OPENED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPENED);
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MixingCupBlockEntity cupBe) {
                ItemStack stack = new ItemStack(ModItems.MIXING_CUP.get());
                stack.set(ModDataComponents.OPENED.get(), cupBe.isOpened());
                if (cupBe.getProcesses() != null) {
                    stack.set(ModDataComponents.PROCESSES.get(), cupBe.getProcesses());
                }
                if (cupBe.getOutput() != null) {
                    stack.set(ModDataComponents.OUTPUT.get(), cupBe.getOutput());
                }

                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }

                cupBe.setPickedUp(true);
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
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
