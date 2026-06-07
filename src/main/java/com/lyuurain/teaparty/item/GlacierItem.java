package com.lyuurain.teaparty.item;

import java.util.Arrays;

import com.lyuurain.teaparty.config.ConfigValues;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.registry.ModEffects;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class GlacierItem extends TooltipItem {
    private static final String DISABLED_MESSAGE_KEY = DrinkItem.DISABLED_MESSAGE_KEY;
    private static final int PARTICLE_INTERVAL = 4;
    private static final int PARTICLE_POINTS = 72;
    private static final DustParticleOptions GLACIER_PARTICLE = new DustParticleOptions(new Vector3f(0.35F, 0.75F, 1.0F), 1.0F);

    public GlacierItem(Properties properties, TooltipLine... tooltipLines) {
        super(properties, tooltipLines);
    }

    public static boolean isDrinkingGlacier(LivingEntity livingEntity) {
        return livingEntity.isUsingItem() && livingEntity.getUseItem().is(ModItems.GLACIER.get());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 32;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide()) {
            stopHorizontalMovement(livingEntity);

            if (ModConfig.COMMON.showGlacierRange && level instanceof ServerLevel serverLevel && remainingUseDuration % PARTICLE_INTERVAL == 0) {
                spawnParticleCircle(serverLevel, livingEntity);
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide()) {
            if (Arrays.asList(ModConfig.COMMON.disabledDrinks).contains(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).toString()) || ConfigValues.isDimensionListed(level.dimension(), ModConfig.COMMON.disabledGlacierDimensions)) {
                livingEntity.extinguishFire();

                if (livingEntity instanceof Player player) {
                    player.displayClientMessage(Component.translatable(DISABLED_MESSAGE_KEY).withStyle(ChatFormatting.GRAY), true);
                }
            } else {
                livingEntity.addEffect(new MobEffectInstance(getGlacierEffect(stack), ModConfig.COMMON.glacierGelidDuration, 0, false, true, true));
            }
        }

        if (!(livingEntity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }

    private Holder<MobEffect> getGlacierEffect(ItemStack stack) {
        if (!ModConfig.COMMON.allowGlacierEasterEgg || !stack.has(DataComponents.CUSTOM_NAME)) {
            return ModEffects.GELID;
        }

        String name = stack.getHoverName().getString();
        return name.equals("baka") || name.equals("cirno") || name.equals("⑨") ? ModEffects.PERFECT_FROZEN : ModEffects.GELID;
    }

    private void stopHorizontalMovement(LivingEntity livingEntity) {
        Vec3 movement = livingEntity.getDeltaMovement();
        livingEntity.setDeltaMovement(0.0D, movement.y, 0.0D);
        livingEntity.setSprinting(false);
    }

    private void spawnParticleCircle(ServerLevel serverLevel, LivingEntity livingEntity) {
        double centerX = livingEntity.getX();
        double centerY = livingEntity.getY() + 0.1D;
        double centerZ = livingEntity.getZ();
        double radius = ModConfig.COMMON.glacierFreezeRadius;

        for (int i = 0; i < PARTICLE_POINTS; i++) {
            double angle = Math.TAU * i / PARTICLE_POINTS;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            serverLevel.sendParticles(GLACIER_PARTICLE, x, centerY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.GLOW, x, centerY + 0.05D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }
}
