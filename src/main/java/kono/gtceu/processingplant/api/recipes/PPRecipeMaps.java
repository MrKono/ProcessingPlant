package kono.gtceu.processingplant.api.recipes;

import crafttweaker.annotations.ZenRegister;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenExpansion("mods.gregtech.recipe.RecipeMaps")
@ZenRegister
public class PPRecipeMaps {

    @ZenProperty
    public static final RecipeMap<SimpleRecipeBuilder> ORE_PROCESSING_PLANT = new RecipeMap<>("ore_processing_plant",
            1, 2,
            1,16,
            0, 1,
            0,1,
            new SimpleRecipeBuilder(), false)
            .setSlotOverlay(false, false, false, GuiTextures.CRUSHED_ORE_OVERLAY)
            .setSlotOverlay(false, false, true, GuiTextures.INT_CIRCUIT_OVERLAY)
            .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
            .setSlotOverlay(false, true, GuiTextures.BEAKER_OVERLAY_1)
            .setSlotOverlay(true, true, GuiTextures.BEAKER_OVERLAY_2)
            .setSound(GTSoundEvents.MINER)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MACERATE, ProgressWidget.MoveType.HORIZONTAL);
}
