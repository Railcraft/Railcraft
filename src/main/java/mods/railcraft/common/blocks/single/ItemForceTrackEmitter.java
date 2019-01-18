/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;

/**
 *
 */
public class ItemForceTrackEmitter extends ItemBlockRailcraft<BlockForceTrackEmitter> {

    public ItemForceTrackEmitter(BlockForceTrackEmitter block) {
        super(block);
    }

    @Override
    public ColorPlugin.IColorFunctionItem colorHandler() {
        return (stack, index) -> EnumColor.fromItemStack(stack).orElse(BlockForceTrackEmitter.DEFAULT_COLOR).getHexColor();
    }
}
