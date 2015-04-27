/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.Level;

public abstract class TileSignalFoundation extends RailcraftTileEntity {
    private boolean checkedBlock = false;

    public abstract ISignalTileDefinition getSignalType();

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(worldObj))
            return;

        // Check and fix invalid block ids and metadata
        if (!checkedBlock) {
            checkedBlock = true;

            if (!getSignalType().isEnabled()) {
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                return;
            }

            if (getBlockType() != getSignalType().getBlock()) {
                Game.log(Level.INFO, "Updating Machine Tile Block: {0} {1}->{2}, [{3}, {4}, {5}]", getClass().getSimpleName(), getBlockType(), getSignalType().getBlock(), xCoord, yCoord, zCoord);
                worldObj.setBlock(xCoord, yCoord, zCoord, getSignalType().getBlock(), getId(), 3);
                validate();
                worldObj.setTileEntity(xCoord, yCoord, zCoord, this);
                updateContainingBlockInfo();
            }

            int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            if (getBlockType() != null && getClass() != ((BlockSignalBase) getBlockType()).getSignalType(meta).getTileClass()) {
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, getSignalType().getMeta(), 3);
                validate();
                worldObj.setTileEntity(xCoord, yCoord, zCoord, this);
                Game.log(Level.INFO, "Updating Machine Tile Metadata: {0} {1}->{2}, [{3}, {4}, {5}]", getClass().getSimpleName(), meta, getSignalType().getMeta(), xCoord, yCoord, zCoord);
                updateContainingBlockInfo();
            }
        }
    }

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
    public String getLocalizationTag() {
        return getSignalType().getTag() + ".name";
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
