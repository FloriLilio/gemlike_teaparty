package com.lyuurain.teaparty.item;

import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlueberryItem extends ItemNameBlockItem {
    public BlueberryItem(Properties properties) {
        super(ModBlocks.BLUE_BERRY_BUSH.get(), properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);

        if (blockstate.is(ModBlocks.BLUE_BERRY_BUSH.get())) {
            return InteractionResult.PASS;
        }

        return super.useOn(context);
    }
}
