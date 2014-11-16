/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;

public abstract class TileSignalFoundation extends RailcraftTileEntity {

    public abstract ISignalTileDefinition getSignalType();

    public boolean blockActivated(int side, EntityPlayer player) {
        return false;
    }

    public boolean rotateBlock(ForgeDirection axis) {
        return false;
    }

    public ForgeDirection[] getValidRotations() {
        return ForgeDirection.VALID_DIRECTIONS;
    }

    public void onBlockPlaced() {
    }

    public void onBlockRemoval() {
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        RailcraftBlocks.getBlockSignal().setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1);
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        return AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1);
    }

    public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
        return false;
    }

    public boolean canConnectRedstone(int dir) {
        return false;
    }

    public int getPowerOutput(int side) {
        return PowerPlugin.NO_POWER;
    }

    public float getHardness() {
        return getSignalType().getHardness();
    }

    @Override
    public Block getBlockType() {
        return RailcraftBlocks.getBlockSignal();
    }

    @Override
    public short getId() {
        return (short) getSignalType().getMeta();
    }

    @Override
    public String getName() {
        return LocalizationPlugin.translate(getSignalType().getTag() + ".name");
    }

}
