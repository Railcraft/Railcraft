/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import mods.railcraft.common.plugins.forge.BlockPlugin;

public class BlockFenceReplacement extends BlockFence {

    public BlockFenceReplacement() {
        this(Material.wood);
    }

    public BlockFenceReplacement(Material material) {
        super(material);
        setHardness(2.0F);
        setResistance(5F);
        setStepSound(soundTypeWood);
        setUnlocalizedName("fence");
    }
    
    @Override
    public boolean canConnectTo(IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block == this || BlockPlugin.isGate(block))
            return true;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileTrack)
            if (((TileTrack) tile).getTrackInstance() instanceof TrackGated)
                return true;
        if (block != null && block.getMaterial().isOpaque() && block.isFullCube())
            return block.getMaterial() != Material.gourd;
        return false;
    }
}
