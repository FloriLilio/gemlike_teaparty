package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.effect.EndVisionEffect;
import com.lyuurain.teaparty.effect.FusionEffect;
import com.lyuurain.teaparty.effect.FrozenEffect;
import com.lyuurain.teaparty.effect.GelidEffect;
import com.lyuurain.teaparty.effect.LiesRhymeEffect;
import com.lyuurain.teaparty.effect.PerfectFrozenEffect;
import com.lyuurain.teaparty.effect.RebornEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, GemlikeTeaParty.MODID);

    public static final DeferredHolder<MobEffect, RebornEffect> REBORN = EFFECTS.register("reborn", RebornEffect::new);
    public static final DeferredHolder<MobEffect, EndVisionEffect> END_VISION = EFFECTS.register("end_vision", EndVisionEffect::new);
    public static final DeferredHolder<MobEffect, FusionEffect> FUSION = EFFECTS.register("fusion", FusionEffect::new);
    public static final DeferredHolder<MobEffect, LiesRhymeEffect> LIES_RHYME = EFFECTS.register("lies_rhyme", LiesRhymeEffect::new);
    public static final DeferredHolder<MobEffect, GelidEffect> GELID = EFFECTS.register("gelid", GelidEffect::new);
    public static final DeferredHolder<MobEffect, PerfectFrozenEffect> PERFECT_FROZEN = EFFECTS.register("perfect_frozen", PerfectFrozenEffect::new);
    public static final DeferredHolder<MobEffect, FrozenEffect> FROZEN = EFFECTS.register("frozen", FrozenEffect::new);
    public static final DeferredHolder<MobEffect, com.lyuurain.teaparty.effect.SoftTouchEffect> SOFT_TOUCH = EFFECTS.register("soft_touch", com.lyuurain.teaparty.effect.SoftTouchEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
