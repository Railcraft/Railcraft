/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import net.minecraft.block.Block;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemBlockRailcraftMultiType extends ItemBlockRailcraft {

    public ItemBlockRailcraftMultiType(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
