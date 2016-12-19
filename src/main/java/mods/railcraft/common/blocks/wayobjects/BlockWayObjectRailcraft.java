/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.api.core.IVariantEnum;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class BlockWayObjectRailcraft extends BlockWayObject {

    public static final PropertyEnum<EnumWayObject> TYPE = PropertyEnum.create("type", EnumWayObject.class);

    public BlockWayObjectRailcraft() {
        setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumWayObject.BLOCK_SIGNAL));

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

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return EnumWayObject.class;
    }

    @Override
    public void defineRecipes() {
        // Define Block Signal
        EnumWayObject structure = EnumWayObject.BLOCK_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LCI",
                    " BI",
                    "   ",
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL,
                    'I', "ingotIron",
                    'L', RailcraftItems.SIGNAL_LAMP,
                    'B', "dyeBlack");
        }

        // Define Dual Head Block Signal
        structure = EnumWayObject.DUAL_HEAD_BLOCK_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LCI",
                    " BI",
                    "LRI",
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL,
                    'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                    'I', "ingotIron",
                    'L', RailcraftItems.SIGNAL_LAMP,
                    'B', "dyeBlack");
        }

        // Define Distant Signal
        structure = EnumWayObject.DISTANT_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LCI",
                    " BI",
                    "   ",
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                    'I', "ingotIron",
                    'L', RailcraftItems.SIGNAL_LAMP,
                    'B', "dyeBlack");
        }

        // Define Dual Head Block Signal
        structure = EnumWayObject.DUAL_HEAD_DISTANT_SIGNAL;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "LRI",
                    " BI",
                    "LRI",
                    'R', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                    'I', "ingotIron",
                    'L', RailcraftItems.SIGNAL_LAMP,
                    'B', "dyeBlack");
        }

        // Define Switch Lever
        structure = EnumWayObject.SWITCH_LEVER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            ItemStack stack = structure.getItem();
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "PLI",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'L', Blocks.LEVER,
                    'P', Blocks.PISTON,
                    'B', "dyeBlack",
                    'R', "dyeRed");
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "ILP",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'L', Blocks.LEVER,
                    'P', Blocks.PISTON,
                    'B', "dyeBlack",
                    'R', "dyeRed");
        }

        // Define Switch Motor
        structure = EnumWayObject.SWITCH_MOTOR;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            ItemStack stack = structure.getItem();
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "PCI",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'P', Blocks.PISTON,
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                    'B', "dyeBlack",
                    'R', "dyeRed");
            CraftingPlugin.addRecipe(stack,
                    "RBW",
                    "ICP",
                    'W', "dyeWhite",
                    'I', "ingotIron",
                    'P', Blocks.PISTON,
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                    'B', "dyeBlack",
                    'R', "dyeRed");
        }

        // Define Receiver Box
        structure = EnumWayObject.BOX_RECEIVER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER);
        }

        // Define Controller Box
        structure = EnumWayObject.BOX_CONTROLLER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.CONTROLLER);
        }

        // Define Analog Controller Box
        structure = EnumWayObject.BOX_ANALOG_CONTROLLER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IQI",
                    'I', "ingotIron",
                    'Q', Items.COMPARATOR,
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.CONTROLLER);
        }

        // Define Capacitor Box
        structure = EnumWayObject.BOX_CAPACITOR;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', Items.REPEATER);
        }

        // Define Signal Block Box
        structure = EnumWayObject.BOX_BLOCK_RELAY;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    " C ",
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL);
        }

        // Define Signal Sequencer Box
        structure = EnumWayObject.BOX_SEQUENCER;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'C', Items.COMPARATOR);
        }
        // Define Signal Interlock Box
        structure = EnumWayObject.BOX_INTERLOCK;
        if (RailcraftConfig.isSubBlockEnabled(structure.getTag())) {
            CraftingPlugin.addRecipe(structure.getItem(),
                    " L ",
                    "ICI",
                    "IRI",
                    'I', "ingotIron",
                    'R', "dustRedstone",
                    'L', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                    'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.CONTROLLER);
        }
    }

    @Override
    public IWayObjectDefinition getSignalType(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumWayObject type : EnumWayObject.getCreativeList()) {
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
        return getDefaultState().withProperty(TYPE, EnumWayObject.fromOrdinal(meta));
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
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
