package kono.gtceu.processingplant.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static gregtech.common.blocks.MetaBlocks.statePropertiesToString;

public class PPMetaBlocks {

    public static final PPBlockCasing PP_BLOCK_CASING = new PPBlockCasing();
    public static final PPGlassCasing PP_GLASS_CASING = new PPGlassCasing();

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(PP_BLOCK_CASING);
        PP_GLASS_CASING.onModelRegister();
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            statePropertiesToString(state.getProperties())));
        }
    }
}
