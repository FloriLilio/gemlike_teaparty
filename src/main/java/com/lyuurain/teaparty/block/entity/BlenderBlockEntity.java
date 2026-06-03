package com.lyuurain.teaparty.block.entity;

import com.lyuurain.teaparty.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlenderBlockEntity extends BlockEntity {
    private ResourceLocation liquidId = null;
    private int liquidCount = 0;

    // Client-side animation height
    private float prevLiquidHeight = 0.0F;
    private float liquidHeight = 0.0F;

    public BlenderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLENDER_BE.get(), pos, state);
    }

    public @Nullable ResourceLocation getLiquidId() {
        return this.liquidId;
    }

    public int getLiquidCount() {
        return this.liquidCount;
    }

    public void setLiquid(@Nullable ResourceLocation id, int count) {
        this.liquidId = id;
        this.liquidCount = count;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public float getPrevLiquidHeight() {
        return this.prevLiquidHeight;
    }

    public float getLiquidHeight() {
        return this.liquidHeight;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BlenderBlockEntity blockEntity) {
        blockEntity.prevLiquidHeight = blockEntity.liquidHeight;
        float targetHeight = blockEntity.liquidCount * 3.0F; // max capacity is 6, max render height is 18 pixels (y from 13 to 31)
        if (blockEntity.liquidId == null || blockEntity.liquidCount <= 0) {
            targetHeight = 0.0F;
        }

        if (Math.abs(blockEntity.liquidHeight - targetHeight) > 0.01F) {
            blockEntity.liquidHeight += (targetHeight - blockEntity.liquidHeight) * 0.2F;
        } else {
            blockEntity.liquidHeight = targetHeight;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("LiquidId")) {
            this.liquidId = ResourceLocation.parse(tag.getString("LiquidId"));
        } else {
            this.liquidId = null;
        }
        this.liquidCount = tag.getInt("LiquidCount");
        // Initialize client animation height immediately on load to avoid jump
        this.liquidHeight = this.liquidCount * 3.0F;
        this.prevLiquidHeight = this.liquidHeight;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.liquidId != null) {
            tag.putString("LiquidId", this.liquidId.toString());
        }
        tag.putInt("LiquidCount", this.liquidCount);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }
}
