package com.lyuurain.teaparty.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ModPatchouliBookProvider implements DataProvider {
    private static final String BOOK_ID = "guide";
    private static final String DEFAULT_LANG = "en_us";
    private final PackOutput packOutput;

    public ModPatchouliBookProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.allOf(
                DataProvider.saveStable(cachedOutput, bookJson(), dataPath("book.json")),
                DataProvider.saveStable(cachedOutput, categoryJson(), assetPath("categories/introduction.json")),
                DataProvider.saveStable(cachedOutput, entryJson(), assetPath("entries/overview.json")),
                DataProvider.saveStable(cachedOutput, modelJson(), modelPath())
        );
    }

    private Path dataPath(String path) {
        return packOutput.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(GemlikeTeaParty.MODID)
                .resolve("patchouli_books")
                .resolve(BOOK_ID)
                .resolve(path);
    }

    private Path assetPath(String path) {
        return packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(GemlikeTeaParty.MODID)
                .resolve("patchouli_books")
                .resolve(BOOK_ID)
                .resolve(DEFAULT_LANG)
                .resolve(path);
    }

    private Path modelPath() {
        return packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(GemlikeTeaParty.MODID)
                .resolve("models")
                .resolve("item")
                .resolve("patchouli_guide.json");
    }

    private static JsonObject bookJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "book.gemlike_teaparty.guide.name");
        json.addProperty("landing_text", "book.gemlike_teaparty.guide.landing_text");
        json.addProperty("subtitle", "book.gemlike_teaparty.guide.subtitle");
        json.addProperty("version", "1");
        json.addProperty("creative_tab", "gemlike_teaparty:gemlike_teaparty");
        json.addProperty("use_resource_pack", true);
        json.addProperty("pamphlet", true);
        json.addProperty("i18n", true);
        json.addProperty("show_progress", false);
        json.addProperty("book_texture", "gemlike_teaparty:textures/gui/patchouli/guide_book.png");
        json.addProperty("model", "gemlike_teaparty:patchouli_guide");
        return json;
    }

    private static JsonObject categoryJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "book.gemlike_teaparty.category.introduction.name");
        json.addProperty("description", "");
        json.addProperty("icon", "gemlike_teaparty:magic_bottle");
        json.addProperty("sortnum", 0);
        return json;
    }

    private static JsonObject entryJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", "book.gemlike_teaparty.entry.overview.name");
        json.addProperty("category", "gemlike_teaparty:introduction");
        json.addProperty("icon", "gemlike_teaparty:dreamy_sky");
        json.addProperty("read_by_default", true);
        json.addProperty("sortnum", 0);

        JsonArray pages = new JsonArray();
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.add("text", translatable("book.gemlike_teaparty.entry.overview.text"));
        pages.add(page);
        json.add("pages", pages);
        return json;
    }

    private static JsonObject modelJson() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "gemlike_teaparty:item/patchouli_guide");
        json.add("textures", textures);
        return json;
    }

    private static JsonObject translatable(String key) {
        JsonObject json = new JsonObject();
        json.addProperty("translate", key);
        return json;
    }

    @Override
    public String getName() {
        return "Gem-like Tea Party Patchouli Books";
    }
}
