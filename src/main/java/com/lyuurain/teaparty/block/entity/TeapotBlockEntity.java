package com.lyuurain.teaparty.block.entity;

import com.lyuurain.teaparty.block.TeapotBlock;
import com.lyuurain.teaparty.recipe.MatchResult;
import com.lyuurain.teaparty.recipe.TeapotRecipe;
import com.lyuurain.teaparty.recipe.TeapotRecipeManager;
import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.lyuurain.teaparty.registry.ModSounds;
import com.lyuurain.teaparty.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TeapotBlockEntity extends BlockEntity {
    private static final int SLOT_COUNT = 4;
    private ResourceLocation liquidId = null;
    private int liquidCount = 0;
    private int craftingProgress = 0;
    private int totalCraftingTicks = 0;
    private boolean finished = false;
    private int activeTick = 0;
    private boolean changingContents = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (isProcessingLocked()) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (isProcessingLocked()) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!changingContents && !isProcessingLocked()) {
                resetCrafting();
            }
            setChanged();
            syncToClient();
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            if (!nbt.contains("Size", CompoundTag.TAG_INT) || nbt.getInt("Size") < SLOT_COUNT) {
                nbt = nbt.copy();
                nbt.putInt("Size", SLOT_COUNT);
            }
            super.deserializeNBT(provider, nbt);
        }

        @Override
        public int getSlots() {
            return SLOT_COUNT;
        }
    };

    public TeapotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEAPOT_BE.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    public NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> list = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < SLOT_COUNT; i++) {
            list.set(i, this.itemHandler.getStackInSlot(i).copy());
        }
        return list;
    }

    public NonNullList<ItemStack> getInputItems() {
        return getItems();
    }

    public boolean isEmpty() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean insertItem(ItemStack stack) {
        if (stack.isEmpty() || isProcessingLocked()) {
            return false;
        }

        int topSlot = -1;
        for (int i = SLOT_COUNT - 1; i >= 0; i--) {
            if (!this.itemHandler.getStackInSlot(i).isEmpty()) {
                topSlot = i;
                break;
            }
        }

        if (topSlot == -1) {
            return insertOneItem(stack, 0);
        }

        ItemStack topStack = this.itemHandler.getStackInSlot(topSlot);
        if (ItemStack.isSameItemSameComponents(topStack, stack) && insertOneItem(stack, topSlot)) {
            return true;
        }

        if (topSlot < SLOT_COUNT - 1) {
            return insertOneItem(stack, topSlot + 1);
        }
        return false;
    }

    private boolean insertOneItem(ItemStack stack, int slot) {
        ItemStack insertStack = stack.copyWithCount(1);
        ItemStack remainder = this.itemHandler.insertItem(slot, insertStack, false);
        if (remainder.isEmpty()) {
            stack.shrink(1);
            return true;
        }
        return false;
    }

    public ItemStack extractItem() {
        if (isProcessingLocked()) {
            return ItemStack.EMPTY;
        }

        int topSlot = -1;
        for (int i = SLOT_COUNT - 1; i >= 0; i--) {
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

    public boolean canModifyContents() {
        return !this.finished && this.craftingProgress <= 0;
    }

    public boolean isProcessingLocked() {
        return this.finished || this.craftingProgress > 0;
    }

    public boolean isFinished() {
        return this.finished;
    }

    @Nullable
    public ResourceLocation getLiquidId() {
        return this.liquidId;
    }

    public int getLiquidCount() {
        return this.liquidCount;
    }

    public void setLiquid(@Nullable ResourceLocation id, int count) {
        this.liquidId = count > 0 ? id : null;
        this.liquidCount = Math.max(count, 0);
        if (this.liquidCount <= 0) {
            this.finished = false;
            this.activeTick = 0;
        }
        if (!this.finished) {
            this.craftingProgress = 0;
            this.totalCraftingTicks = 0;
        }
        setChanged();
        syncToClient();
    }

    public void resetCrafting() {
        this.craftingProgress = 0;
        this.totalCraftingTicks = 0;
        this.finished = false;
        this.activeTick = 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TeapotBlockEntity be) {
        boolean opened = state.getValue(TeapotBlock.OPENED);
        boolean heated = isHeated(level, pos);

        if (be.finished) {
            be.tickFinished(level, pos, state, opened, heated);
            return;
        }

        if (opened || !heated || be.liquidId == null || be.liquidCount <= 0) {
            setActive(level, pos, state, 0);
            return;
        }

        TeapotRecipe match = TeapotRecipeManager.findMatch(be.getInputItems(), be.liquidId, be.liquidCount);
        if (match == null) {
            if (be.craftingProgress > 0) {
                be.resetCrafting();
                be.setChanged();
                be.syncToClient();
            }
            setActive(level, pos, state, 0);
            return;
        }

        MatchResult result = match.match(be.getInputItems(), be.liquidId, be.liquidCount);
        if (result != null && result.totalTicks() > 0) {
            be.totalCraftingTicks = result.totalTicks();
            be.craftingProgress++;
            if (be.craftingProgress >= be.totalCraftingTicks) {
                be.completeCraft(match);
            } else {
                be.setChanged();
                be.syncToClient();
            }
        }
    }

    private void tickFinished(Level level, BlockPos pos, BlockState state, boolean opened, boolean heated) {
        if (opened || !heated || this.liquidId == null || this.liquidCount <= 0) {
            setActive(level, pos, state, 0);
            this.activeTick = 0;
            return;
        }

        this.activeTick++;
        int active = (this.activeTick / 4) % 3 + 1;
        setActive(level, pos, state, active);

        if (this.activeTick % 20 == 0) {
            level.playSound(null, pos, ModSounds.TEAPOT_WHISTLE.get(), SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
        }
        if (this.activeTick % 5 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD,
                    pos.getX() + 0.5D, pos.getY() + 0.9D, pos.getZ() + 0.5D,
                    2, 0.12D, 0.05D, 0.12D, 0.01D);
        }
    }

    private void completeCraft(TeapotRecipe recipe) {
        this.liquidId = recipe.output();
        this.finished = true;
        this.craftingProgress = 0;
        this.totalCraftingTicks = 0;
        this.changingContents = true;
        for (int i = 0; i < SLOT_COUNT; i++) {
            this.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        this.changingContents = false;
        setChanged();
        syncToClient();
    }

    public static boolean isHeated(Level level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.TEAPOT_HEAT_SOURCES);
    }

    private static void setActive(Level level, BlockPos pos, BlockState state, int active) {
        if (state.hasProperty(TeapotBlock.ACTIVE) && state.getValue(TeapotBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(TeapotBlock.ACTIVE, active), 3);
        }
    }

    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.TEAPOT_BE.get(),
                (be, side) -> be.getItemHandler()
        );
    }

    private void syncToClient() {
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
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
        this.craftingProgress = tag.getInt("CraftingProgress");
        this.totalCraftingTicks = tag.getInt("TotalCraftingTicks");
        this.finished = tag.getBoolean("Finished");
        this.activeTick = tag.getInt("ActiveTick");

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
        tag.putBoolean("Finished", this.finished);
        tag.putInt("ActiveTick", this.activeTick);
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
