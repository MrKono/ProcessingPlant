package kono.gtceu.processingplant.common.metatileentities;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import kono.gtceu.processingplant.api.recipes.PPRecipeMaps;
import kono.gtceu.processingplant.client.PPTextures;
import kono.gtceu.processingplant.common.PPBlockCasing;
import kono.gtceu.processingplant.common.PPGlassCasing;
import kono.gtceu.processingplant.common.PPMetaBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MetaTileEntityOreProcessingPlant extends RecipeMapMultiblockController {

    public MetaTileEntityOreProcessingPlant(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PPRecipeMaps.ORE_PROCESSING_PLANT);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityOreProcessingPlant(metaTileEntityId);
    }

   @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXXXXXX", "XXXXXXXXX", "XHHHHHHHX", "XXXXXXXXX", "XTTTTTTTX", "XXXXXXXXX", "XXXXXXXXX","XXXXXXXXX")
                .aisle("XXXXXXXXX", "XP     PX", "HP GGG PH", "XP     PX", "TP WWW PT", "XP     PX", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X       X", "H GGGGG H", "X       X", "T WWWWW T", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X       X", "HGGGGGGGH", "X       X", "TWWWWWWWT", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X   P   X", "HGGGPGGGH", "X   P   X", "TWWWPWWWT", "X   P   X", "XCCCPCCCX", "XFFFMFFFX")
                .aisle("XXXXXXXXX", "X       X", "HGGGGGGGH", "X       X", "TWWWWWWWT", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X       X", "H GGGGG H", "X       X", "T WWWWW T", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "XP     PX", "HP GGG PH", "XP     PX", "TP WWW PT", "XP     PX", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "XXXXSXXXX", "XHHHHHHHX", "XXXXXXXXX", "XTTTTTTTX", "XXXXXXXXX", "XXXXXXXXX", "XXXXXXXXX")
                .where('S', selfPredicate())
                .where('X', states(PPMetaBlocks.PP_BLOCK_CASING.getState(PPBlockCasing.MetalCasingType.ORE_PLANT)).setMinGlobalLimited(200)
                        .or(autoAbilities(true, true, true, true, true, false, false)))
                .where('P', states(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE)))
                .where('H', states(GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT)))
                .where('G', states(MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING)))
                .where('W', states(PPMetaBlocks.PP_GLASS_CASING.getState(PPGlassCasing.CasingType.WATER)))
                .where('T', states(PPMetaBlocks.PP_BLOCK_CASING.getState(PPBlockCasing.MetalCasingType.ORE_PLANT))
                        .or(states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS))))
                .where('C', states(GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.CRUSHING_WHEELS)))
                .where('F', states(MetaBlocks.FRAMES.get(Materials.RedSteel).getBlock(Materials.RedSteel)))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where(' ', any())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder base = MultiblockShapeInfo.builder()
                .aisle("XXXXEXXXX", "XXXXXXXXX", "XHHHHHHHX", "XXXXXXXXX", "XTTTTTTTX", "XXXXXXXXX", "XXXXXXXXX","XXXXXXXXX")
                .aisle("XXXXXXXXX", "XP     PX", "HP GGG PH", "XP     PX", "TP WWW PT", "XP     PX", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X       X", "H GGGGG H", "X       X", "T WWWWW T", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X       X", "HGGGGGGGH", "X       X", "TWWWWWWWT", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X   P   X", "HGGGPGGGH", "X   P   X", "TWWWPWWWT", "X   P   X", "XCCCPCCCX", "XFFFMFFFX")
                .aisle("XXXXXXXXX", "X       X", "HGGGGGGGH", "X       X", "TWWWWWWWT", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "X       X", "H GGGGG H", "X       X", "T WWWWW T", "X       X", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXXXXXXX", "XP     PX", "HP GGG PH", "XP     PX", "TP WWW PT", "XP     PX", "XCCCCCCCX", "XFFFFFFFX")
                .aisle("XXXOXmXXX", "XXXISiXXX", "XHHHHHHHX", "XXXXXXXXX", "XTTTTTTTX", "XXXXXXXXX", "XXXXXXXXX", "XXXXXXXXX")
                .where('S', PPMetaTileEntities.ORE_PROCESSING_PLANT, EnumFacing.SOUTH)
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.EV], EnumFacing.SOUTH)
                .where('i', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.EV], EnumFacing.SOUTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.EV], EnumFacing.SOUTH)
                .where('m', MetaTileEntities.MAINTENANCE_HATCH, EnumFacing.SOUTH)
                .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.EV], EnumFacing.NORTH)
                .where('M', MetaTileEntities.MUFFLER_HATCH[GTValues.LV], EnumFacing.UP)
                .where('X', PPMetaBlocks.PP_BLOCK_CASING.getState(PPBlockCasing.MetalCasingType.ORE_PLANT))
                .where('P', MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TITANIUM_PIPE))
                .where('H', GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT))
                .where('G', MetaBlocks.MULTIBLOCK_CASING.getState(BlockMultiblockCasing.MultiblockCasingType.GRATE_CASING))
                .where('W', PPMetaBlocks.PP_GLASS_CASING.getState(PPGlassCasing.CasingType.WATER))
                .where('T', PPMetaBlocks.PP_BLOCK_CASING.getState(PPBlockCasing.MetalCasingType.ORE_PLANT))
                .where('C', GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.CRUSHING_WHEELS))
                .where('F', MetaBlocks.FRAMES.get(Materials.RedSteel).getBlock(Materials.RedSteel))
                .where(' ', Blocks.AIR.getDefaultState());

        shapeInfo.add(base.shallowCopy()
                .where('T', MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS))
                .build());
        shapeInfo.add(base.build());
        return shapeInfo;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return PPTextures.ORE_PLANT_CASING;
    }

    @Nonnull
    @Override
    protected OrientedOverlayRenderer getFrontOverlay() {
        return PPTextures.ORE_PROCESSING_PLANT;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
    }


    @Override
    public boolean canBeDistinct() {
        return true;
    }
}

