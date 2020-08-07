/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.items.ItemCircuit;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by CovertJaguar on 7/5/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(SignalVariant.class)
public class BlockMachineSignalRailcraft extends BlockMachineSignal<SignalVariant> {

    public static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(-BLOCK_BOUNDS).raiseFloor(0.35).build();

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), FRONT, CONNECTION_DOWN, CONNECTION_NORTH, CONNECTION_SOUTH, CONNECTION_EAST, CONNECTION_WEST);
    }

    @Override
    public void defineRecipes() {
        SignalVariant.BLOCK.ifAvailable(v -> CraftingPlugin.addShapedRecipe(v.getStack(),
                "LCI",
                " BI",
                "   ",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.SIGNAL,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));

        SignalVariant.DISTANT.ifAvailable(v -> CraftingPlugin.addShapedRecipe(v.getStack(),
                "LCI",
                " BI",
                "   ",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RECEIVER,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));

        SignalVariant.TOKEN.ifAvailable(v -> CraftingPlugin.addShapedRecipe(v.getStack(),
                "LCI",
                " BI",
                "   ",
                'C', RailcraftItems.CIRCUIT, ItemCircuit.EnumCircuit.RADIO,
                'I', "ingotIron",
                'L', RailcraftItems.SIGNAL_LAMP,
                'B', "dyeBlack"));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BOUNDING_BOX;
    }

}
