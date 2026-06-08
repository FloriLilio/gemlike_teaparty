package com.lyuurain.teaparty.registry;

import com.lyuurain.teaparty.GemlikeTeaParty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    private ModTags() {
    }

    public static final class Blocks {
        public static final TagKey<Block> TEAPOT_HEAT_SOURCES = blockTag("teapot_heat_sources");

        private Blocks() {
        }

        private static TagKey<Block> blockTag(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, path));
        }
    }

    public static final class Items {
        public static final TagKey<Item> ADVANCED_DRINKS = itemTag("advanced_drinks");

        private Items() {
        }

        private static TagKey<Item> itemTag(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(GemlikeTeaParty.MODID, path));
        }
    }
}
