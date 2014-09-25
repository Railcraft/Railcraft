/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.render;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.common.blocks.machine.beta.TileBoilerTank;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RenderBoilerTank implements IBlockRenderer {

    private static final float BORDER = RenderTools.PIXEL;

    public RenderBoilerTank() {
    }

    @Override
    public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block) {
        TileEntity t = world.getTileEntity(x, y, z);
        if (t instanceof TileBoilerTank) {
            TileBoilerTank myTile = (TileBoilerTank) t;

            float x1, x2, z1, z2;
            x1 = z1 = BORDER;
            x2 = z2 = 1 - BORDER;

            if (myTile.isConnected()) {
                TileEntity tile = world.getTileEntity(x - 1, y, z);
                if (tile instanceof TileBoilerTank) {
                    x1 = 0;
                }
                tile = world.getTileEntity(x + 1, y, z);
                if (tile instanceof TileBoilerTank) {
                    x2 = 1;
                }
                tile = world.getTileEntity(x, y, z - 1);
                if (tile instanceof TileBoilerTank) {
                    z1 = 0;
                }
                tile = world.getTileEntity(x, y, z + 1);
                if (tile instanceof TileBoilerTank) {
                    z2 = 1;
                }
            }

            block.setBlockBounds(x1, 0, z1, x2, 1, z2);
            RenderTools.renderStandardBlock(renderblocks, block, x, y, z);
            block.setBlockBounds(0, 0, 0, 1, 1, 1);
        }
    }
}
