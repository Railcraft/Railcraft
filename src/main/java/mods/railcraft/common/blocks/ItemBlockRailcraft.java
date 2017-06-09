/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks;

import mods.railcraft.common.plugins.color.ColorPlugin;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemBlockRailcraft extends ItemBlock implements ColorPlugin.IColoredItem, IRailcraftItemBlock {

    public ItemBlockRailcraft(Block block) {
        super(block);
    }

    @Override
    public void finalizeDefinition() {
        if (block instanceof ColorPlugin.IColoredBlock)
            ColorPlugin.instance.register(this, this);
    }

    @Override
    public ItemBlockRailcraft getObject() {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor colorHandler() {
        return (stack, tintIndex) -> EnumColor.fromItemStack(stack).getHexColor();
    }

    @Override
    public String getUnlocalizedName() {
        return LocalizationPlugin.convertTag(super.getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        addToolTips(stack, player, info, adv);
    }
}
