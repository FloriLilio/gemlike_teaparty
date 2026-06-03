package com.lyuurain.teaparty.config;

import com.lyuurain.teaparty.network.EndVisionConfigPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EndVisionConfigValues {
    private static EndVisionConfigPayload syncedConfig = EndVisionConfigPayload.fromCommonConfig();

    public static EndVisionConfigPayload syncedConfig() {
        return syncedConfig;
    }

    public static void update(EndVisionConfigPayload payload) {
        syncedConfig = payload;
    }

    public static boolean isBlockVisible(BlockState blockState) {
        EndVisionConfigPayload config = syncedConfig;
        EndVisionFilterMode mode = EndVisionFilterMode.fromConfig(config.blockFilterMode(), EndVisionFilterMode.WHITELIST);
        boolean listed = isBlockListed(blockState.getBlock(), config.blockFilter());
        return mode == EndVisionFilterMode.WHITELIST ? listed : !listed;
    }

    public static boolean isEntityVisible(EntityType<?> entityType) {
        EndVisionConfigPayload config = syncedConfig;

        if (!config.showEntities()) {
            return false;
        }

        EndVisionFilterMode mode = EndVisionFilterMode.fromConfig(config.entityFilterMode(), EndVisionFilterMode.BLACKLIST);
        boolean listed = ConfigValues.isEntityListed(entityType, config.entityFilter());
        return mode == EndVisionFilterMode.WHITELIST ? listed : !listed;
    }

    private static boolean isBlockListed(Block block, String[] blocks) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

        for (String configuredBlock : blocks) {
            ResourceLocation configuredId = ResourceLocation.tryParse(configuredBlock);

            if (blockId.equals(configuredId)) {
                return true;
            }
        }

        return false;
    }
}
