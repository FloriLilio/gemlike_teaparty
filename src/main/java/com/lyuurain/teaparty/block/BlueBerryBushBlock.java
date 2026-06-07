package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlueBerryBushBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<BlueBerryBushBlock> CODEC = simpleCodec(BlueBerryBushBlock::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public BlueBerryBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.BLUEBERRY.get());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(AGE) == 0) {
            return SAPLING_SHAPE;
        }
        return state.getValue(AGE) < 3 ? MID_GROWTH_SHAPE : super.getShape(state, level, pos, context);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && level.getRawBrightness(pos.above(), 0) >= 9 &&
                net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt(5) == 0)) {
            BlockState newState = state.setValue(AGE, age + 1);
            level.setBlock(pos, newState, 2);
            net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && entity.getType() != EntityType.FOX && entity.getType() != EntityType.BEE) {
            entity.makeStuckInBlock(state, new Vec3(0.8F, 0.75, 0.8F));
            if (!level.isClientSide && state.getValue(AGE) > 0 && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double dx = Math.abs(entity.getX() - entity.xOld);
                double dz = Math.abs(entity.getZ() - entity.zOld);
                if (dx >= 0.003F || dz >= 0.003F) {
                    entity.hurt(level.damageSources().sweetBerryBush(), 1.0F);
                }
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        boolean isMature = age == 3;
        if (!isMature && stack.is(Items.BONE_MEAL)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (age > 1) {
            if (!level.isClientSide) {
                int count = 1 + level.random.nextInt(2) + (isMature ? 1 : 0);
                popResource(level, pos, new ItemStack(ModItems.BLUEBERRY.get(), count));
                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                BlockState newState = state.setValue(AGE, 1);
                level.setBlock(pos, newState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        boolean isMature = age == 3;
        if (age > 1) {
            if (!level.isClientSide) {
                int count = 1 + level.random.nextInt(2) + (isMature ? 1 : 0);
                popResource(level, pos, new ItemStack(ModItems.BLUEBERRY.get(), count));
                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                BlockState newState = state.setValue(AGE, 1);
                level.setBlock(pos, newState, 2);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
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
        int age = Math.min(3, state.getValue(AGE) + 1);
        level.setBlock(pos, state.setValue(AGE, age), 2);
    }
}
