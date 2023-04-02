package kono.gtceu.processingplant.common.metatileentities;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.gui.Widget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import kono.gtceu.processingplant.api.recipes.PPRecipeMaps;
import kono.gtceu.processingplant.api.recipes.builders.OreProcessingPlantRecipeBuilder;
import kono.gtceu.processingplant.api.recipes.recipeproperties.RecipeTypeProperty;
import kono.gtceu.processingplant.client.PPTextures;
import kono.gtceu.processingplant.common.PPBlockCasing;
import kono.gtceu.processingplant.common.PPGlassCasing;
import kono.gtceu.processingplant.common.PPMetaBlocks;
import kono.gtceu.processingplant.loaders.recipe.metatileentities.OreProcessingPlantLoader;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.util.text.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static gregtech.api.gui.widgets.AdvancedTextWidget.withButton;

public class MetaTileEntityOreProcessingPlant extends RecipeMapMultiblockController {

    private int Type;
    //private int targetType;
    private boolean canRunRecipe = false;

    public MetaTileEntityOreProcessingPlant(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, PPRecipeMaps.ORE_PROCESSING_PLANT);
        this.recipeMapWorkable = new OreProcessingPlantLogic(this);

        Type = 0;
        //targetType = 0;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityOreProcessingPlant(metaTileEntityId);
    }

    //コントローラーGUIのテキスト登録
    @Override
    public void addDisplayText(List<ITextComponent> textList) {
        if (!this.recipeMapWorkable.isWorkingEnabled()) {
            //停止中?
            textList.add(new TextComponentTranslation("gregtech.multiblock.work_paused"));
        } else if (this.recipeMapWorkable.isActive()) {
            //レシピ実行中?
            textList.add(new TextComponentTranslation("gregtech.multiblock.running"));
            int currentProgress = (int) (this.recipeMapWorkable.getProgressPercent() * 100.0D);
            textList.add(new TextComponentTranslation("gregtech.multiblock.progress", currentProgress));
        } else {
            //待機中?
            textList.add(new TextComponentTranslation("gregtech.multiblock.idling"));
        }
        if (this.recipeMapWorkable.isHasNotEnoughEnergy()) {
            //エネルギー不足
            textList.add((new TextComponentTranslation("gregtech.multiblock.not_enough_energy")).setStyle((new Style()).setColor(TextFormatting.RED)));
        }
        // 現在のTypeを表示させるテキスト
        textList.add(new TextComponentTranslation("processingplant.multiblock.oreprocessingplant_1", Type));
        if (!this.isActive()) {
            //動いていない場合
            //翻訳可能なボタンの説明を登録
            ITextComponent buttonText = new TextComponentTranslation("processingplant.multiblock.oreprocessingplant_2");
            //ボタン前に表示するテキスト登録
            buttonText.appendText("");
            //"sub"とマークされたボタンテキストを翻訳可能なテキストと共に登録
            buttonText.appendSibling(withButton(new TextComponentTranslation("processingplant.multiblock.back"), "sub"));
            //ボタン前に表示するテキストを登録
            buttonText.appendText("/");
            //"add"とマークされたボタンテキストを翻訳可能なテキストと共に登録
            buttonText.appendSibling(withButton(new TextComponentTranslation("processingplant.multiblock.next"), "add"));
            //buttonTextをテキストとして生成
            textList.add(buttonText);
        } else {
            //動いている場合
            textList.add((new TextComponentTranslation("processingplant.multiblock.warning_1")).setStyle((new Style()).setColor(TextFormatting.LIGHT_PURPLE)));
        }
        //処理工程表示
        textList.add(new TextComponentTranslation("processingplant.multiblock.oreprocessingplant_3").setStyle((new Style()).setColor(TextFormatting.YELLOW)));
        ITextComponent processingType = new TextComponentString("1.");
        processingType.appendSibling(new TextComponentTranslation("recipemap.macerator.name"));
        if ((Type < 2) || (Type > 3)) {
            processingType.appendText(" 2.");
            processingType.appendSibling(new TextComponentTranslation("recipemap.ore_washer.name"));
        }
        if ((Type == 4) || (Type == 5) || (Type == 8) || (Type == 9)) {
            processingType.appendText(" or ");
            processingType.appendSibling(new TextComponentTranslation("recipemap.chemical_bath.name"));
        }
        if ((Type >= 6 ) && (Type <= 9)) {
            processingType.appendText(" 3.");
            processingType.appendText("[");
            processingType.appendSibling(new TextComponentTranslation("recipemap.sifter.name"));
            processingType.appendText("]");
            processingType.appendText(" or ");
        }
        if ((Type == 1) || (Type == 2) || (Type == 5) || (Type == 7) || (Type ==9)) {
            if ((Type == 2)) {
                processingType.appendText(" 2.");
                processingType.appendSibling(new TextComponentTranslation("recipemap.thermal_centrifuge.name"));
                processingType.appendText(" 3.");
                processingType.appendSibling(new TextComponentTranslation("recipemap.macerator.name"));
            } else {
                if ((Type < 6)) {
                    processingType.appendText(" 3.");
                }
                processingType.appendSibling(new TextComponentTranslation("recipemap.thermal_centrifuge.name"));
                processingType.appendText(" 4.");
                processingType.appendSibling(new TextComponentTranslation("recipemap.macerator.name"));
            }
        } else {
            if ((Type == 3)) {
                processingType.appendText(" 2.");
                processingType.appendSibling(new TextComponentTranslation("recipemap.macerator.name"));
                processingType.appendText(" 3.");
                processingType.appendSibling(new TextComponentTranslation("recipemap.centrifuge.name"));
            } else {
                if ((Type < 6)) {
                    processingType.appendText(" 3.");
                }
                processingType.appendSibling(new TextComponentTranslation("recipemap.macerator.name"));
                processingType.appendText(" 4.");
                processingType.appendSibling(new TextComponentTranslation("recipemap.centrifuge.name"));
            }
        }
        textList.add(processingType);
    }

    //ボタンが押されたときの挙動登録
    @Override
    protected void handleDisplayClick(String componentData, Widget.ClickData clickData) {
        super.handleDisplayClick(componentData, clickData);
        //"add"とマークされたボタンが押された場合
        if (componentData.equals("add")) {
            //Type <= 9 の時
            if (Type <= 8) {
                //Typeに1を加算
                Type += 1;
            }
        } else if (componentData.equals("sub")) { //"sub"とマークされたボタンが押された場合
            // Type > 0の時
            if (Type > 0) {
                //Typeに1を減算
                Type -= 1;
            }
        }
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
                        .or(autoAbilities(true, true, true, true, true, false, false))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(3))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(3)))
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
        tooltip.add(I18n.format("processingplant.machine.oreprocessingplant.tooltip1"));
        tooltip.add(I18n.format("processingplant.machine.oreprocessingplant.tooltip2"));
        tooltip.add(I18n.format("processingplant.machine.oreprocessingplant.tooltip3"));
    }


    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("Type", this.Type);
        //data.setInteger("targetType", this.targetType);
        return data;
    }

    /*public void setType(int Type) {
        this.Type = Type;
        if (!getWorld().isRemote) {
            writeCustomData(600, buf -> buf.writeInt(Type));
            markDirty();
        }
    }*/

   /* @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == 600) {
            this.Type = buf.readInt();
            scheduleRenderUpdate();
        }
    }*/

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.Type = data.getInteger("Type");
        //this.targetType = data.getInteger("targetType");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.Type);
        //buf.writeInt(this.targetType);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.Type = buf.readInt();
        //this.targetType = buf.readInt();
    }

    @Override
    public boolean checkRecipe(Recipe recipe, boolean consumeIfSuccess) {
        return this.Type == recipe.getProperty(RecipeTypeProperty.getInstance(), 0);
    }

    public class OreProcessingPlantLogic extends MultiblockRecipeLogic {
        public OreProcessingPlantLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public boolean isWorking() {
            return !this.hasNotEnoughEnergy && this.workingEnabled;
        }

        @Override
        protected void updateRecipeProgress() {
            super.updateRecipeProgress();
            canRunRecipe = true;
        }

    }
}

