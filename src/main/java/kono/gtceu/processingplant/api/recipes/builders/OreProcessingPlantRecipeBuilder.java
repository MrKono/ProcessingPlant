package kono.gtceu.processingplant.api.recipes.builders;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.EnumValidationResult;
import gregtech.api.util.GTLog;
import kono.gtceu.processingplant.api.recipes.recipeproperties.RecipeTypeProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;

public class OreProcessingPlantRecipeBuilder extends RecipeBuilder<OreProcessingPlantRecipeBuilder> {

    private int type;
    public OreProcessingPlantRecipeBuilder() {
    }

    public OreProcessingPlantRecipeBuilder(Recipe recipe, RecipeMap<OreProcessingPlantRecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public OreProcessingPlantRecipeBuilder(RecipeBuilder<OreProcessingPlantRecipeBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public OreProcessingPlantRecipeBuilder copy() {
        return new OreProcessingPlantRecipeBuilder(this);
    }

    public OreProcessingPlantRecipeBuilder setType(int recipeType) {
        this.type = recipeType;
        this.applyProperty(RecipeTypeProperty.getInstance(), recipeType);
        return this;
    }

    @Override
    public boolean applyProperty(@Nonnull String key, Object value) {
        if (key.equals(RecipeTypeProperty.KEY)) {
            this.ProceccingType(((Number) value).intValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    public OreProcessingPlantRecipeBuilder ProceccingType(int recipeType) {
        if (recipeType < 0) {
            GTLog.logger.error("Recipe Type cannot be less than 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.applyProperty(RecipeTypeProperty.getInstance(), recipeType);
        return this;
    }

    public int getRecipeType() {
        return this.recipePropertyStorage == null ? -1 :
                this.recipePropertyStorage.getRecipePropertyValue(RecipeTypeProperty.getInstance(), -1);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(RecipeTypeProperty.getInstance().getKey(), getRecipeType())
                .toString();
    }
}
