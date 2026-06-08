package com.lyuurain.teaparty.block.entity;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.BlenderBlock;
import com.lyuurain.teaparty.recipe.BlenderRecipe;
import com.lyuurain.teaparty.recipe.BlenderRecipeManager;
import com.lyuurain.teaparty.recipe.MatchResult;
import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.lyuurain.teaparty.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BlenderBlockEntity extends BlockEntity {
    private ResourceLocation liquidId = null;
    private int liquidCount = 0;
    private int craftingProgress = 0;
    private int totalCraftingTicks = 0;

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 4) {
                return false;
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 4 || isBlockEntityPowered()) {
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
            updateBlockStateIfChanged();
            syncToClient();
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            if (!nbt.contains("Size", CompoundTag.TAG_INT) || nbt.getInt("Size") < 5) {
                nbt = nbt.copy();
                nbt.putInt("Size", 5);
            }
            super.deserializeNBT(provider, nbt);
        }

        @Override
        public int getSlots() {
            return 5;
        }
    };

    private float prevLiquidHeight = 0.0F;
    private float liquidHeight = 0.0F;

    private void updateBlockStateIfChanged() {
        if (this.level != null) {
            BlockState state = this.getBlockState();
            boolean currentHasContents = !this.isEmpty() || this.liquidCount > 0;
            if (state.hasProperty(BlenderBlock.HAS_CONTENTS) && state.getValue(BlenderBlock.HAS_CONTENTS) != currentHasContents) {
                this.level.setBlock(this.worldPosition, state.setValue(BlenderBlock.HAS_CONTENTS, currentHasContents), 3);
                BlockPos upperPos = this.worldPosition.above();
                BlockState upperState = this.level.getBlockState(upperPos);
                if (upperState.is(state.getBlock()) && upperState.getValue(BlenderBlock.HALF) == DoubleBlockHalf.UPPER) {
                    this.level.setBlock(upperPos, upperState.setValue(BlenderBlock.HAS_CONTENTS, currentHasContents), 3);
                }
            }
        }
    }

    public BlenderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLENDER_BE.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    public NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> list = NonNullList.withSize(5, ItemStack.EMPTY);
        for (int i = 0; i < 5; i++) {
            list.set(i, this.itemHandler.getStackInSlot(i).copy());
        }
        return list;
    }

    public NonNullList<ItemStack> getInputItems() {
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

    public boolean hasProductSlotItem() {
        return !this.itemHandler.getStackInSlot(4).isEmpty();
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
        if (stack.isEmpty() || hasProductSlotItem()) {
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
        if (!this.itemHandler.getStackInSlot(4).isEmpty()) {
            return this.itemHandler.extractItem(4, this.itemHandler.getStackInSlot(4).getMaxStackSize(), false);
        }

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
            ModBlocks.BLENDER_LIGHT.get(),
            ModBlocks.BLENDER_DARK.get()
        );
    }

    @Nullable
    public ResourceLocation getLiquidId() {
        return this.liquidId;
    }

    public int getLiquidCount() {
        return this.liquidCount;
    }

    public void setLiquid(@Nullable ResourceLocation id, int count) {
        this.liquidId = id;
        this.liquidCount = count;
        this.setChanged();
        updateBlockStateIfChanged();
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
        float targetHeight = blockEntity.liquidCount * 3.0F;
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlenderBlockEntity be) {
        if (!state.getValue(BlenderBlock.POWERED)) {
            return;
        }

        if (be.hasProductSlotItem()) {
            return;
        }

        if (be.isEmpty() && (be.liquidId == null || be.liquidCount <= 0)) {
            return;
        }

        NonNullList<ItemStack> inputItems = be.getInputItems();
        BlenderRecipe match = BlenderRecipeManager.findMatch(inputItems, be.liquidId, be.liquidCount);

        if (match != null) {
            MatchResult result = match.match(inputItems, be.liquidId, be.liquidCount);
            if (result != null && result.totalTicks() > 0) {
                be.totalCraftingTicks = result.totalTicks();
                be.craftingProgress++;
                if (be.craftingProgress >= be.totalCraftingTicks) {
                    be.completeCraft(match, result.totalYield());
                }
                be.setChanged();
            }
        } else {
            be.handleFallbackCrafting();
        }
    }

    private void completeCraft(BlenderRecipe recipe, float yield) {
        if (recipe.output() instanceof BlenderRecipe.ItemOutput itemOut) {
            int outputCount = Math.min((int) yield, recipe.maxPerBatch());
            if (outputCount > 0) {
                ResourceLocation itemId = itemOut.item();
                var item = BuiltInRegistries.ITEM.get(itemId);
                ItemStack outputStack = new ItemStack(item, outputCount);
                this.itemHandler.setStackInSlot(4, outputStack);
            }
            if (this.liquidId != null && this.liquidCount > 0) {
                this.setLiquid(null, 0);
            }
        } else if (recipe.output() instanceof BlenderRecipe.LiquidOutput liqOut) {
            int outputBottles = Math.min((int) yield, 6);
            if (outputBottles > 0) {
                this.setLiquid(liqOut.liquid(), outputBottles);
            }
        }

        for (int i = 0; i < 4; i++) {
            this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        this.craftingProgress = 0;
        this.totalCraftingTicks = 0;

        if (this.level != null && !this.level.isClientSide) {
            this.level.levelEvent(2005, this.worldPosition, 0);
            this.level.playSound(null, this.worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
            BlenderBlock.deactivateAdjacentRedstoneSources(this.level, this.worldPosition);
        }
    }

    private void handleFallbackCrafting() {
        if (this.liquidId == null || this.liquidCount <= 0) {
            this.totalCraftingTicks = 100;
            this.craftingProgress++;
            if (this.craftingProgress >= this.totalCraftingTicks) {
                for (int i = 0; i < 4; i++) {
                    this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
                this.craftingProgress = 0;
                this.totalCraftingTicks = 0;
            }
        } else {
            this.totalCraftingTicks = this.liquidCount * 30;
            this.craftingProgress++;
            if (this.craftingProgress >= this.totalCraftingTicks) {
                int outputBottles = Math.min(this.liquidCount, 6);
                ResourceLocation strangeId = ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, "strange_drink_glass");
                this.setLiquid(strangeId, outputBottles);
                for (int i = 0; i < 4; i++) {
                    this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
                this.craftingProgress = 0;
                this.totalCraftingTicks = 0;

                if (this.level != null && !this.level.isClientSide) {
                    this.level.levelEvent(2005, this.worldPosition, 0);
                    this.level.playSound(null, this.worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    BlenderBlock.deactivateAdjacentRedstoneSources(this.level, this.worldPosition);
                }
            }
        }
        this.setChanged();
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
        this.liquidHeight = this.liquidCount * 3.0F;
        this.prevLiquidHeight = this.liquidHeight;

        this.craftingProgress = tag.getInt("CraftingProgress");
        this.totalCraftingTicks = tag.getInt("TotalCraftingTicks");

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
        tag.putInt("CraftingProgress", this.craftingProgress);
        tag.putInt("TotalCraftingTicks", this.totalCraftingTicks);
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
