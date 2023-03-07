package kono.gtceu.processingplant.common.metatileentities;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

public class PPMetaTileEntities {

    public static MetaTileEntityOreProcessingPlant ORE_PROCESSING_PLANT;

    public static void init() {
        ORE_PROCESSING_PLANT = registerMetaTileEntity(21000, new MetaTileEntityOreProcessingPlant(ppId("ore_processing_plant")));
    }

    @Nonnull
    private static ResourceLocation ppId(String name) {
        return new ResourceLocation("processingplant", name);
    }
}
