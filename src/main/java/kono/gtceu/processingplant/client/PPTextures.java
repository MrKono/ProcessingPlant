package kono.gtceu.processingplant.client;

import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import static gregtech.client.renderer.texture.cube.OrientedOverlayRenderer.OverlayFace.*;
@Mod.EventBusSubscriber(modid = "oreprocessingplant", value = Side.CLIENT)
public class PPTextures {

    // Controller
    public static OrientedOverlayRenderer ORE_PROCESSING_PLANT;

    // Casing
    public static SimpleOverlayRenderer ORE_PLANT_CASING;

    public static void preInit() {
        //Controller
        ORE_PROCESSING_PLANT= new OrientedOverlayRenderer("multiblock/ore_processing_plant", FRONT);
        //Casing
        ORE_PLANT_CASING = new SimpleOverlayRenderer("casings/metal/ore_plant_casing");
        //Blocks
    }
}
