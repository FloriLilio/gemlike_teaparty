package com.lyuurain.teaparty.datagen;

import com.lyuurain.teaparty.GemlikeTeaParty;
import com.lyuurain.teaparty.block.AdvancedDrinkBlock;
import com.lyuurain.teaparty.block.LemonCropBlock;
import com.lyuurain.teaparty.block.RedGrapeVineBlock;
import com.lyuurain.teaparty.block.TeaCropBlock;
import com.lyuurain.teaparty.block.TeapotBlock;
import com.lyuurain.teaparty.registry.ModBlocks;
import com.lyuurain.teaparty.registry.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
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
        registerTeaCrop();
        registerRedGrapeVine();
        registerTeapot();
        registerAdvancedDrinkBlocks();
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

    private void registerTeaCrop() {
        ModelFile[] stageModels = new ModelFile[3];
        for (int age = 0; age <= 2; age++) {
            String name = "tea_crop_stage" + age;
            String texture = "block/" + name;
            trackGeneratedTexture(texture);
            stageModels[age] = models().cross(name, modLoc(texture)).renderType("minecraft:cutout");
        }
        getVariantBuilder(ModBlocks.TEA_CROP.get()).forAllStates(state -> new ConfiguredModel[] {
                new ConfiguredModel(stageModels[state.getValue(TeaCropBlock.AGE)])
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

    private void registerTeapot() {
        getVariantBuilder(ModBlocks.TEAPOT.get()).forAllStates(state -> {
            int active = state.getValue(TeapotBlock.ACTIVE);
            String modelName;
            if (active > 0) {
                modelName = "teapot_active_" + active;
            } else {
                boolean opened = state.getValue(TeapotBlock.OPENED);
                modelName = switch (state.getValue(TeapotBlock.FORM)) {
                    case CHAIN -> opened ? "teapot_chain_open" : "teapot_chain";
                    case BASE -> opened ? "teapot_base_open" : "teapot_base";
                    default -> opened ? "teapot_open" : "teapot";
                };
            }
            ModelFile model = new ModelFile.UncheckedModelFile(modLoc("block/" + modelName));
            return new ConfiguredModel[] {
                    new ConfiguredModel(model, 0, horizontalRotationY(state.getValue(TeapotBlock.FACING)), false)
            };
        });
        itemModels().getBuilder("teapot").parent(new ModelFile.UncheckedModelFile(modLoc("block/teapot")));
    }

    private void registerAdvancedDrinkBlocks() {
        registerAdvancedDrinkBlock(ModBlocks.GLACIER.get(), "glacier");
        registerAdvancedDrinkBlock(ModBlocks.END_VISION.get(), "end_vision");
        registerAdvancedDrinkBlock(ModBlocks.DREAMY_SKY.get(), "dreamy_sky");
        registerAdvancedDrinkBlock(ModBlocks.SIRENS_DEW.get(), "sirens_dew");
        registerAdvancedDrinkBlock(ModBlocks.UNDERGROUND_SUN.get(), "underground_sun");
    }

    private void registerAdvancedDrinkBlock(Block block, String name) {
        ModelFile model = new ModelFile.UncheckedModelFile(modLoc("block/" + name));
        getVariantBuilder(block).forAllStates(state -> new ConfiguredModel[] {
                new ConfiguredModel(model, 0, horizontalRotationY(state.getValue(AdvancedDrinkBlock.FACING)), false)
        });
    }

    private void trackGeneratedTexture(String path) {
        this.existingFileHelper.trackGenerated(modLoc(path), ModelProvider.TEXTURE);
    }

    private int horizontalRotationY(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
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
