package kono.gtceu.processingplant.loaders.recipe.metatileentities;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import kono.gtceu.processingplant.api.recipes.PPRecipeMaps;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

import static gregtech.api.GTValues.LV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.info.MaterialFlags.*;

public class OreProcessingPlantLoader {
    private static final List<OrePrefix> oreType = ConfigHolder.worldgen.allUniqueStoneTypes ? Arrays.asList(
            OrePrefix.ore, OrePrefix.oreNetherrack, OrePrefix.oreEndstone, OrePrefix.oreGranite, OrePrefix.oreDiorite,
            OrePrefix.oreAndesite, OrePrefix.oreBasalt, OrePrefix.oreBlackgranite, OrePrefix.oreMarble, OrePrefix.oreRedgranite,
            OrePrefix.oreSand, OrePrefix.oreRedSand) : Arrays.asList(OrePrefix.ore, OrePrefix.oreNetherrack, OrePrefix.oreEndstone);

    public static void init() {
        for (OrePrefix orePrefix : oreType) {
        orePrefix.addProcessingHandler(PropertyKey.ORE, OreProcessingPlantLoader::register);
        }
    }

    public static void register(OrePrefix orePrefix, Material material, OreProperty property){
        //==Byproduct List==
        //NO.1
        //oreOre -> oreCrushed
        //oreCrushed -> dustImpure
        //dustImpure -> dust
        Material byproductCrush = GTUtility.selectItemInList(0, material, property.getOreByProducts(), Material.class);
        //No.2
        //oreCrushed -> oreCrushedPurified by Washer
        //oreCrushed -> oreCrushedCentrifuged
        //oreCrushedPurified -> oreCrushedCentrifuged or dustPure
        //dustPure -> dust
        Material byproductPure = GTUtility.selectItemInList(1, material, property.getOreByProducts(), Material.class);
        //No.3
        //oreCrushedCentrifuged -> dust
        Material byproductThermal = GTUtility.selectItemInList(2, material, property.getOreByProducts(), Material.class);
        //No.4
        //oreCrushed -> oreCrushedPurified by Chemical Bath
        Material byproductBath = GTUtility.selectItemInList(3, material, property.getOreByProducts(), Material.class);
        //==END==
        //==Output==
        //Ore crushing byproduct (gem)
        ItemStack byproductStack = OreDictUnifier.get(OrePrefix.gem, byproductCrush);
        //Ore crushing byproduct (dust)
        if (byproductStack.isEmpty()) byproductStack = OreDictUnifier.get(OrePrefix.dust, byproductCrush);
        ItemStack dustStack = OreDictUnifier.get(OrePrefix.dust, material);
        //oreType multiplier and output multiplier
        int oreTypeMultiplier = orePrefix == OrePrefix.oreNetherrack || orePrefix == OrePrefix.oreEndstone ? 2 : 1;
        double amountOfCrushedOre = property.getOreMultiplier();
        int outputMultiplier =  (int) Math.round(amountOfCrushedOre) * 2 * oreTypeMultiplier;

        processCWCC(orePrefix, material, byproductCrush, byproductPure, byproductStack, dustStack, outputMultiplier);//crush,wash, crush,centrifuge
        processCWTC(orePrefix, material, byproductCrush,  byproductThermal, byproductStack, dustStack, outputMultiplier);//crush, wash, thermal, crush
        processCTC(orePrefix, material, property, byproductCrush, byproductThermal, byproductStack, dustStack, outputMultiplier);//crush, thermal, crush
        processCCC(orePrefix, material, property, byproductCrush, byproductStack, dustStack, outputMultiplier);//crush, crush, centrifuge
        processCBCC(orePrefix, material, property, byproductCrush, byproductPure, byproductBath, byproductStack, dustStack, outputMultiplier);//crush, wash or bath, crush, centrifuge
        processCBTC(orePrefix, material, property, byproductCrush, byproductBath, byproductThermal, byproductStack, dustStack, outputMultiplier);//crush, wash or bath, thermal, crush
        processCWCCS(orePrefix, material, byproductCrush, byproductPure, byproductStack, dustStack, outputMultiplier);//crush, wash, crush, centrifuge + sifting
        processCWTCS(orePrefix, material, byproductCrush, byproductThermal, byproductStack, dustStack, outputMultiplier);//crush, wash, thermal, crush + sifting
        processCBCCS(orePrefix, material, property, byproductCrush, byproductPure, byproductBath, byproductStack, dustStack, outputMultiplier);//crush, wash or bath ,crush, centrifuge + sifting
        processCBTCS(orePrefix, material, property, byproductCrush, byproductBath, byproductThermal, byproductStack, dustStack, outputMultiplier);//crush, wash or bath, thermal, crush + sifting
    }

    //All the same process
    //crush,wash, crush,centrifuge -0
    public static void processCWCC(OrePrefix orePrefix, Material material, Material byproductCrush, Material byproductPure, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(0)
                    .input(orePrefix, material)
                    .outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductPure, outputMultiplier)) //centrifuge
                    .chancedOutput(orePrefix.dust, byproductPure, outputMultiplier, 1400, 850) //crushPurified
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                            OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier))//wash
                    .chancedOutput(byproductStack, 1400, 850)//crushOre
                    .EUt(VA[LV])
                    .duration(1200);
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800);//crush
                }
            }
            builder.buildAndRegister();
        }
    }

    //crush, wash, thermal, crush -1
    public static void processCWTC(OrePrefix orePrefix, Material material, Material byproductCrush, Material byproductThermal, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier){
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(1)
                    .input(orePrefix, material)
                    .outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                    .chancedOutput(OrePrefix.dust, byproductThermal, outputMultiplier, 1400, 850) //crushCentrifuged
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier)) //ThermalCentrifuge
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                            OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier))//wash byproduct
                    .chancedOutput(byproductStack, 1400, 850)//crushOre
                    .EUt(VA[LV])
                    .duration(1200);
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

    //crush, thermal, crush -2
    public static void processCTC(OrePrefix orePrefix, Material material, OreProperty property, Material byproductCrush, Material byproductThermal, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(2)
                    .input(orePrefix, material)
                    .outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                    .chancedOutput(OrePrefix.dust, byproductThermal, outputMultiplier, 1400, 850) //crushCentrifuged
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, property.getByProductMultiplier() * outputMultiplier),
                            OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)) //ThermalCentrifuge
                    .chancedOutput(byproductStack, 1400, 850)//crushOre
                    .EUt(VA[LV])
                    .duration(1200);
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

    //crush, crush, centrifuge -3
    public static void processCCC(OrePrefix orePrefix, Material material, OreProperty property, Material byproductCrush, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(3)
                    .input(orePrefix, material)
                    .outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier));//main
            if (byproductCrush.hasProperty(PropertyKey.DUST)) {//centrifuge
                builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush));
            } else {
                builder.fluidOutputs(byproductCrush.getFluid(GTValues.L / 9));
            }
            builder.chancedOutput(orePrefix.dust, byproductCrush, property.getByProductMultiplier() * outputMultiplier, 1400, 850) //crush
                    .chancedOutput(byproductStack, 1400, 850)//crushOre
                    .EUt(VA[LV])
                    .duration(1200);
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800);//crush
                }
            }
            builder.buildAndRegister();
        }
    }

    //add Bath process
    //crush, wash or bath, crush, centrifuge -4
    public static void processCBCC(OrePrefix orePrefix, Material material, OreProperty property, Material byproductCrush, Material byproductPure, Material byproductBath, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(4)
                    .input(orePrefix, material)
                    .outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductPure, outputMultiplier)) //centrifuge
                    .chancedOutput(orePrefix.dust, byproductPure, outputMultiplier, 1400, 850) //crushPurified
                    .EUt(VA[LV])
                    .duration(1200);
            if (property.getWashedIn().getKey() != null) { //Bath process
                builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * outputMultiplier))
                        .chancedOutput(orePrefix.dust, byproductBath, property.getByProductMultiplier() * outputMultiplier, 7000, 500)
                        .chancedOutput(orePrefix.dust, Materials.Stone, outputMultiplier, 4000, 650);
            } else {
                builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                                OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)); //wash
            }
            builder.chancedOutput(byproductStack, 1400, 850); //crushOre
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

    //crush, wash or bath, thermal, crush -5
    public static void processCBTC(OrePrefix orePrefix, Material material, OreProperty property, Material byproductCrush, Material byproductBath, Material byproductThermal, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(5)
                    .input(orePrefix, material)
                    .outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                    .chancedOutput(OrePrefix.dust, byproductThermal, outputMultiplier, 1400, 850) //crushCentrifuged
                    .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, property.getByProductMultiplier() * outputMultiplier),
                            OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)) //ThermalCentrifuge
                    .EUt(VA[LV])
                    .duration(1200);
            if (property.getWashedIn().getKey() != null) { //Bath process
                builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * outputMultiplier))
                        .chancedOutput(orePrefix.dust, byproductBath, property.getByProductMultiplier() * outputMultiplier, 7000, 500)
                        .chancedOutput(orePrefix.dust, Materials.Stone, outputMultiplier, 4000, 650);
            } else {
                builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                        OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)); //wash
            }
            builder.chancedOutput(byproductStack, 1400, 850); //crushOre
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

    //add sifting process
    //crush, wash, crush, centrifuge + sifting -6
    public static void processCWCCS(OrePrefix orePrefix, Material material, Material byproductCrush, Material byproductPure, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(6)
                    .input(orePrefix, material);
            //gem output
            if (material.hasProperty(PropertyKey.GEM)) {
                ItemStack flawedStack = OreDictUnifier.get(OrePrefix.gemFlawed, material);
                ItemStack chippedStack = OreDictUnifier.get(OrePrefix.gemChipped, material);
                if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 500, 150)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1500, 2000)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 5000, 1000)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 2500, 500);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2000, 500);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3000, 350);
                } else {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 300, 100)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1000, 150)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 3500, 500)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 5000, 750);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2500, 300);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3500, 400);
                }
            } else {
                //normal output
                    builder.outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                        .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductPure, outputMultiplier)) //centrifuge
                        .chancedOutput(orePrefix.dust, byproductPure, outputMultiplier, 1400, 850); //crushPurified
            }
            //same output
                    builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                            OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier))//wash
                    .chancedOutput(byproductStack, 1400, 850)//crushOre
                    .EUt(VA[LV])
                    .duration(1200);
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800);//crush
                }
            }
            builder.buildAndRegister();
        }
    }

    //crush, wash, thermal, crush + sifting -7
    public static void processCWTCS(OrePrefix orePrefix, Material material, Material byproductCrush, Material byproductThermal, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier){
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(7)
                    .input(orePrefix, material);
            //gem output
            if (material.hasProperty(PropertyKey.GEM)) {
                ItemStack flawedStack = OreDictUnifier.get(OrePrefix.gemFlawed, material);
                ItemStack chippedStack = OreDictUnifier.get(OrePrefix.gemChipped, material);
                if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 500, 150)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1500, 2000)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 5000, 1000)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 2500, 500);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2000, 500);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3000, 350);
                } else {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 300, 100)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1000, 150)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 3500, 500)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 5000, 750);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2500, 300);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3500, 400);
                }
            } else {
                //normal output
                builder.outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                        .chancedOutput(OrePrefix.dust, byproductThermal, outputMultiplier, 1400, 850) //crushCentrifuged
                        .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier)); //ThermalCentrifuge
            }
            //same output
                    builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                            OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier))//wash byproduct
                    .chancedOutput(byproductStack, 1400, 850)//crushOre
                    .EUt(VA[LV])
                    .duration(1200);
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

    //add sifting or bath process
    //crush, wash or bath, crush, centrifuge + sifting -8
    public static void processCBCCS(OrePrefix orePrefix, Material material, OreProperty property, Material byproductCrush, Material byproductPure, Material byproductBath, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(8)
                    .input(orePrefix, material);
            //gem output
            if (material.hasProperty(PropertyKey.GEM)) {
                ItemStack flawedStack = OreDictUnifier.get(OrePrefix.gemFlawed, material);
                ItemStack chippedStack = OreDictUnifier.get(OrePrefix.gemChipped, material);
                if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 500, 150)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1500, 2000)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 5000, 1000)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 2500, 500);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2000, 500);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3000, 350);
                } else {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 300, 100)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1000, 150)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 3500, 500)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 5000, 750);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2500, 300);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3500, 400);
                }
            } else {
                //normal output
                builder.outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                        .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductPure, outputMultiplier));//centrifuge
            }
            //same output
            builder.chancedOutput(orePrefix.dust, byproductPure, outputMultiplier, 1400, 850) //crushPurified
                    .EUt(VA[LV])
                    .duration(1200);
            if (property.getWashedIn().getKey() != null) { //Bath process
                builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * outputMultiplier))
                        .chancedOutput(orePrefix.dust, byproductBath, property.getByProductMultiplier() * outputMultiplier, 7000, 500)
                        .chancedOutput(orePrefix.dust, Materials.Stone, outputMultiplier, 4000, 650);
            } else {
                builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                        OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)); //wash
            }
            builder.chancedOutput(byproductStack, 1400, 850); //crushOre
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

    //crush, wash or bath, thermal, crush + sifting -9
    public static void processCBTCS(OrePrefix orePrefix, Material material, OreProperty property, Material byproductCrush, Material byproductBath, Material byproductThermal, ItemStack byproductStack, ItemStack dustStack, int outputMultiplier) {
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        if (!dustStack.isEmpty()) {
            RecipeBuilder<?> builder = PPRecipeMaps.ORE_PROCESSING_PLANT.recipeBuilder()
                    .circuitMeta(9)
                    .input(orePrefix, material);
            //gem output
            if (material.hasProperty(PropertyKey.GEM)) {
                ItemStack flawedStack = OreDictUnifier.get(OrePrefix.gemFlawed, material);
                ItemStack chippedStack = OreDictUnifier.get(OrePrefix.gemChipped, material);
                if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 500, 150)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1500, 2000)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 5000, 1000)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 2500, 500);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, outputMultiplier, 2000, 500);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3000, 350);
                } else {
                    builder.chancedOutput(OrePrefix.gemExquisite, material, outputMultiplier, 300, 100)
                            .chancedOutput(OrePrefix.gemFlawless, material, outputMultiplier, 1000, 150)
                            .chancedOutput(OrePrefix.gem, material, outputMultiplier, 3500, 500)
                            .chancedOutput(OrePrefix.dustPure, material, outputMultiplier, 5000, 750);
                    if (!flawedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemFlawed, material, 2500, outputMultiplier, 300);
                    if (!chippedStack.isEmpty())
                        builder.chancedOutput(OrePrefix.gemChipped, material, outputMultiplier, 3500, 400);
                }
            } else {
                //normal output
                builder.outputs(OreDictUnifier.get(OrePrefix.dust, material, outputMultiplier)) //main
                        .chancedOutput(OrePrefix.dust, byproductThermal, outputMultiplier, 1400, 850) //crushCentrifuged
                        .outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, property.getByProductMultiplier() * outputMultiplier),
                                OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)); //ThermalCentrifuge
            }
            //same output
            builder.EUt(VA[LV])
                    .duration(1200);
            if (property.getWashedIn().getKey() != null) { //Bath process
                builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * outputMultiplier))
                        .chancedOutput(orePrefix.dust, byproductBath, property.getByProductMultiplier() * outputMultiplier, 7000, 500)
                        .chancedOutput(orePrefix.dust, Materials.Stone, outputMultiplier, 4000, 650);
            } else {
                builder.outputs(OreDictUnifier.get(OrePrefix.dustTiny, byproductCrush, 3 * outputMultiplier),
                        OreDictUnifier.get(OrePrefix.dust, Materials.Stone, outputMultiplier)); //wash
            }
            builder.chancedOutput(byproductStack, 1400, 850); //crushOre
            for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
                if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                    ItemStack dustStackByproduct = OreDictUnifier.getGem(secondaryMaterial);
                    builder.chancedOutput(dustStackByproduct, 6700, 800); //crushOre
                }
            }
            builder.buildAndRegister();
        }
    }

}
