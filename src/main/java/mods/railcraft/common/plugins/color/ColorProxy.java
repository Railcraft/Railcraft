/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.plugins.color;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Created by CovertJaguar on 7/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ColorProxy {
    public <I extends Item & ColorPlugin.IColorHandlerItem> void register(I item) {
        register(item, item);
    }

    public void register(Item item, ColorPlugin.IColorHandlerItem colorHandler) {

    }

    public <B extends Block & ColorPlugin.IColorHandlerBlock> void register(B block) {
        register(block, block);
    }

    public void register(Block item, ColorPlugin.IColorHandlerBlock colorHandler) {

    }
}
