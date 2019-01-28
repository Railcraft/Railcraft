/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileCrafter;
import mods.railcraft.common.blocks.logic.CokeOvenLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class TileCokeOven extends TileCrafter {

    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

    static {
        char[][][] map = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'W', 'A', 'W', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },};
        patterns.add(new MultiBlockPattern(map, 2, 1, 2));
    }

    public TileCokeOven() {
        setLogic(new StructureLogic("coke_oven", this, patterns, new CokeOvenLogic(Logic.Adapter.of(this))) {

            @Override
            public boolean isMapPositionValid(BlockPos pos, char mapPos) {
                IBlockState other = WorldPlugin.getBlockState(world, pos);
                switch (mapPos) {
                    case 'O': // Other
                        if (RailcraftBlocks.COKE_OVEN.isEqual(other) || RailcraftBlocks.COKE_OVEN_RED.isEqual(other))
                            return false;
                        break;
                    case 'W': // Window
                    case 'B': // Block
                        if (!RailcraftBlocks.COKE_OVEN.isEqual(other) && !RailcraftBlocks.COKE_OVEN_RED.isEqual(other))
                            return false;
                        break;
                    case 'A': // Air
                        if (!other.getBlock().isAir(other, world, pos))
                            return false;
                        break;
                    case '*': // Anything
                        return true;
                }
                return true;
            }
        });
    }

    public static void placeCokeOven(World world, BlockPos pos, int creosote, ItemStack input, ItemStack output) {
        MultiBlockPattern pattern = TileCokeOven.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.COKE_OVEN.getDefaultState());
        blockMapping.put('W', RailcraftBlocks.COKE_OVEN.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileCokeOven) {
            // FIXME this might not work if the structure isn't ready
//            CokeOvenLogic logic = ((TileCokeOvenLogic) tile).getLogic(CokeOvenLogic.class);
//            logic.getTankManager().get(0).setFluid(Fluids.CREOSOTE.get(creosote));
//            logic.setInventorySlotContents(CokeOvenLogic.SLOT_INPUT, input);
//            logic.setInventorySlotContents(CokeOvenLogic.SLOT_OUTPUT, output);
        }
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return getLogic(IFluidHandlerImplementor.class)
                .map(l -> FluidTools.interactWithFluidHandler(player, hand, l.getTankManager())).orElse(false)
                || super.blockActivated(player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class).map(l -> l.getPatternMarker() == 'W').orElse(false)
                ? hasFlames()
                ? base.withProperty(BlockCokeOven.ICON, 2)
                : base.withProperty(BlockCokeOven.ICON, 1)
                : base.withProperty(BlockCokeOven.ICON, 0);
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.COKE_OVEN;
    }

}