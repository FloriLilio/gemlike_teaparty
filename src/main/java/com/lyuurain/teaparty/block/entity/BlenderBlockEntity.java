package com.lyuurain.teaparty.block.entity;

import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.block.BlenderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlenderBlockEntity extends BlockEntity {
    private ResourceLocation liquidId = null;
    private int liquidCount = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (isBlockEntityPowered()) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isBlockEntityPowered()) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            syncToClient();
        }
    };

    // Client-side animation height
    private float prevLiquidHeight = 0.0F;
    private float liquidHeight = 0.0F;

    public BlenderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLENDER_BE.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    public NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> list = NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            list.set(i, this.itemHandler.getStackInSlot(i).copy());
        }
        return list;
    }

    public boolean isEmpty() {
        for (int i = 0; i < 4; i++) {
            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isBlockEntityPowered() {
        if (this.level == null) {
            return false;
        }
        BlockState state = this.getBlockState();
        if (state.hasProperty(BlenderBlock.POWERED)) {
            return state.getValue(BlenderBlock.POWERED);
        }
        return false;
    }

    public boolean insertItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        int topSlot = -1;
        for (int i = 3; i >= 0; i--) {
            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                topSlot = i;
                break;
            }
        }

        int countToInsert = 1;

        if (topSlot == -1) {
            ItemStack insertStack = stack.copyWithCount(countToInsert);
            ItemStack remainder = this.itemHandler.insertItem(0, insertStack, false);
            int accepted = countToInsert - remainder.getCount();
            if (accepted > 0) {
                stack.shrink(accepted);
                return true;
            }
        } else {
            ItemStack topStack = this.itemHandler.getStackInSlot(topSlot);
            if (ItemStack.isSameItemSameComponents(topStack, stack)) {
                ItemStack insertStack = stack.copyWithCount(countToInsert);
                ItemStack remainder = this.itemHandler.insertItem(topSlot, insertStack, false);
                int accepted = countToInsert - remainder.getCount();
                if (accepted > 0) {
                    stack.shrink(accepted);
                    return true;
                }
            }

            if (topSlot < 3) {
                int nextSlot = topSlot + 1;
                ItemStack insertStack = stack.copyWithCount(countToInsert);
                ItemStack remainder = this.itemHandler.insertItem(nextSlot, insertStack, false);
                int accepted = countToInsert - remainder.getCount();
                if (accepted > 0) {
                    stack.shrink(accepted);
                    return true;
                }
            }
        }
        return false;
    }

    public ItemStack extractItem() {
        int topSlot = -1;
        for (int i = 3; i >= 0; i--) {
            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                topSlot = i;
                break;
            }
        }

        if (topSlot == -1) {
            return ItemStack.EMPTY;
        }

        return this.itemHandler.extractItem(topSlot, 1, false);
    }

    private void syncToClient() {
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.BLENDER_BE.get(),
            (be, side) -> be.getItemHandler()
        );

        event.registerBlock(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
            (level, pos, state, be, side) -> {
                if (state.getValue(BlenderBlock.HALF) == DoubleBlockHalf.UPPER) {
                    BlockEntity lowerBe = level.getBlockEntity(pos.below());
                    if (lowerBe instanceof BlenderBlockEntity blender) {
                        return blender.getItemHandler();
                    }
                } else if (be instanceof BlenderBlockEntity blender) {
                    return blender.getItemHandler();
                }
                return null;
            },
            ModBlocks.BLENDER.get()
        );
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

        com.lyuurain.teaparty.client.BlenderSoundHelper.tickSound(blockEntity);
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

        if (tag.contains("Inventory")) {
            this.itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.liquidId != null) {
            tag.putString("LiquidId", this.liquidId.toString());
        }
        tag.putInt("LiquidCount", this.liquidCount);
        tag.put("Inventory", this.itemHandler.serializeNBT(registries));
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
