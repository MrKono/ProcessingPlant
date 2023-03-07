package kono.gtceu.processingplant.client;

import kono.gtceu.processingplant.common.CommonProxy;
import kono.gtceu.processingplant.common.PPMetaBlocks;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        PPTextures.preInit();
    }
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        PPMetaBlocks.registerItemModels();
    }

}
