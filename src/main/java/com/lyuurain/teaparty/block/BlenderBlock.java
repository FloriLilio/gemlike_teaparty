package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.lyuurain.teaparty.registry.ModItems;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.Containers;
import org.jetbrains.annotations.Nullable;

public class BlenderBlock extends BaseEntityBlock {
    public static final MapCodec<BlenderBlock> CODEC = simpleCodec(BlenderBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape LOWER_BASE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    private static final VoxelShape LOWER_TANK_BOTTOM = Block.box(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape LOWER_SHAPE = Shapes.or(LOWER_BASE, LOWER_TANK_BOTTOM);
    private static final VoxelShape UPPER_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public BlenderBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(POWERED, false).setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, POWERED, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(POWERED, level.hasNeighborSignal(pos)).setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, state.getValue(FACING)), 3);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(HALF) != half) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.asItem());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos lowerPos = (half == DoubleBlockHalf.LOWER) ? pos : pos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this)) {
                boolean hasPower = level.hasNeighborSignal(lowerPos);
                if (lowerState.getValue(POWERED) != hasPower) {
                    level.setBlock(lowerPos, lowerState.setValue(POWERED, hasPower), 3);
                }
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this)) {
                return this.useItemOn(stack, lowerState, level, lowerPos, player, hand, hitResult);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (hitResult.getLocation().y - pos.getY() < 0.8125D) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (be instanceof BlenderBlockEntity blender) {
            if (lowerStateHasPowered(state, level, pos)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!stack.isEmpty()) {
                LiquidDefinition.ItemConversion conv = LiquidManager.getConversion(stack);
                LiquidDefinition def = LiquidManager.getLiquidFor(stack);
                if (conv != null && def != null) {
                    boolean canAdd = blender.getLiquidId() == null || blender.getLiquidCount() == 0 ||
                            (blender.getLiquidId().equals(def.id()) && blender.getLiquidCount() + conv.bottles() <= 6);
                    if (canAdd) {
                        if (!level.isClientSide) {
                            blender.setLiquid(def.id(), blender.getLiquidCount() + conv.bottles());
                            boolean isBucket = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("bucket");
                            net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_FILL;
                            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

                            if (!player.hasInfiniteMaterials()) {
                                Item emptyItem = BuiltInRegistries.ITEM.get(conv.container());
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
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                }

                if (blender.getLiquidCount() > 0 && blender.getLiquidId() != null) {
                    LiquidDefinition currentDef = LiquidManager.INSTANCE.getLiquids().get(blender.getLiquidId());
                    if (currentDef != null) {
                        for (LiquidDefinition.ItemConversion conversion : currentDef.items()) {
                            net.minecraft.resources.ResourceLocation heldItemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                            if (conversion.container().equals(heldItemId)) {
                                if (blender.getLiquidCount() >= conversion.bottles()) {
                                    if (!level.isClientSide) {
                                        blender.setLiquid(blender.getLiquidId(), blender.getLiquidCount() - conversion.bottles());
                                        boolean isBucket = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("bucket");
                                        net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_EMPTY;
                                        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

                                        Item filledItem = BuiltInRegistries.ITEM.get(conversion.item());
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
                                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                                }
                            }
                        }
                    }
                }

                // If not a liquid interaction, insert item
                if (blender.insertItem(stack)) {
                    if (!level.isClientSide) {
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean lowerStateHasPowered(BlockState state, Level level, BlockPos pos) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos lowerPos = (half == DoubleBlockHalf.LOWER) ? pos : pos.below();
        BlockState lowerState = level.getBlockState(lowerPos);
        return lowerState.is(this) && lowerState.hasProperty(POWERED) && lowerState.getValue(POWERED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pos.below();
            BlockState lowerState = level.getBlockState(lowerPos);
            if (lowerState.is(this)) {
                return this.useWithoutItem(lowerState, level, lowerPos, player, hitResult);
            }
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (hitResult.getLocation().y - pos.getY() < 0.8125D) {
            return InteractionResult.PASS;
        }
        if (be instanceof BlenderBlockEntity blender) {
            if (lowerStateHasPowered(state, level, pos)) {
                return InteractionResult.PASS;
            }

            if (!blender.isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack extracted = blender.extractItem();
                    if (!extracted.isEmpty()) {
                        if (!player.getInventory().add(extracted)) {
                            player.drop(extracted, false);
                        }
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlenderBlockEntity blender) {
                Containers.dropContents(level, pos, blender.getItems());
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new BlenderBlockEntity(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return createTickerHelper(type, ModBlockEntities.BLENDER_BE.get(), BlenderBlockEntity::clientTick);
        }
        return null;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? Shapes.block() : Shapes.empty();
    }
}
