/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.glass;

import mods.railcraft.common.blocks.ItemBlockRailcraftSubtyped;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStrengthGlass extends ItemBlockRailcraftSubtyped {

    public ItemStrengthGlass(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getPropertyString(IBlockState state) {
        return super.getPropertyString(state).replaceAll("color=.*,", "");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor colorHandler() {
        return (stack, tintIndex) -> EnumColor.fromOrdinal(stack.getItemDamage()).getHexColor();
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return block.getUnlocalizedName();
    }
}
