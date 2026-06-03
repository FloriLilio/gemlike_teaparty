package com.lyuurain.teaparty.client;

public class ClientMagicBottleCache {
    public static int count = 0;
    public static int lastCount = -1;
    public static int animState = 0;
    public static long stateStartTime = 0;
    public static long lastChangeTime = 0;
    public static boolean hasChanged = false;

    public static void update(int newCount) {
        if (lastCount == -1) {
            lastCount = newCount;
            count = newCount;
            return;
        }
        if (newCount != count) {
            lastCount = count;
            count = newCount;
            hasChanged = true;
            long now = System.currentTimeMillis();
            lastChangeTime = now;
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.playSound(net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            }
            if (animState == 0) {
                animState = 1;
                stateStartTime = now;
            } else if (animState == 2) {
                stateStartTime = now;
            } else if (animState == 3) {
                animState = 1;
                stateStartTime = now;
            }
        }
    }

    public static void forceTriggerZero() {
        long now = System.currentTimeMillis();
        boolean isChanged = (count != 0);
        if (isChanged) {
            lastCount = count;
            count = 0;
            lastChangeTime = now;
        }
        if (animState == 0) {
            animState = 1;
            stateStartTime = now;
        } else if (animState == 2) {
            stateStartTime = now;
        } else if (animState == 3) {
            animState = 1;
            stateStartTime = now;
        }
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.playSound(net.minecraft.sounds.SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
        }
    }

    public static void tick() {
        if (animState == 0) {
            return;
        }
        long now = System.currentTimeMillis();
        long elapsed = now - stateStartTime;
        if (animState == 1) {
            if (elapsed >= 300) {
                animState = 2;
                stateStartTime = now;
            }
        } else if (animState == 2) {
            if (elapsed >= 3000) {
                animState = 3;
                stateStartTime = now;
            }
        } else if (animState == 3) {
            if (elapsed >= 300) {
                animState = 0;
            }
        }
    }
}
