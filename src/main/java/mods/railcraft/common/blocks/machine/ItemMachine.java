/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import mods.railcraft.common.blocks.ItemBlockRailcraftMultiType;
import mods.railcraft.common.gui.tooltips.ToolTip;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemMachine extends ItemBlockRailcraftMultiType {

    private final BlockMachine machineBlock;

    public ItemMachine(Block block) {
        super(block);
        this.machineBlock = (BlockMachine) block;
        setUnlocalizedName("railcraft.machine");
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return machineBlock.getIcon(2, damage);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return machineBlock.getMachineProxy().getMachine(stack.getItemDamage()).getTag();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (!world.setBlock(x, y, z, machineBlock, metadata, 3))
            return false;

        if (world.getBlock(x, y, z) == machineBlock) {
            machineBlock.onBlockPlacedBy(world, x, y, z, player, stack);
            machineBlock.onPostBlockPlaced(world, x, y, z, metadata);
            machineBlock.initFromItem(world, x, y, z, stack);
        }

        return true;
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        return machineBlock.getMachineProxy().getMachine(stack.getItemDamage()).getToolTip(stack, player, adv);
    }

}
