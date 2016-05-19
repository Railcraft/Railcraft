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
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class TileSignalFoundation extends RailcraftTileEntity {

    private boolean checkedBlock = false;

    public abstract ISignalTileDefinition getSignalType();

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(worldObj))
            return;

        // Check and fix invalid block ids and metadata
        if (!checkedBlock) {
            checkedBlock = true;

            if (!getSignalType().isEnabled()) {
                worldObj.setBlockToAir(getPos());
                return;
            }

//            if (getBlockType() != getSignalType().getBlock()) {
//                Game.log(Level.INFO, "Updating Machine Tile Block: {0} {1}->{2}, [{3}]", getClass().getSimpleName(), getBlockType(), getSignalType().getBlock(), getPos());
//                worldObj.setBlockState(getPos(), newState/*getSignalType().getBlock(), getId()*/, 3);
//                validate();
//                worldObj.setTileEntity(getPos(), this);
//                updateContainingBlockInfo();
//            }
//
//            int meta = worldObj.getBlockMetadata(getPos());
//            if (getBlockType() != null && getClass() != ((BlockSignalBase) getBlockType()).getSignalType(meta).getTileClass()) {
//                worldObj.setBlockState(getPos(), newState/*getSignalType().getMeta()*/, 3);
//                validate();
//                worldObj.setTileEntity(getPos(), this);
//                Game.log(Level.INFO, "Updating Machine Tile Metadata: {0} {1}->{2}, [{3}, {4}, {5}]", getClass().getSimpleName(), meta, getSignalType().getMeta(), getPos());
//                updateContainingBlockInfo();
//            }
        }
    }

    public boolean blockActivated(EnumFacing side, EntityPlayer player) {
        return false;
    }

    public boolean rotateBlock(EnumFacing axis) {
        return false;
    }

    public EnumFacing[] getValidRotations() {
        return EnumFacing.VALUES;
    }

    public void onBlockRemoval() {
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        getBlockType().setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos) {
        return AABBFactory.start().createBoxForTileAt(pos).build();
    }

    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return getCollisionBoundingBox(world, pos);
    }

    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    public boolean canConnectRedstone(EnumFacing side) {
        return false;
    }

    public int getPowerOutput(EnumFacing side) {
        return PowerPlugin.NO_POWER;
    }

    public float getHardness() {
        return getSignalType().getHardness();
    }

    @Override
    public Block getBlockType() {
        return RailcraftBlocks.signal.block();
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
