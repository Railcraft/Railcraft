/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.Block;

public class ItemBlockRailcraftColored<B extends Block & IRailcraftBlock> extends ItemBlockRailcraftSubtyped<B> {

    public ItemBlockRailcraftColored(B block) {
        super(block);
    }

    @Override
    public ColorPlugin.IColorFunctionItem colorHandler() {
        return (stack, tintIndex) -> EnumColor.fromOrdinal(stack.getItemDamage()).getHexColor();
    }
}
