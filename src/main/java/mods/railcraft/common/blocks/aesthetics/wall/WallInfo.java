/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.wall;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface WallInfo {

    ItemStack getItem();

    ItemStack getItem(int qty);

    Block getSource();

    ItemStack getSourceItem();

    int getSourceMeta();

    String getTag();

    boolean isEnabled();

    float getBlockHardness(World world, int x, int y, int z);

    float getExplosionResistance(Entity entity);

    Block getBlock();
    
}
