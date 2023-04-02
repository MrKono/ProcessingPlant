package kono.gtceu.processingplant.api.recipes.recipeproperties;

import gregtech.api.recipes.recipeproperties.RecipeProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class RecipeTypeProperty extends RecipeProperty<Integer> {
    public static final String KEY = "recipeType";

    //public static final TreeMap

    private static RecipeTypeProperty INSTANCE;

    private RecipeTypeProperty() {
        super(KEY, Integer.class);
    }

    public static RecipeProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RecipeTypeProperty();
        }
        return INSTANCE;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format("processingplant.recipe.type",
                value), x, y, color);
    }

}
