/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wayobjects;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.RailcraftTickingTileEntity;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class TileWayObject extends RailcraftTickingTileEntity {

    private boolean checkedBlock = false;

    public abstract IWayObjectDefinition getSignalType();

    @Override
    public void update() {
        super.update();

        if (Game.isClient(worldObj))
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

    public boolean blockActivated(EnumFacing side, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem) {
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

//    public AxisAlignedBB getBoundingBox(World world, BlockPos pos) {
//        return AABBFactory.start().createBoxForTileAt(pos).build();
//    }

    public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return Block.FULL_BLOCK_AABB;
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos) {
        return getBoundingBox(world, pos);
    }

    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return getBoundingBox(world, pos).offset(pos);
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
        return RailcraftBlocks.WAY_OBJECT.block();
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
