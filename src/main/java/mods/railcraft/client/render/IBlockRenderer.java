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

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IBlockRenderer
{

    public void renderBlock(RenderBlocks renderblocks, IBlockAccess world, int x, int y, int z, Block block);
}
