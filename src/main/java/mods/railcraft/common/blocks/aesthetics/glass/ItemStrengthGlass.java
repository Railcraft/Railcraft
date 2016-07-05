/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.aesthetics.glass;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStrengthGlass extends ItemBlockRailcraft {

    public ItemStrengthGlass(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor colorHandler() {
        return (stack, tintIndex) -> {
            if (BlockStrengthGlass.renderingHighlight)
                return EnumColor.WHITE.getHexColor();
            return EnumColor.fromOrdinal(15 - stack.getItemDamage()).getHexColor();
        };
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

//    @Override
//    public String getUnlocalizedName(ItemStack stack) {
//        return getUnlocalizedName() + "." + EnumColor.fromOrdinal(15 - stack.getItemDamage()).getBaseTag();
//    }

}
