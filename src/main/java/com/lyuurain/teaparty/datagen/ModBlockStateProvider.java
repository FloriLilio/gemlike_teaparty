package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.LemonCropBlock;
import com.lyuurain.teaparty.block.RedGrapeVineBlock;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    private final ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, GemlikeTeaParty.MODID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        registerLemonCrop();
        registerRedGrapeVine();
    }

    private void registerLemonCrop() {
        ModelFile[] stageModels = new ModelFile[3];
        for (int age = 0; age <= 2; age++) {
            String name = "lemon_crop_stage" + age;
            String texture = "block/" + name;
            trackGeneratedTexture(texture);
            stageModels[age] = models().cross(name, modLoc(texture)).renderType("minecraft:cutout");
        }
        getVariantBuilder(ModBlocks.LEMON_CROP.get()).forAllStates(state -> new ConfiguredModel[] {
                new ConfiguredModel(stageModels[state.getValue(LemonCropBlock.AGE)])
        });
    }

    private void registerRedGrapeVine() {
        ModelFile[] hangingModels = new ModelFile[4];
        ModelFile[] wallModels = new ModelFile[4];
        for (int age = 0; age <= 3; age++) {
            String hangingName = "red_grape_vine_hanging_stage" + age;
            String wallName = "red_grape_vine_wall_stage" + age;
            String hangingTexture = "block/" + hangingName;
            String wallTexture = "block/" + wallName;
            trackGeneratedTexture(hangingTexture);
            trackGeneratedTexture(wallTexture);
            hangingModels[age] = models().cross(hangingName, modLoc(hangingTexture)).renderType("minecraft:cutout");
            wallModels[age] = models().withExistingParent(wallName, mcLoc("block/vine"))
                    .texture("vine", modLoc(wallTexture))
                    .texture("particle", modLoc(wallTexture))
                    .renderType("minecraft:cutout");
        }
        getVariantBuilder(ModBlocks.RED_GRAPE_VINE.get()).forAllStates(state -> {
            int age = state.getValue(RedGrapeVineBlock.AGE);
            if (state.getValue(RedGrapeVineBlock.FORM) == RedGrapeVineBlock.Form.HANGING) {
                return new ConfiguredModel[] {new ConfiguredModel(hangingModels[age])};
            }
            return new ConfiguredModel[] {new ConfiguredModel(wallModels[age], 0, wallRotationY(state.getValue(RedGrapeVineBlock.FACING)), true)};
        });
        trackGeneratedTexture("item/red_grape_seeds");
        itemModels().basicItem(ModItems.RED_GRAPE_SEEDS.get());
    }

    private void trackGeneratedTexture(String path) {
        this.existingFileHelper.trackGenerated(modLoc(path), ModelProvider.TEXTURE);
    }

    private int wallRotationY(Direction facing) {
        return switch (facing) {
            case SOUTH -> 0;
            case WEST -> 90;
            case NORTH -> 180;
            default -> 270;
        };
    }
}
