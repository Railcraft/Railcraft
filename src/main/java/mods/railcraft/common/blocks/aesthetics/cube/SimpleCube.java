/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.cube;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class SimpleCube {

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return type != EnumCreatureType.CREATURE;
    }

    public void updateTick(World world, int x, int y, int z, Random rand) {
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
    }

    public void onBlockPlaced(World world, int x, int y, int z) {
    }

    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    }

    public void onBlockAdded(World world, int x, int y, int z) {
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
    }

    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        return world.setBlockToAir(pos);
    }

    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, EnumFacing face) {
        return 0;
    }

    public int getFlammability(IBlockAccess world, int x, int y, int z, EnumFacing face) {
        return 0;
    }

    public boolean isFlammable(IBlockAccess world, int x, int y, int z, EnumFacing face) {
        return false;
    }

}
