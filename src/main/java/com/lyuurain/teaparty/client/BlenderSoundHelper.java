package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.block.entity.BlenderBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import java.util.HashMap;
import java.util.Map;

public class BlenderSoundHelper {
    private static final Map<BlockPos, BlenderSoundInstance> ACTIVE_SOUNDS = new HashMap<>();

    public static void tickSound(BlenderBlockEntity blender) {
        BlockPos pos = blender.getBlockPos();
        boolean isPowered = blender.getBlockState().getValue(com.lyuurain.teaparty.block.BlenderBlock.POWERED);
        boolean isRemoved = blender.isRemoved();

        if (isRemoved || !isPowered) {
            if (ACTIVE_SOUNDS.containsKey(pos)) {
                BlenderSoundInstance sound = ACTIVE_SOUNDS.remove(pos);
                if (sound != null) {
                    sound.stopSound();
                }
            }
            return;
        }

        SoundEvent requiredSound = SoundEvents.BEE_LOOP;

        if (ACTIVE_SOUNDS.containsKey(pos)) {
            BlenderSoundInstance currentSound = ACTIVE_SOUNDS.get(pos);
            if (currentSound.getSoundEvent() != requiredSound) {
                currentSound.stopSound();
                BlenderSoundInstance newSound = new BlenderSoundInstance(blender, requiredSound);
                Minecraft.getInstance().getSoundManager().play(newSound);
                ACTIVE_SOUNDS.put(pos, newSound);
            }
        } else {
            BlenderSoundInstance newSound = new BlenderSoundInstance(blender, requiredSound);
            Minecraft.getInstance().getSoundManager().play(newSound);
            ACTIVE_SOUNDS.put(pos, newSound);
        }
    }
}
