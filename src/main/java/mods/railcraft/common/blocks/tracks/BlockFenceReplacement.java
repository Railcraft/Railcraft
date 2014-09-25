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
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class BlockFenceReplacement extends BlockFence {

    public BlockFenceReplacement(int i) {
        this(Material.wood);
    }

    public BlockFenceReplacement(Material material) {
        super("planks_oak", material);
        setHardness(2.0F);
        setResistance(5F);
        setStepSound(soundTypeWood);
        setBlockName("fence");
    }

    @Override
    public boolean canConnectFenceTo(IBlockAccess world, int i, int j, int k) {
        Block block = world.getBlock(i, j, k);
        if (block == this || block == Blocks.fence_gate)
            return true;
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileTrack)
            if (((TileTrack) tile).getTrackInstance() instanceof TrackGated)
                return true;
        if (block != null && block.getMaterial().isOpaque() && block.renderAsNormalBlock())
            return block.getMaterial() != Material.gourd;
        return false;
    }

}
