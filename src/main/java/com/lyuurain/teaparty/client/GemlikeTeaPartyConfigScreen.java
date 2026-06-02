package com.lyuurain.teaparty.client;

import com.lyuurain.teaparty.config.ModConfig;
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
    private final Screen parent;
    private final List<Label> labels = new ArrayList<>();
    private Page page = Page.GLACIER;
    private int glacierPage;
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
    private int dreamySkyTeleportHeight;
    private boolean dreamySkyCheckTopBlock;
    private boolean showDreamySkyParticles;
    private String dreamySkyDisabledDimensions;
    private boolean showFrozenFirstPersonOverlay;
    private boolean showFrozenIceBlock;
    private boolean tintFrozenPlayers;

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
        addGlacierPageButtons();
        addPageFields();
        addBottomButtons();
    }

    private void addPageButtons() {
        int buttonWidth = 98;
        int gap = 4;
        int totalWidth = buttonWidth * Page.values().length + gap * (Page.values().length - 1);
        int x = (this.width - totalWidth) / 2;

        for (Page pageValue : Page.values()) {
            addRenderableWidget(Button.builder(Component.translatable(pageValue.key), button -> {
                this.page = pageValue;
                rebuildPage();
            }).bounds(x, 34, buttonWidth, 20).build());
            x += buttonWidth + gap;
        }
    }

    private void addGlacierPageButtons() {
        if (this.page != Page.GLACIER) {
            return;
        }

        addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            this.glacierPage = Math.floorMod(this.glacierPage - 1, GLACIER_PAGE_COUNT);
            rebuildPage();
        }).bounds(this.width / 2 - 75, 56, 24, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("config.gemlike_teaparty.subpage", this.glacierPage + 1, GLACIER_PAGE_COUNT), button -> {
        }).bounds(this.width / 2 - 47, 56, 94, 20).build()).active = false;
        addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            this.glacierPage = Math.floorMod(this.glacierPage + 1, GLACIER_PAGE_COUNT);
            rebuildPage();
        }).bounds(this.width / 2 + 51, 56, 24, 20).build());
    }

    private void addPageFields() {
        int labelX = Math.max(20, this.width / 2 - 230);
        int widgetX = this.width / 2 + 40;
        int y = this.page == Page.GLACIER ? 84 : 68;
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
            case DREAMY_SKY -> {
                addIntField(labelX, widgetX, y, "config.gemlike_teaparty.dreamy_sky_teleport_height", this.dreamySkyTeleportHeight, -64, 1024, value -> this.dreamySkyTeleportHeight = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.dreamy_sky_check_top_block", this.dreamySkyCheckTopBlock, value -> this.dreamySkyCheckTopBlock = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_dreamy_sky_particles", this.showDreamySkyParticles, value -> this.showDreamySkyParticles = value);
                y += rowHeight;
                addStringField(labelX, widgetX, y, "config.gemlike_teaparty.dreamy_sky_disabled_dimensions", this.dreamySkyDisabledDimensions, value -> this.dreamySkyDisabledDimensions = value);
            }
            case CLIENT -> {
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_frozen_first_person_overlay", this.showFrozenFirstPersonOverlay, value -> this.showFrozenFirstPersonOverlay = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.show_frozen_ice_block", this.showFrozenIceBlock, value -> this.showFrozenIceBlock = value);
                y += rowHeight;
                addBooleanField(labelX, widgetX, y, "config.gemlike_teaparty.tint_frozen_players", this.tintFrozenPlayers, value -> this.tintFrozenPlayers = value);
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
        this.dreamySkyTeleportHeight = ModConfig.COMMON.dreamySkyTeleportHeight;
        this.dreamySkyCheckTopBlock = ModConfig.COMMON.dreamySkyCheckTopBlock;
        this.showDreamySkyParticles = ModConfig.COMMON.showDreamySkyParticles;
        this.dreamySkyDisabledDimensions = join(ModConfig.COMMON.dreamySkyDisabledDimensions);
        this.showFrozenFirstPersonOverlay = ModConfig.CLIENT.showFrozenFirstPersonOverlay;
        this.showFrozenIceBlock = ModConfig.CLIENT.showFrozenIceBlock;
        this.tintFrozenPlayers = ModConfig.CLIENT.tintFrozenPlayers;
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
        ModConfig.COMMON.dreamySkyTeleportHeight = this.dreamySkyTeleportHeight;
        ModConfig.COMMON.dreamySkyCheckTopBlock = this.dreamySkyCheckTopBlock;
        ModConfig.COMMON.showDreamySkyParticles = this.showDreamySkyParticles;
        ModConfig.COMMON.dreamySkyDisabledDimensions = split(this.dreamySkyDisabledDimensions);
        ModConfig.CLIENT.showFrozenFirstPersonOverlay = this.showFrozenFirstPersonOverlay;
        ModConfig.CLIENT.showFrozenIceBlock = this.showFrozenIceBlock;
        ModConfig.CLIENT.tintFrozenPlayers = this.tintFrozenPlayers;
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
        DREAMY_SKY("config.gemlike_teaparty.page.dreamy_sky"),
        CLIENT("config.gemlike_teaparty.page.client");

        private final String key;

        Page(String key) {
            this.key = key;
        }
    }

    private record Label(Component component, int x, int y) {
    }
}
