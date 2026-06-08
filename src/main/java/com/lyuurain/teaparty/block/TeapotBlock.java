package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.block.entity.TeapotBlockEntity;
import com.lyuurain.teaparty.recipe.LiquidDefinition;
import com.lyuurain.teaparty.recipe.LiquidManager;
import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.lyuurain.teaparty.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TeapotBlock extends BaseEntityBlock {
    public static final MapCodec<TeapotBlock> CODEC = simpleCodec(TeapotBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    public static final EnumProperty<Form> FORM = EnumProperty.create("form", Form.class);
    public static final IntegerProperty ACTIVE = IntegerProperty.create("active", 0, 3);
    private static final int CAPACITY = 4;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public TeapotBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENED, false)
                .setValue(FORM, Form.NORMAL)
                .setValue(ACTIVE, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED, FORM, ACTIVE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Form form = context.getClickedFace() == Direction.DOWN ? Form.CHAIN : getRestingForm(context.getLevel(), pos);
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(FORM, form);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Form form = state.getValue(FORM);
        Form newForm = form == Form.CHAIN && canHang(level, pos) ? Form.CHAIN : getRestingForm(level, pos);
        return state.setValue(FORM, newForm);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TeapotBlockEntity teapot) {
            if (!stack.isEmpty()) {
                ItemInteractionResult result = tryExtractLiquid(stack, teapot, level, pos, player, hand);
                if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
                    return result;
                }

                if (state.getValue(OPENED) && teapot.canModifyContents()) {
                    result = tryAddLiquid(stack, teapot, level, pos, player, hand);
                    if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
                        return result;
                    }

                    if (teapot.insertItem(stack)) {
                        if (!level.isClientSide) {
                            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemInteractionResult tryAddLiquid(ItemStack stack, TeapotBlockEntity teapot, Level level, BlockPos pos, Player player, InteractionHand hand) {
        LiquidDefinition.ItemConversion conv = LiquidManager.getConversion(stack);
        LiquidDefinition def = LiquidManager.getLiquidFor(stack);
        if (conv == null || def == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        boolean canAdd = teapot.getLiquidId() == null || teapot.getLiquidCount() == 0 ||
                (teapot.getLiquidId().equals(def.id()) && teapot.getLiquidCount() + conv.bottles() <= CAPACITY);
        if (!canAdd) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide) {
            teapot.setLiquid(def.id(), teapot.getLiquidCount() + conv.bottles());
            boolean isBucket = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("bucket");
            net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_EMPTY : SoundEvents.BOTTLE_FILL;
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!player.hasInfiniteMaterials()) {
                Item emptyItem = BuiltInRegistries.ITEM.get(conv.container());
                ItemStack emptyStack = new ItemStack(emptyItem);
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, emptyStack);
                } else if (!player.getInventory().add(emptyStack)) {
                    player.drop(emptyStack, false);
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private ItemInteractionResult tryExtractLiquid(ItemStack stack, TeapotBlockEntity teapot, Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (teapot.getLiquidId() == null || teapot.getLiquidCount() <= 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        LiquidDefinition currentDef = LiquidManager.INSTANCE.getLiquids().get(teapot.getLiquidId());
        if (currentDef == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        for (LiquidDefinition.ItemConversion conversion : currentDef.items()) {
            net.minecraft.resources.ResourceLocation heldItemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (conversion.container().equals(heldItemId) && teapot.getLiquidCount() >= conversion.bottles()) {
                if (!level.isClientSide) {
                    net.minecraft.resources.ResourceLocation liquidId = teapot.getLiquidId();
                    int newCount = teapot.getLiquidCount() - conversion.bottles();
                    teapot.setLiquid(newCount > 0 ? liquidId : null, newCount);
                    boolean isBucket = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("bucket");
                    net.minecraft.sounds.SoundEvent sound = isBucket ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_EMPTY;
                    level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);

                    Item filledItem = BuiltInRegistries.ITEM.get(conversion.item());
                    ItemStack filledStack = new ItemStack(filledItem);
                    if (filledItem == Items.POTION && liquidId != null && "minecraft:water".equals(liquidId.toString())) {
                        filledStack.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                                new net.minecraft.world.item.alchemy.PotionContents(net.minecraft.world.item.alchemy.Potions.WATER));
                    }

                    if (!player.hasInfiniteMaterials()) {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                            player.setItemInHand(hand, filledStack);
                        } else if (!player.getInventory().add(filledStack)) {
                            player.drop(filledStack, false);
                        }
                    } else if (!player.getInventory().add(filledStack)) {
                        player.drop(filledStack, false);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TeapotBlockEntity teapot && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                ItemStack extracted = teapot.extractItem();
                if (!extracted.isEmpty()) {
                    if (!player.getInventory().add(extracted)) {
                        player.drop(extracted, false);
                    }
                    if (TeapotBlockEntity.isHeated(level, pos)) {
                        player.hurt(player.damageSources().magic(), 1.0F);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            boolean opened = !state.getValue(OPENED);
            BlockState newState = state.setValue(OPENED, opened).setValue(ACTIVE, 0);
            level.setBlock(pos, newState, 3);
            level.playSound(null, pos, opened ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TeapotBlockEntity teapot) {
                Containers.dropContents(level, pos, teapot.getItems());
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeapotBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.TEAPOT_BE.get(), TeapotBlockEntity::serverTick);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.TEAPOT.get());
    }

    private static boolean canHang(LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.above(), Direction.DOWN);
    }

    private static Form getRestingForm(LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.isAir() && !Block.canSupportCenter(level, pos.below(), Direction.UP)) {
            return Form.BASE;
        }
        return Form.NORMAL;
    }

    public enum Form implements StringRepresentable {
        NORMAL("normal"),
        CHAIN("chain"),
        BASE("base");

        private final String name;

        Form(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
