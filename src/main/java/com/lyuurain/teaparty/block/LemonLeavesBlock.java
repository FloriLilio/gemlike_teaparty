package com.lyuurain.teaparty.block;

import com.lyuurain.teaparty.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class LemonLeavesBlock extends LeavesBlock {
    public static final MapCodec<LemonLeavesBlock> CODEC = simpleCodec(LemonLeavesBlock::new);
    private static final int LEMON_GROWTH_CHANCE = 20;

    public LemonLeavesBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state) || !state.getValue(WATERLOGGED);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (decaying(state)) {
            super.randomTick(state, level, pos, random);
            return;
        }
        if (state.getValue(WATERLOGGED) || random.nextInt(LEMON_GROWTH_CHANCE) != 0) {
            return;
        }
        BlockPos cropPos = pos.below();
        if (!level.getBlockState(cropPos).isAir() || hasAdjacentLemonCrop(level, cropPos)) {
            return;
        }
        BlockState cropState = ModBlocks.LEMON_CROP.get().defaultBlockState().setValue(LemonCropBlock.AGE, 0);
        if (!cropState.canSurvive(level, cropPos)) {
            return;
        }
        level.setBlock(cropPos, cropState, 2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, cropPos, GameEvent.Context.of(cropState));
    }

    private static boolean hasAdjacentLemonCrop(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(ModBlocks.LEMON_CROP.get())) {
                return true;
            }
        }
        return false;
    }
}
