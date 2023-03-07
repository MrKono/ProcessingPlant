package kono.gtceu.processingplant.common;

import gregtech.api.GregTechAPI;
import gregtech.api.block.VariantItemBlock;
import kono.gtceu.processingplant.api.unification.material.flags.PPFlagAddition;
import kono.gtceu.processingplant.common.metatileentities.PPMetaTileEntities;
import kono.gtceu.processingplant.loaders.recipe.PPRecipes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Function;

import static kono.gtceu.processingplant.common.PPMetaBlocks.*;


@Mod.EventBusSubscriber(modid = "processingplant")
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        PPMetaTileEntities.init();
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(PP_BLOCK_CASING);
        event.getRegistry().register(PP_GLASS_CASING);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(createItemBlock(PP_BLOCK_CASING, VariantItemBlock::new));
        event.getRegistry().register(createItemBlock(PP_GLASS_CASING, VariantItemBlock::new));
    }

    private static <T extends Block> ItemBlock createItemBlock(T block, Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return itemBlock;
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(GregTechAPI.MaterialEvent event) {
        PPFlagAddition.init();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerRecipesRemoval(RegistryEvent.Register<IRecipe> event) {
    }

    @SubscribeEvent()
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event){
        PPRecipes.init();
    }
}
