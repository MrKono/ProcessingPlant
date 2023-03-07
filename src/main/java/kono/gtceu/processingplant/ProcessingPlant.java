package kono.gtceu.processingplant;

import gregtech.api.GTValues;
import kono.gtceu.processingplant.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid= "processingplant",
        name = "Processing Plant",
        acceptedMinecraftVersions = "[1.12, 1.12.2]",
        dependencies = GTValues.MOD_VERSION_DEP)

public class ProcessingPlant {
    @SidedProxy(modId = "processingplant", clientSide = "kono.gtceu.processingplant.client.ClientProxy", serverSide = "kono.gtceu.processingplant.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static ProcessingPlant instance;

    public static Logger logger;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}