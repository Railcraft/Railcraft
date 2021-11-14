/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.tracks;

import mods.railcraft.api.core.items.ITrackItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * A number of utility functions related to rails.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailTools {
    public static boolean isRailBlockAt(IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z) instanceof BlockRailBase;
    }

    /**
     * Attempts to place a rail of the type provided. There is no need to verify
     * that the ItemStack contains a valid rail prior to calling this function.
     * <p/>
     * The function takes care of that and will return false if the ItemStack is
     * not a valid ITrackItem or an ItemBlock who's id will return true when
     * passed to BlockRailBase.isRailBlock(itemID).
     * <p/>
     * That means this function can place any Railcraft or vanilla rail and has
     * at least a decent chance of being able to place most third party rails.
     *
     * @param stack The ItemStack containing the rail
     * @param world The World object
     * @param i     x-Coord
     * @param j     y-Coord
     * @param k     z-Coord
     * @return true if successful
     * @see ITrackItem
     */
    public static boolean placeRailAt(ItemStack stack, World world, int i, int j, int k) {
        if (stack == null)
            return false;
        if (stack.getItem() instanceof ITrackItem)
            return ((ITrackItem) stack.getItem()).placeTrack(stack.copy(), world, i, j, k);
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).field_150939_a;
            if (BlockRailBase.func_150051_a(block)) {
                boolean success = world.setBlock(i, j, k, block);
                if (success)
                    world.playSoundEffect((float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F, block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                return success;
            }
        }
        return false;
    }

    /**
     * Returns true if the ItemStack contains a valid Railcraft Track item.
     * <p/>
     * Will return false is passed a vanilla rail.
     *
     * @param stack The ItemStack to test
     * @return true if rail
     * @see ITrackItem
     */
    public static boolean isTrackItem(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ITrackItem;
    }

    /**
     * Checks to see if a cart is being held by a ITrackLockdown.
     *
     * @param cart The cart to check
     * @return True if being held
     */
    public static boolean isCartLockedDown(EntityMinecart cart) {
        int x = MathHelper.floor_double(cart.posX);
        int y = MathHelper.floor_double(cart.posY);
        int z = MathHelper.floor_double(cart.posZ);

        if (BlockRailBase.func_150049_b_(cart.worldObj, x, y - 1, z))
            y--;

        TileEntity tile = cart.worldObj.getTileEntity(x, y, z);
        if (tile instanceof ITrackTile) {
            ITrackInstance track = ((ITrackTile) tile).getTrackInstance();
            return track instanceof ITrackLockdown && ((ITrackLockdown) track).isCartLockedDown(cart);
        } else if (tile instanceof ITrackLockdown)
            return ((ITrackLockdown) tile).isCartLockedDown(cart);
        return false;
    }

    public static int countAdjecentTracks(World world, int x, int y, int z) {
        int i = 0;

        if (isTrackFuzzyAt(world, x, y, z - 1))
            ++i;

        if (isTrackFuzzyAt(world, x, y, z + 1))
            ++i;

        if (isTrackFuzzyAt(world, x - 1, y, z))
            ++i;

        if (isTrackFuzzyAt(world, x + 1, y, z))
            ++i;

        return i;
    }

    public static boolean isTrackFuzzyAt(World world, int x, int y, int z) {
        return BlockRailBase.func_150049_b_(world, x, y, z) ? true : (BlockRailBase.func_150049_b_(world, x, y + 1, z) ? true : BlockRailBase.func_150049_b_(world, x, y - 1, z));
    }

    public static Set<ITrackTile> getAdjecentTrackTiles(World world, int x, int y, int z) {
        Set<ITrackTile> tracks = new HashSet<ITrackTile>();

        ITrackTile tile = getTrackFuzzyAt(world, x, y, z - 1);
        if (tile != null)
            tracks.add(tile);

        tile = getTrackFuzzyAt(world, x, y, z + 1);
        if (tile != null)
            tracks.add(tile);

        tile = getTrackFuzzyAt(world, x - 1, y, z);
        if (tile != null)
            tracks.add(tile);

        tile = getTrackFuzzyAt(world, x + 1, y, z);
        if (tile != null)
            tracks.add(tile);

        return tracks;
    }

    public static ITrackTile getTrackFuzzyAt(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ITrackTile)
            return (ITrackTile) tile;
        tile = world.getTileEntity(x, y + 1, z);
        if (tile instanceof ITrackTile)
            return (ITrackTile) tile;
        tile = world.getTileEntity(x, y - 1, z);
        if (tile instanceof ITrackTile)
            return (ITrackTile) tile;
        return null;
    }

    public static <T> Set<T> getAdjecentTrackObjects(World world, int x, int y, int z, Class<T> type) {
        Set<T> tracks = new HashSet<T>();

        T object = getTrackObjectFuzzyAt(world, x, y, z - 1, type);
        if (object != null)
            tracks.add(object);

        object = getTrackObjectFuzzyAt(world, x, y, z + 1, type);
        if (object != null)
            tracks.add(object);

        object = getTrackObjectFuzzyAt(world, x - 1, y, z, type);
        if (object != null)
            tracks.add(object);

        object = getTrackObjectFuzzyAt(world, x + 1, y, z, type);
        if (object != null)
            tracks.add(object);

        return tracks;
    }

    public static <T> T getTrackObjectFuzzyAt(World world, int x, int y, int z, Class<T> type) {
        T object = getTrackObjectAt(world, x, y, z, type);
        if (object != null)
            return object;
        object = getTrackObjectAt(world, x, y + 1, z, type);
        if (object != null)
            return object;
        object = getTrackObjectAt(world, x, y - 1, z, type);
        if (object != null)
            return object;
        return null;
    }

    public static <T> T getTrackObjectAt(World world, int x, int y, int z, Class<T> type) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null)
            return null;
        if (type.isInstance(tile))
            return (T) tile;
        if (tile instanceof ITrackTile) {
            ITrackInstance track = ((ITrackTile) tile).getTrackInstance();
            if (type.isInstance(track))
                return (T) track;
        }
        return null;
    }

}
