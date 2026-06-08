package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class RedGrapeVineBlock extends Block implements BonemealableBlock {
    public static final MapCodec<RedGrapeVineBlock> CODEC = simpleCodec(RedGrapeVineBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final EnumProperty<Form> FORM = EnumProperty.create("form", Form.class);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty TRIMMED = BooleanProperty.create("trimmed");
    private static final int MAX_HANGING_LENGTH = 6;
    private static final int MAX_WALL_VINES_NEARBY = 8;
    private static final VoxelShape HANGING_STAGE0_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    private static final VoxelShape HANGING_STAGE1_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 12.0, 13.0);
    private static final VoxelShape HANGING_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape WALL_NORTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
    private static final VoxelShape WALL_SOUTH_SHAPE = Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
    private static final VoxelShape WALL_WEST_SHAPE = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
    private static final VoxelShape WALL_EAST_SHAPE = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public RedGrapeVineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(FORM, Form.HANGING)
                .setValue(FACING, Direction.NORTH)
                .setValue(TRIMMED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        BlockState state;
        if (direction == Direction.DOWN) {
            state = this.defaultBlockState().setValue(FORM, Form.HANGING);
        } else if (direction.getAxis().isHorizontal()) {
            state = this.defaultBlockState().setValue(FORM, Form.WALL).setValue(FACING, direction);
        } else {
            return null;
        }
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.RED_GRAPE_SEEDS.get());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(FORM) == Form.HANGING) {
            return switch (state.getValue(AGE)) {
                case 0 -> HANGING_STAGE0_SHAPE;
                case 1 -> HANGING_STAGE1_SHAPE;
                default -> HANGING_SHAPE;
            };
        }
        return switch (state.getValue(FACING)) {
            case NORTH -> WALL_SOUTH_SHAPE;
            case SOUTH -> WALL_NORTH_SHAPE;
            case WEST -> WALL_EAST_SHAPE;
            default -> WALL_WEST_SHAPE;
        };
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(FORM) == Form.HANGING) {
            BlockState aboveState = level.getBlockState(pos.above());
            return aboveState.is(this) && aboveState.getValue(FORM) == Form.HANGING
                    || Block.canSupportCenter(level, pos.above(), Direction.DOWN);
        }
        Direction facing = state.getValue(FACING);
        return Block.canSupportCenter(level, pos.relative(facing.getOpposite()), facing);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.canSurvive(level, pos) ? super.updateShape(state, direction, neighborState, level, pos, neighborPos) : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3 || !state.getValue(TRIMMED);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3) {
            if (level.getRawBrightness(pos, 0) >= 9 && CommonHooks.canCropGrow(level, pos, state, random.nextInt(5) == 0)) {
                BlockState newState = state.setValue(AGE, age + 1);
                level.setBlock(pos, newState, 2);
                CommonHooks.fireCropGrowPost(level, pos, state);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
            }
            return;
        }
        if (!state.getValue(TRIMMED) && CommonHooks.canCropGrow(level, pos, state, random.nextInt(8) == 0)) {
            tryExtend(state, level, pos, random);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(TRIMMED) && stack.canPerformAction(ItemAbilities.SHEARS_TRIM)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (state.getValue(AGE) < 3 && stack.is(Items.BONE_MEAL)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (state.getValue(AGE) == 3) {
            harvest(state, level, pos, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(AGE) == 3) {
            harvest(state, level, pos, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, FORM, FACING, TRIMMED);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(AGE, Math.min(3, state.getValue(AGE) + 1)), 2);
    }

    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.SHEARS_TRIM && !state.getValue(TRIMMED)) {
            return state.setValue(TRIMMED, true);
        }
        return null;
    }

    private void harvest(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            popResource(level, pos, new ItemStack(ModItems.RED_GRAPE.get(), 2 + level.random.nextInt(2)));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState newState = state.setValue(AGE, 1);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
        }
    }

    private void tryExtend(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(FORM) == Form.HANGING) {
            extendHanging(state, level, pos);
        } else {
            extendWall(state, level, pos, random);
        }
    }

    private void extendHanging(BlockState state, ServerLevel level, BlockPos pos) {
        if (getHangingLength(level, pos) >= MAX_HANGING_LENGTH) {
            return;
        }
        BlockPos targetPos = pos.below();
        BlockState newState = state.setValue(AGE, 0).setValue(TRIMMED, false);
        if (canGrowInto(level, targetPos, newState)) {
            BlockState oldState = level.getBlockState(targetPos);
            level.setBlock(targetPos, newState, 2);
            CommonHooks.fireCropGrowPost(level, targetPos, oldState);
            level.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(newState));
        }
    }

    private void extendWall(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!hasRoomForWallGrowth(level, pos)) {
            return;
        }
        Direction facing = state.getValue(FACING);
        Direction[] directions = {Direction.UP, Direction.DOWN, facing.getClockWise(), facing.getCounterClockWise()};
        int start = random.nextInt(directions.length);
        for (int i = 0; i < directions.length; i++) {
            BlockPos targetPos = pos.relative(directions[(start + i) % directions.length]);
            BlockState newState = state.setValue(AGE, 0).setValue(TRIMMED, false);
            if (canGrowInto(level, targetPos, newState)) {
                BlockState oldState = level.getBlockState(targetPos);
                level.setBlock(targetPos, newState, 2);
                CommonHooks.fireCropGrowPost(level, targetPos, oldState);
                level.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(newState));
                return;
            }
        }
    }

    private boolean canGrowInto(ServerLevel level, BlockPos pos, BlockState state) {
        BlockState existingState = level.getBlockState(pos);
        return (existingState.isAir() || existingState.canBeReplaced()) && state.canSurvive(level, pos);
    }

    private int getHangingLength(LevelReader level, BlockPos pos) {
        int length = 1;
        BlockPos currentPos = pos.above();
        while (length < MAX_HANGING_LENGTH) {
            BlockState state = level.getBlockState(currentPos);
            if (!state.is(this) || state.getValue(FORM) != Form.HANGING) {
                break;
            }
            length++;
            currentPos = currentPos.above();
        }
        return length;
    }

    private boolean hasRoomForWallGrowth(LevelReader level, BlockPos pos) {
        int count = 0;
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockState state = level.getBlockState(pos.offset(x, y, z));
                    if (state.is(this) && state.getValue(FORM) == Form.WALL && ++count > MAX_WALL_VINES_NEARBY) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public enum Form implements StringRepresentable {
        HANGING("hanging"),
        WALL("wall");

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
