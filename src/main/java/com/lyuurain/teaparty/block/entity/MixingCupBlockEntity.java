package com.lyuurain.teaparty.block.entity;

import com.lyuurain.teaparty.recipe.MixingCupOutput;
import com.lyuurain.teaparty.recipe.MixingCupProcess;
import com.lyuurain.teaparty.registry.ModBlockEntities;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class MixingCupBlockEntity extends BlockEntity {
    private boolean opened = false;
    private List<MixingCupProcess> processes = null;
    private MixingCupOutput output = null;
    private boolean pickedUp = false;

    public MixingCupBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIXING_CUP_BE.get(), pos, state);
    }

    public boolean isOpened() {
        return this.opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
        this.setChanged();
    }

    public List<MixingCupProcess> getProcesses() {
        return this.processes;
    }

    public void setProcesses(List<MixingCupProcess> processes) {
        this.processes = processes;
        this.setChanged();
    }

    public MixingCupOutput getOutput() {
        return this.output;
    }

    public void setOutput(MixingCupOutput output) {
        this.output = output;
        this.setChanged();
    }

    public boolean isPickedUp() {
        return this.pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.opened = tag.getBoolean("Opened");
        if (tag.contains("Processes")) {
            MixingCupProcess.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get("Processes"))
                    .result().ifPresent(list -> this.processes = list);
        } else {
            this.processes = null;
        }
        if (tag.contains("Output")) {
            MixingCupOutput.CODEC.parse(NbtOps.INSTANCE, tag.get("Output"))
                    .result().ifPresent(out -> this.output = out);
        } else {
            this.output = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Opened", this.opened);
        if (this.processes != null) {
            DataResult<Tag> result = MixingCupProcess.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.processes);
            result.result().ifPresent(t -> tag.put("Processes", t));
        }
        if (this.output != null) {
            DataResult<Tag> result = MixingCupOutput.CODEC.encodeStart(NbtOps.INSTANCE, this.output);
            result.result().ifPresent(t -> tag.put("Output", t));
        }
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
