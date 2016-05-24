/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class BlockSignalRailcraft extends BlockSignalBase implements IRailcraftObject {

    public static final PropertyEnum<EnumSignal> TYPE = PropertyEnum.create("type", EnumSignal.class);

    public BlockSignalRailcraft() {
        setUnlocalizedName("railcraft.signal");
        setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumSignal.BLOCK_SIGNAL));

        GameRegistry.registerTileEntity(TileBoxController.class, "RCTileStructureControllerBox");
        GameRegistry.registerTileEntity(TileBoxReceiver.class, "RCTileStructureReceiverBox");
        GameRegistry.registerTileEntity(TileBoxCapacitor.class, "RCTileStructureCapacitorBox");
        GameRegistry.registerTileEntity(TileBoxBlockRelay.class, "RCTileStructureSignalBox");
        GameRegistry.registerTileEntity(TileBoxSequencer.class, "RCTileStructureSequencerBox");
        GameRegistry.registerTileEntity(TileBoxInterlock.class, "RCTileStructureInterlockBox");
        GameRegistry.registerTileEntity(TileBoxAnalogController.class, "RCTileStructureAnalogBox");
        GameRegistry.registerTileEntity(TileSwitchMotor.class, "RCTileStructureSwitchMotor");
        GameRegistry.registerTileEntity(TileSwitchLever.class, "RCTileStructureSwitchLever");
        GameRegistry.registerTileEntity(TileSwitchRouting.class, "RCTileStructureSwitchRouting");
        GameRegistry.registerTileEntity(TileSignalDistantSignal.class, "RCTileStructureDistantSignal");
        GameRegistry.registerTileEntity(TileSignalDualHeadBlockSignal.class, "RCTileStructureDualHeadBlockSignal");
        GameRegistry.registerTileEntity(TileSignalBlockSignal.class, "RCTileStructureBlockSignal");
        GameRegistry.registerTileEntity(TileSignalDualHeadDistantSignal.class, "RCTileStructureDualHeadDistantSignal");
    }

    @Override
    public void defineRecipes() {
        // Define Block Signal
        EnumSignal structure = EnumSignal.BLOCK_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LCI",
                    " BI",
                    "   ",
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.SIGNAL,
                    'I', "ingotIron",
                    'L', RailcraftItems.signalLamp,
                    'B', "dyeBlack");
        }

        // Define Dual Head Block Signal
        structure = EnumSignal.DUAL_HEAD_BLOCK_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LCI",
                    " BI",
                    "LRI",
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.SIGNAL,
                    'R', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                    'I', "ingotIron",
                    'L', RailcraftItems.signalLamp,
                    'B', "dyeBlack");
        }

        // Define Distant Signal
        structure = EnumSignal.DISTANT_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LCI",
                    " BI",
                    "   ",
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                    'I', "ingotIron",
                    'L', RailcraftItems.signalLamp,
                    'B', "dyeBlack");
        }

        // Define Dual Head Block Signal
        structure = EnumSignal.DUAL_HEAD_DISTANT_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LRI",
                    " BI",
                    "LRI",
                    'R', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                    'I', "ingotIron",
                    'L', RailcraftItems.signalLamp,
                    'B', "dyeBlack");
        }

        // Define Switch Lever
        structure = EnumSignal.SWITCH_LEVER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            ItemStack stack = structure.getItem();
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "PLI",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'L', Blocks.lever,
                    'P', Blocks.piston,
                    'B', "dyeBlack",
                    'R', "dyeRed");
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "ILP",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'L', Blocks.lever,
                    'P', Blocks.piston,
                    'B', "dyeBlack",
                    'R', "dyeRed");
        }

        // Define Switch Motor
        structure = EnumSignal.SWITCH_MOTOR;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            ItemStack stack = structure.getItem();
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "PCI",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'P', Blocks.piston,
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                    'B', "dyeBlack",
                    'R', "dyeRed");
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "ICP",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'P', Blocks.piston,
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                    'B', "dyeBlack",
                    'R', "dyeRed");
        }

        // Define Receiver Box
        structure = EnumSignal.BOX_RECEIVER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER);
        }

        // Define Controller Box
        structure = EnumSignal.BOX_CONTROLLER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.CONTROLLER);
        }

        // Define Analog Controller Box
        structure = EnumSignal.BOX_ANALOG_CONTROLLER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IQI",
                    'I', "ingotIron",
                    'Q', Items.comparator,
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.CONTROLLER);
        }

        // Define Capacitor Box
        structure = EnumSignal.BOX_CAPACITOR;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', Items.repeater);
        }

        // Define Signal Block Box
        structure = EnumSignal.BOX_BLOCK_RELAY;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    " C ",
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.SIGNAL);
        }

        // Define Signal Sequencer Box
        structure = EnumSignal.BOX_SEQUENCER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', Items.comparator);
        }
        // Define Signal Interlock Box
        structure = EnumSignal.BOX_INTERLOCK;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    " L ",
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'L', RailcraftItems.circuit, ItemCircuit.EnumCircuit.RECEIVER,
                    'C', RailcraftItems.circuit, ItemCircuit.EnumCircuit.CONTROLLER);
        }
    }

    @Override
    public ISignalTileDefinition getSignalType(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumSignal type : EnumSignal.getCreativeList()) {
            if (type.isEnabled())
                list.add(type.getItem());
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(TYPE).getBlockEntity();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumSignal.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        IVariantEnum.tools.checkVariantObject(getClass(), variant);
        return new ItemStack(this, 1, variant != null ? variant.getItemMeta() : 0);
    }

    @Override
    public void initializeDefinintion() {

    }

    @Override
    public void finalizeDefinition() {

    }
}
