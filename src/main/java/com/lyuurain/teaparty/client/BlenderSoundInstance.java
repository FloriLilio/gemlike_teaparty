package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class BlenderSoundInstance extends AbstractTickableSoundInstance {
    private final BlenderBlockEntity blender;
    private final SoundEvent soundEvent;

    public BlenderSoundInstance(BlenderBlockEntity blender, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.BLOCKS, RandomSource.create());
        this.blender = blender;
        this.soundEvent = soundEvent;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.5F;
        this.pitch = 1.0F;
        this.x = (float) blender.getBlockPos().getX() + 0.5F;
        this.y = (float) blender.getBlockPos().getY() + 0.5F;
        this.z = (float) blender.getBlockPos().getZ() + 0.5F;
    }

    @Override
    public void tick() {
        if (this.blender.isRemoved() || !this.blender.getBlockState().getValue(com.lyuurain.teaparty.block.BlenderBlock.POWERED)) {
            this.stopSound();
            return;
        }
        this.x = (float) this.blender.getBlockPos().getX() + 0.5F;
        this.y = (float) this.blender.getBlockPos().getY() + 0.5F;
        this.z = (float) this.blender.getBlockPos().getZ() + 0.5F;
    }

    public void stopSound() {
        this.stop();
    }

    public SoundEvent getSoundEvent() {
        return this.soundEvent;
    }
}
