/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;
import static net.minecraftforge.common.util.ForgeDirection.*;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class PowerPlugin {

    private static final ForgeDirection[] SIDES = {NORTH, EAST, SOUTH, WEST};
    public static final int NO_POWER = 0;
    public static final int FULL_POWER = 15;

    public static boolean isBlockBeingPowered(World world, int x, int y, int z) {
        return world.isBlockIndirectlyGettingPowered(x, y, z);
    }

    public static boolean isBlockBeingPowered(World world, int x, int y, int z, ForgeDirection from) {
        x = MiscTools.getXOnSide(x, from);
        y = MiscTools.getYOnSide(y, from);
        z = MiscTools.getZOnSide(z, from);
        return world.getIndirectPowerOutput(x, y, z, from.ordinal());
    }
    
    public static int getBlockPowerLevel(World world, int x, int y, int z, ForgeDirection from) {
    	x = MiscTools.getXOnSide(x, from);
    	y = MiscTools.getYOnSide(y, from);
    	z = MiscTools.getZOnSide(z, from);
    	return world.getIndirectPowerLevelTo(x, y, z, from.ordinal());
    }

    public static boolean isBlockBeingPoweredByRepeater(World world, int x, int y, int z, ForgeDirection from) {
        Block block = WorldPlugin.getBlockOnSide(world, x, y, z, from);
        return block == Blocks.powered_repeater && isBlockBeingPowered(world, x, y, z, from);
    }

    public static boolean isBlockBeingPoweredByRepeater(World world, int x, int y, int z) {
        for (ForgeDirection side : SIDES) {
            if (isBlockBeingPoweredByRepeater(world, x, y, z, side))
                return true;
        }
        return false;
    }

    public static boolean isRedstonePowered(World world, int x, int y, int z) {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (isRedstonePowering(world, x, y, z, 0, side) || isRedstonePowering(world, x, y, z, -1, side))
                return true;
        }
        return false;
    }

    private static boolean isRedstonePowering(World world, int x, int y, int z, int yOffset, ForgeDirection side) {
        Block block = WorldPlugin.getBlockOnSide(world, x, y + yOffset, z, side);
        if (block == Blocks.redstone_wire) {
            int meta = WorldPlugin.getBlockMetadataOnSide(world, x, y + yOffset, z, side);
            if (meta > 0)
                return true;
        }
        return false;
    }

}
