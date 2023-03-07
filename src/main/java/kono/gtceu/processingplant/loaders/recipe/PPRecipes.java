package kono.gtceu.processingplant.loaders.recipe;

import kono.gtceu.processingplant.loaders.recipe.metatileentities.*;

public class PPRecipes {
    public static void init() {
        PPCasingLoader.init();
        PPMetaTileEntitiesLoader.init();
        OreProcessingPlantLoader.init();
    }
}
