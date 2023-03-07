package kono.gtceu.processingplant.loaders.recipe;

import gregicality.multiblocks.api.unification.GCYMMaterials;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.stack.UnificationEntry;
import kono.gtceu.processingplant.common.PPBlockCasing;
import kono.gtceu.processingplant.common.PPMetaBlocks;
import kono.gtceu.processingplant.common.PPGlassCasing;

import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.api.unification.material.Materials.*;

public class PPCasingLoader {
    public static void init(){
        //BlockCasing
        ModHandler.addShapedRecipe("casing_ore_plant", PPMetaBlocks.PP_BLOCK_CASING.getItemVariant(PPBlockCasing.MetalCasingType.ORE_PLANT, 2),
                "PhP", "PFP", "PwP",
                'P', new UnificationEntry(plate, RedSteel),
                'F', new UnificationEntry(frameGt, HSSS)
                );
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, RedSteel, 6)
                .input(frameGt, HSSS)
                .circuitMeta(6)
                .outputs(PPMetaBlocks.PP_BLOCK_CASING.getItemVariant(PPBlockCasing.MetalCasingType.ORE_PLANT, 2))
                .EUt(16)
                .duration(50)
                .buildAndRegister();

        //GlassCasing
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, BorosilicateGlass, 6)
                .input(frameGt, GCYMMaterials.WatertightSteel, 2)
                .input(plate, EnderPearl, 2)
                .input(pipeSmallFluid, Polytetrafluoroethylene, 2)
                .input(dust, EnderEye)
                .fluidInputs(DistilledWater.getFluid(2000))
                .outputs(PPMetaBlocks.PP_GLASS_CASING.getItemVariant(PPGlassCasing.CasingType.WATER, 4))
                .EUt(1024)
                .duration(400)
                .buildAndRegister();
    }
}
