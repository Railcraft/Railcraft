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
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

/**
 * Created by CovertJaguar on 7/15/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("unused")
public class ColorProxyClient extends ColorProxy {
    @Override
    public void register(Item item, ColorPlugin.IColorHandlerItem colorHandler) {
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorHandler.colorHandler()::getColor, item);
    }

    @Override
    public void register(Block block, ColorPlugin.IColorHandlerBlock colorHandler) {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(colorHandler.colorHandler()::getColor, block);
    }
}
