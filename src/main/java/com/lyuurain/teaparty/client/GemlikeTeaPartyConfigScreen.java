package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.config.EndVisionFilterMode;
import com.lyuurain.teaparty.config.FusionWarningSoundMode;
import com.lyuurain.teaparty.config.ModConfig;
import com.lyuurain.teaparty.config.SirenHealthReductionMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GemlikeTeaPartyConfigScreen extends Screen {
    private static final int GLACIER_PAGE_COUNT = 2;
    private static final int SIRENS_DEW_PAGE_COUNT = 2;
    private final Screen parent;
    private final List<Label> labels = new ArrayList<>();
    private Page page = Page.GLACIER;
    private int glacierPage;
    private int sirensDewPage;
    private boolean showGlacierRange;
    private double glacierFreezeRadius;
    private int gelidFrozenDuration;
    private int frozenSlownessDuration;
    private int glacierGelidDuration;
    private boolean allowSkeletonToStrayConversion;
    private String disabledGlacierDimensions;
    private String frozenImmuneEntities;
    private float frozenDamage;
    private boolean allowGlacierEasterEgg;
    private int endVisionDuration;
    private int endVisionRadius;
    private EndVisionFilterMode endVisionBlockFilterMode;
    private String endVisionBlockFilter;
    private boolean endVisionShowEntities;
    private EndVisionFilterMode endVisionEntityFilterMode;
    private String endVisionEntityFilter;
    private String endVisionOutlineColor;
    private int dreamySkyTeleportHeight;
    private boolean dreamySkyCheckTopBlock;
    private boolean showDreamySkyParticles;
    private String dreamySkyDisabledDimensions;
    private int undergroundSunFusionDuration;
    private float fusionBlockExplosionRadius;
    private float fusionAttackExplosionRadius;
    private float fusionSelfExplosionRadius;
    private FusionWarningSoundMode fusionWarningSoundMode;
    private int liesRhymeDuration;
    private double sirensDewBuffRadius;
    private double liesRhymeEndDebuffRadius;
    private int liesRhymeWarningRangeDisplayTime;
    private SirenHealthReductionMode liesRhymeHealthReductionMode;
    private double liesRhymeHealthReductionPercentage;
    private double liesRhymeHealthReductionAmount;
    private float liesRhymeEndDamage;
    private boolean showFrozenFirstPersonOverlay;
    private boolean showFrozenIceBlock;
    private boolean tintFrozenPlayers;
    private int maxMagicBottleCount;
    private int shakeTime;
    private int stirTime;
    private String magicBottleHudPosition;

    public GemlikeTeaPartyConfigScreen(Screen parent) {
        super(Component.translatable("config.gemlike_teaparty.title"));
        this.parent = parent;
        loadValues();
    }

    @Override
    protected void init() {
        rebuildPage();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

        for (Label label : this.labels) {
            guiGraphics.drawString(this.font, label.component(), label.x(), label.y(), 0xFFFFFF);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void rebuildPage() {
        clearWidgets();
        this.labels.clear();
        addPageButtons();
        addSubPageButtons();
        addPageFields();
        addBottomButtons();
    }

    private void addPageButtons() {
        int x = this.width / 2 - 100;
        addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            this.page = Page.values()[Math.floorMod(this.page.ordinal() - 1, Page.values().length)];
            rebuildPage();
        }).bounds(x, 34, 20, 20).build());

        addRenderableWidget(Button.builder(Component.translatable(this.page.key), button -> {
            this.page = Page.values()[Math.floorMod(this.page.ordinal() + 1, Page.values().length)];
            rebuildPage();
        }).bounds(x + 24, 34, 152, 20).build());

        addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            this.page = Page.values()[Math.floorMod(this.page.ordinal() + 1, Page.values().length)];
            rebuildPage();
        }).bounds(x + 180, 34, 20, 20).build());
    }

    private void addSubPageButtons() {
        int pageIndex;
        int pageCount;
        Consumer<Integer> setter;

        if (this.page == Page.GLACIER) {
            pageIndex = this.glacierPage;
            pageCount = GLACIER_PAGE_COUNT;
            setter = value -> this.glacierPage = value;
        } else if (this.page == Page.SIRENS_DEW) {
            pageIndex = this.sirensDewPage;
            pageCount = SIRENS_DEW_PAGE_COUNT;
            setter = value -> this.sirensDewPage = value;
        } else {
            return;
        }

        int currentPage = pageIndex;
        addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            setter.accept(Math.floorMod(currentPage - 1, pageCount));
            rebuildPage();
        }).bounds(this.width / 2 - 75, 56, 24, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.subpage", currentPage + 1, pageCount), button -> {
        }).bounds(this.width / 2 - 47, 56, 94, 20).build()).active = false;
        addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            setter.accept(Math.floorMod(currentPage + 1, pageCount));
            rebuildPage();
        }).bounds(this.width / 2 + 51, 56, 24, 20).build());
    }

    private void addPageFields() {
        int labelX = Math.max(20, this.width / 2 - 230);
        int widgetX = this.width / 2 + 40;
        int y = this.page == Page.GLACIER || this.page == Page.SIRENS_DEW ? 84 : 68;
        int rowHeight = 22;

        switch (this.page) {
            case GLACIER -> {
                if (this.glacierPage == 0) {
                    addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_glacier_range", this.showGlacierRange, value -> this.showGlacierRange = value);
                    y += rowHeight;
                    addDoubleField(labelX, widgetX, y, "config.gemlike_teaparty.glacier_freeze_radius", this.glacierFreezeRadius, 0.0D, 128.0D, value -> this.glacierFreezeRadius = value);
                    y += rowHeight;
                    addIntField(labelX, widgetX, y, "config.gemlike_teaparty.gelid_frozen_duration", this.gelidFrozenDuration, 0, 72000, value -> this.gelidFrozenDuration = value);
                    y += rowHeight;
                    addIntField(labelX, widgetX, y, "config.gemlike_teaparty.frozen_slowness_duration", this.frozenSlownessDuration, 0, 72000, value -> this.frozenSlownessDuration = value);
                    y += rowHeight;
                    addIntField(labelX, widgetX, y, "config.gemlike_teaparty.glacier_gelid_duration", this.glacierGelidDuration, 0, 72000, value -> this.glacierGelidDuration = value);
                    y += rowHeight;
                    addFloatField(labelX, widgetX, y, "config.gemlike_teaparty.frozen_damage", this.frozenDamage, 0.0F, 1000.0F, value -> this.frozenDamage = value);
                } else {
                    addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.allow_skeleton_to_stray_conversion", this.allowSkeletonToStrayConversion, value -> this.allowSkeletonToStrayConversion = value);
                    y += rowHeight;
                    addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.allow_glacier_easter_egg", this.allowGlacierEasterEgg, value -> this.allowGlacierEasterEgg = value);
                    y += rowHeight;
                    addStringField(labelX, widgetX, y, "config.gemlike_teaparty.disabled_glacier_dimensions", this.disabledGlacierDimensions, value -> this.disabledGlacierDimensions = value);
                    y += rowHeight;
                    addStringField(labelX, widgetX, y, "config.gemlike_teaparty.frozen_immune_entities", this.frozenImmuneEntities, value -> this.frozenImmuneEntities = value);
                }
            }
            case END_VISION -> {
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.end_vision_duration", this.endVisionDuration, 0, 72000, value -> this.endVisionDuration = value);
                y += rowHeight;
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.end_vision_radius", this.endVisionRadius, 0, 64, value -> this.endVisionRadius = value);
                y += rowHeight;
                addEndVisionBlockFilterModeField(labelX, widgetX, y);
                y += rowHeight;
                addStringField(labelX, widgetX, y, "config.gemlike_teaparty.end_vision_block_filter", this.endVisionBlockFilter, value -> this.endVisionBlockFilter = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.end_vision_show_entities", this.endVisionShowEntities, value -> this.endVisionShowEntities = value);
                y += rowHeight;
                addEndVisionEntityFilterModeField(labelX, widgetX, y);
                y += rowHeight;
                addStringField(labelX, widgetX, y, "config.gemlike_teaparty.end_vision_entity_filter", this.endVisionEntityFilter, value -> this.endVisionEntityFilter = value);
            }
            case DREAMY_SKY -> {
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.dreamy_sky_teleport_height", this.dreamySkyTeleportHeight, -64, 1024, value -> this.dreamySkyTeleportHeight = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.dreamy_sky_check_top_block", this.dreamySkyCheckTopBlock, value -> this.dreamySkyCheckTopBlock = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_dreamy_sky_particles", this.showDreamySkyParticles, value -> this.showDreamySkyParticles = value);
                y += rowHeight;
                addStringField(labelX, widgetX, y, "config.gemlike_teaparty.dreamy_sky_disabled_dimensions", this.dreamySkyDisabledDimensions, value -> this.dreamySkyDisabledDimensions = value);
            }
            case UNDERGROUND_SUN -> {
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.underground_sun_fusion_duration", this.undergroundSunFusionDuration, 0, 72000, value -> this.undergroundSunFusionDuration = value);
                y += rowHeight;
                addFloatField(labelX, widgetX, y, "config.gemlike_teaparty.fusion_block_explosion_radius", this.fusionBlockExplosionRadius, 0.0F, 128.0F, value -> this.fusionBlockExplosionRadius = value);
                y += rowHeight;
                addFloatField(labelX, widgetX, y, "config.gemlike_teaparty.fusion_attack_explosion_radius", this.fusionAttackExplosionRadius, 0.0F, 128.0F, value -> this.fusionAttackExplosionRadius = value);
                y += rowHeight;
                addFloatField(labelX, widgetX, y, "config.gemlike_teaparty.fusion_self_explosion_radius", this.fusionSelfExplosionRadius, 0.0F, 128.0F, value -> this.fusionSelfExplosionRadius = value);
                y += rowHeight;
                addFusionWarningSoundModeField(labelX, widgetX, y);
            }
            case SIRENS_DEW -> {
                if (this.sirensDewPage == 0) {
                    addIntField(labelX, widgetX, y, "config.gemlike_teaparty.lies_rhyme_duration", this.liesRhymeDuration, 0, 72000, value -> this.liesRhymeDuration = value);
                    y += rowHeight;
                    addDoubleField(labelX, widgetX, y, "config.gemlike_teaparty.sirens_dew_buff_radius", this.sirensDewBuffRadius, 0.0D, 128.0D, value -> this.sirensDewBuffRadius = value);
                    y += rowHeight;
                    addDoubleField(labelX, widgetX, y, "config.gemlike_teaparty.lies_rhyme_end_debuff_radius", this.liesRhymeEndDebuffRadius, 0.0D, 128.0D, value -> this.liesRhymeEndDebuffRadius = value);
                    y += rowHeight;
                    addIntField(labelX, widgetX, y, "config.gemlike_teaparty.lies_rhyme_warning_range_display_time", this.liesRhymeWarningRangeDisplayTime, -1, 72000, value -> this.liesRhymeWarningRangeDisplayTime = value);
                } else {
                    addSirenHealthReductionModeField(labelX, widgetX, y);
                    y += rowHeight;
                    addDoubleField(labelX, widgetX, y, "config.gemlike_teaparty.lies_rhyme_health_reduction_percentage", this.liesRhymeHealthReductionPercentage, 0.0D, 1.0D, value -> this.liesRhymeHealthReductionPercentage = value);
                    y += rowHeight;
                    addDoubleField(labelX, widgetX, y, "config.gemlike_teaparty.lies_rhyme_health_reduction_amount", this.liesRhymeHealthReductionAmount, 0.0D, 1024.0D, value -> this.liesRhymeHealthReductionAmount = value);
                    y += rowHeight;
                    addFloatField(labelX, widgetX, y, "config.gemlike_teaparty.lies_rhyme_end_damage", this.liesRhymeEndDamage, 0.0F, 1000.0F, value -> this.liesRhymeEndDamage = value);
                }
            }
            case OTHER -> {
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.max_magic_bottle_count", this.maxMagicBottleCount, 1, 1000000, value -> this.maxMagicBottleCount = value);
                y += rowHeight;
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.shake_time", this.shakeTime, 1, 72000, value -> this.shakeTime = value);
                y += rowHeight;
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.stir_time", this.stirTime, 1, 72000, value -> this.stirTime = value);
            }
            case CLIENT -> {
                addStringField(labelX, widgetX, y, "config.gemlike_teaparty.end_vision_outline_color", this.endVisionOutlineColor, value -> this.endVisionOutlineColor = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_frozen_first_person_overlay", this.showFrozenFirstPersonOverlay, value -> this.showFrozenFirstPersonOverlay = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_frozen_ice_block", this.showFrozenIceBlock, value -> this.showFrozenIceBlock = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.tint_frozen_players", this.tintFrozenPlayers, value -> this.tintFrozenPlayers = value);
                y += rowHeight;
                addMagicBottleHudPositionField(labelX, widgetX, y);
            }
        }
    }

    private void addBottomButtons() {
        int y = this.height - 30;
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.save"), button -> {
            saveValues();
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 155, y, 150, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 + 5, y, 150, 20).build());
    }

    private void addBooleanField(int labelX, int widgetX, int y, String key, boolean value, Consumer<Boolean> setter) {
        labels.add(new Label(Component.translatable(key), labelX, y + 6));
        addRenderableWidget(CycleButton.onOffBuilder(value).displayOnlyValue().create(widgetX, y, 180, 20, Component.empty(), (button, newValue) -> setter.accept(newValue)));
    }

    private void addIntField(int labelX, int widgetX, int y, String key, int value, int min, int max, Consumer<Integer> setter) {
        EditBox editBox = addStringField(labelX, widgetX, y, key, Integer.toString(value), text -> parseInt(text, min, max, setter));
        editBox.setFilter(text -> text.matches("-?\\d*"));
    }

    private void addDoubleField(int labelX, int widgetX, int y, String key, double value, double min, double max, Consumer<Double> setter) {
        EditBox editBox = addStringField(labelX, widgetX, y, key, Double.toString(value), text -> parseDouble(text, min, max, setter));
        editBox.setFilter(text -> text.matches("-?\\d*(\\.\\d*)?"));
    }

    private void addFloatField(int labelX, int widgetX, int y, String key, float value, float min, float max, Consumer<Float> setter) {
        EditBox editBox = addStringField(labelX, widgetX, y, key, Float.toString(value), text -> parseFloat(text, min, max, setter));
        editBox.setFilter(text -> text.matches("-?\\d*(\\.\\d*)?"));
    }

    private void addEndVisionBlockFilterModeField(int labelX, int widgetX, int y) {
        labels.add(new Label(Component.translatable("config.gemlike_teaparty.end_vision_block_filter_mode"), labelX, y + 6));
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.end_vision_filter_mode." + this.endVisionBlockFilterMode.configValue()), button -> {
            this.endVisionBlockFilterMode = nextEndVisionFilterMode(this.endVisionBlockFilterMode);
            button.setMessage(Component.translatable("config.gemlike_teaparty.end_vision_filter_mode." + this.endVisionBlockFilterMode.configValue()));
        }).bounds(widgetX, y, 180, 20).build());
    }

    private void addEndVisionEntityFilterModeField(int labelX, int widgetX, int y) {
        labels.add(new Label(Component.translatable("config.gemlike_teaparty.end_vision_entity_filter_mode"), labelX, y + 6));
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.end_vision_filter_mode." + this.endVisionEntityFilterMode.configValue()), button -> {
            this.endVisionEntityFilterMode = nextEndVisionFilterMode(this.endVisionEntityFilterMode);
            button.setMessage(Component.translatable("config.gemlike_teaparty.end_vision_filter_mode." + this.endVisionEntityFilterMode.configValue()));
        }).bounds(widgetX, y, 180, 20).build());
    }

    private static EndVisionFilterMode nextEndVisionFilterMode(EndVisionFilterMode mode) {
        return mode == EndVisionFilterMode.WHITELIST ? EndVisionFilterMode.BLACKLIST : EndVisionFilterMode.WHITELIST;
    }

    private void addFusionWarningSoundModeField(int labelX, int widgetX, int y) {
        labels.add(new Label(Component.translatable("config.gemlike_teaparty.fusion_warning_sound_mode"), labelX, y + 6));
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.fusion_warning_sound_mode." + this.fusionWarningSoundMode.configValue()), button -> {
            this.fusionWarningSoundMode = switch (this.fusionWarningSoundMode) {
                case OFF -> FusionWarningSoundMode.ONCE;
                case ONCE -> FusionWarningSoundMode.LOOP;
                case LOOP -> FusionWarningSoundMode.OFF;
            };
            button.setMessage(Component.translatable("config.gemlike_teaparty.fusion_warning_sound_mode." + this.fusionWarningSoundMode.configValue()));
        }).bounds(widgetX, y, 180, 20).build());
    }

    private void addSirenHealthReductionModeField(int labelX, int widgetX, int y) {
        labels.add(new Label(Component.translatable("config.gemlike_teaparty.lies_rhyme_health_reduction_mode"), labelX, y + 6));
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.lies_rhyme_health_reduction_mode." + this.liesRhymeHealthReductionMode.configValue()), button -> {
            this.liesRhymeHealthReductionMode = this.liesRhymeHealthReductionMode == SirenHealthReductionMode.FIXED ? SirenHealthReductionMode.PERCENTAGE : SirenHealthReductionMode.FIXED;
            button.setMessage(Component.translatable("config.gemlike_teaparty.lies_rhyme_health_reduction_mode." + this.liesRhymeHealthReductionMode.configValue()));
        }).bounds(widgetX, y, 180, 20).build());
    }

    private void addMagicBottleHudPositionField(int labelX, int widgetX, int y) {
        labels.add(new Label(Component.translatable("config.gemlike_teaparty.magic_bottle_hud_position"), labelX, y + 6));
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.magic_bottle_hud_position." + this.magicBottleHudPosition.toLowerCase()), button -> {
            this.magicBottleHudPosition = switch (this.magicBottleHudPosition.toLowerCase()) {
                case "left" -> "right";
                case "right" -> "top";
                default -> "left";
            };
            button.setMessage(Component.translatable("config.gemlike_teaparty.magic_bottle_hud_position." + this.magicBottleHudPosition.toLowerCase()));
        }).bounds(widgetX, y, 180, 20).build());
    }

    private EditBox addStringField(int labelX, int widgetX, int y, String key, String value, Consumer<String> setter) {
        labels.add(new Label(Component.translatable(key), labelX, y + 6));
        EditBox editBox = new EditBox(this.font, widgetX, y, 180, 20, Component.empty());
        editBox.setMaxLength(1024);
        editBox.setValue(value);
        editBox.setResponder(setter);
        addRenderableWidget(editBox);
        return editBox;
    }

    private void loadValues() {
        this.showGlacierRange = ModConfig.COMMON.showGlacierRange;
        this.glacierFreezeRadius = ModConfig.COMMON.glacierFreezeRadius;
        this.gelidFrozenDuration = ModConfig.COMMON.gelidFrozenDuration;
        this.frozenSlownessDuration = ModConfig.COMMON.frozenSlownessDuration;
        this.glacierGelidDuration = ModConfig.COMMON.glacierGelidDuration;
        this.allowSkeletonToStrayConversion = ModConfig.COMMON.allowSkeletonToStrayConversion;
        this.disabledGlacierDimensions = join(ModConfig.COMMON.disabledGlacierDimensions);
        this.frozenImmuneEntities = join(ModConfig.COMMON.frozenImmuneEntities);
        this.frozenDamage = ModConfig.COMMON.frozenDamage;
        this.allowGlacierEasterEgg = ModConfig.COMMON.allowGlacierEasterEgg;
        this.endVisionDuration = ModConfig.COMMON.endVisionDuration;
        this.endVisionRadius = ModConfig.COMMON.endVisionRadius;
        this.endVisionBlockFilterMode = EndVisionFilterMode.fromConfig(ModConfig.COMMON.endVisionBlockFilterMode, EndVisionFilterMode.WHITELIST);
        this.endVisionBlockFilter = join(ModConfig.COMMON.endVisionBlockFilter);
        this.endVisionShowEntities = ModConfig.COMMON.endVisionShowEntities;
        this.endVisionEntityFilterMode = EndVisionFilterMode.fromConfig(ModConfig.COMMON.endVisionEntityFilterMode, EndVisionFilterMode.BLACKLIST);
        this.endVisionEntityFilter = join(ModConfig.COMMON.endVisionEntityFilter);
        this.endVisionOutlineColor = ModConfig.CLIENT.endVisionOutlineColor;
        this.dreamySkyTeleportHeight = ModConfig.COMMON.dreamySkyTeleportHeight;
        this.dreamySkyCheckTopBlock = ModConfig.COMMON.dreamySkyCheckTopBlock;
        this.showDreamySkyParticles = ModConfig.COMMON.showDreamySkyParticles;
        this.dreamySkyDisabledDimensions = join(ModConfig.COMMON.dreamySkyDisabledDimensions);
        this.undergroundSunFusionDuration = ModConfig.COMMON.undergroundSunFusionDuration;
        this.fusionBlockExplosionRadius = ModConfig.COMMON.fusionBlockExplosionRadius;
        this.fusionAttackExplosionRadius = ModConfig.COMMON.fusionAttackExplosionRadius;
        this.fusionSelfExplosionRadius = ModConfig.COMMON.fusionSelfExplosionRadius;
        this.fusionWarningSoundMode = FusionWarningSoundMode.fromConfig(ModConfig.COMMON.fusionWarningSoundMode);
        this.liesRhymeDuration = ModConfig.COMMON.liesRhymeDuration;
        this.sirensDewBuffRadius = ModConfig.COMMON.sirensDewBuffRadius;
        this.liesRhymeEndDebuffRadius = ModConfig.COMMON.liesRhymeEndDebuffRadius;
        this.liesRhymeWarningRangeDisplayTime = ModConfig.COMMON.liesRhymeWarningRangeDisplayTime;
        this.liesRhymeHealthReductionMode = SirenHealthReductionMode.fromConfig(ModConfig.COMMON.liesRhymeHealthReductionMode);
        this.liesRhymeHealthReductionPercentage = ModConfig.COMMON.liesRhymeHealthReductionPercentage;
        this.liesRhymeHealthReductionAmount = ModConfig.COMMON.liesRhymeHealthReductionAmount;
        this.liesRhymeEndDamage = ModConfig.COMMON.liesRhymeEndDamage;
        this.showFrozenFirstPersonOverlay = ModConfig.CLIENT.showFrozenFirstPersonOverlay;
        this.showFrozenIceBlock = ModConfig.CLIENT.showFrozenIceBlock;
        this.tintFrozenPlayers = ModConfig.CLIENT.tintFrozenPlayers;
        this.maxMagicBottleCount = ModConfig.COMMON.maxMagicBottleCount;
        this.shakeTime = ModConfig.COMMON.shakeTime;
        this.stirTime = ModConfig.COMMON.stirTime;
        this.magicBottleHudPosition = ModConfig.CLIENT.magicBottleHudPosition;
    }

    private void saveValues() {
        ModConfig.COMMON.showGlacierRange = this.showGlacierRange;
        ModConfig.COMMON.glacierFreezeRadius = this.glacierFreezeRadius;
        ModConfig.COMMON.gelidFrozenDuration = this.gelidFrozenDuration;
        ModConfig.COMMON.frozenSlownessDuration = this.frozenSlownessDuration;
        ModConfig.COMMON.glacierGelidDuration = this.glacierGelidDuration;
        ModConfig.COMMON.allowSkeletonToStrayConversion = this.allowSkeletonToStrayConversion;
        ModConfig.COMMON.disabledGlacierDimensions = split(this.disabledGlacierDimensions);
        ModConfig.COMMON.frozenImmuneEntities = split(this.frozenImmuneEntities);
        ModConfig.COMMON.frozenDamage = this.frozenDamage;
        ModConfig.COMMON.allowGlacierEasterEgg = this.allowGlacierEasterEgg;
        ModConfig.COMMON.endVisionDuration = this.endVisionDuration;
        ModConfig.COMMON.endVisionRadius = this.endVisionRadius;
        ModConfig.COMMON.endVisionBlockFilterMode = this.endVisionBlockFilterMode.configValue();
        ModConfig.COMMON.endVisionBlockFilter = split(this.endVisionBlockFilter);
        ModConfig.COMMON.endVisionShowEntities = this.endVisionShowEntities;
        ModConfig.COMMON.endVisionEntityFilterMode = this.endVisionEntityFilterMode.configValue();
        ModConfig.COMMON.endVisionEntityFilter = split(this.endVisionEntityFilter);
        ModConfig.COMMON.dreamySkyTeleportHeight = this.dreamySkyTeleportHeight;
        ModConfig.COMMON.dreamySkyCheckTopBlock = this.dreamySkyCheckTopBlock;
        ModConfig.COMMON.showDreamySkyParticles = this.showDreamySkyParticles;
        ModConfig.COMMON.dreamySkyDisabledDimensions = split(this.dreamySkyDisabledDimensions);
        ModConfig.COMMON.undergroundSunFusionDuration = this.undergroundSunFusionDuration;
        ModConfig.COMMON.fusionBlockExplosionRadius = this.fusionBlockExplosionRadius;
        ModConfig.COMMON.fusionAttackExplosionRadius = this.fusionAttackExplosionRadius;
        ModConfig.COMMON.fusionSelfExplosionRadius = this.fusionSelfExplosionRadius;
        ModConfig.COMMON.fusionWarningSoundMode = this.fusionWarningSoundMode.configValue();
        ModConfig.COMMON.liesRhymeDuration = this.liesRhymeDuration;
        ModConfig.COMMON.sirensDewBuffRadius = this.sirensDewBuffRadius;
        ModConfig.COMMON.liesRhymeEndDebuffRadius = this.liesRhymeEndDebuffRadius;
        ModConfig.COMMON.liesRhymeWarningRangeDisplayTime = this.liesRhymeWarningRangeDisplayTime;
        ModConfig.COMMON.liesRhymeHealthReductionMode = this.liesRhymeHealthReductionMode.configValue();
        ModConfig.COMMON.liesRhymeHealthReductionPercentage = this.liesRhymeHealthReductionPercentage;
        ModConfig.COMMON.liesRhymeHealthReductionAmount = this.liesRhymeHealthReductionAmount;
        ModConfig.COMMON.liesRhymeEndDamage = this.liesRhymeEndDamage;
        ModConfig.CLIENT.endVisionOutlineColor = this.endVisionOutlineColor;
        ModConfig.CLIENT.showFrozenFirstPersonOverlay = this.showFrozenFirstPersonOverlay;
        ModConfig.CLIENT.showFrozenIceBlock = this.showFrozenIceBlock;
        ModConfig.CLIENT.tintFrozenPlayers = this.tintFrozenPlayers;
        ModConfig.CLIENT.magicBottleHudPosition = this.magicBottleHudPosition;
        ModConfig.COMMON.maxMagicBottleCount = this.maxMagicBottleCount;
        ModConfig.COMMON.shakeTime = this.shakeTime;
        ModConfig.COMMON.stirTime = this.stirTime;
        ModConfig.saveAll();
    }

    private static void parseInt(String text, int min, int max, Consumer<Integer> setter) {
        try {
            setter.accept(Math.clamp(Integer.parseInt(text), min, max));
        } catch (NumberFormatException ignored) {
        }
    }

    private static void parseDouble(String text, double min, double max, Consumer<Double> setter) {
        try {
            setter.accept(Math.clamp(Double.parseDouble(text), min, max));
        } catch (NumberFormatException ignored) {
        }
    }

    private static void parseFloat(String text, float min, float max, Consumer<Float> setter) {
        try {
            setter.accept(Math.clamp(Float.parseFloat(text), min, max));
        } catch (NumberFormatException ignored) {
        }
    }

    private static String join(String[] values) {
        return String.join(", ", values);
    }

    private static String[] split(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .toArray(String[]::new);
    }

    private enum Page {
        GLACIER("config.gemlike_teaparty.page.glacier"),
        END_VISION("config.gemlike_teaparty.page.end_vision"),
        DREAMY_SKY("config.gemlike_teaparty.page.dreamy_sky"),
        UNDERGROUND_SUN("config.gemlike_teaparty.page.underground_sun"),
        SIRENS_DEW("config.gemlike_teaparty.page.sirens_dew"),
        OTHER("config.gemlike_teaparty.page.other"),
        CLIENT("config.gemlike_teaparty.page.client");

        private final String key;

        Page(String key) {
            this.key = key;
        }
    }

    private record Label(Component component, int x, int y) {
    }
}
