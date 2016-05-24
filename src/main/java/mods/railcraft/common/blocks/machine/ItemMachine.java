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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMachine extends ItemBlockRailcraftMultiType {

    private final BlockMachine<? extends IEnumMachine<?>> machineBlock;

    public ItemMachine(Block block) {
        super(block);
        this.machineBlock = (BlockMachine<? extends IEnumMachine<?>>) block;
        setUnlocalizedName("railcraft.machine");
    }

    private IEnumMachine<?> getMachine(ItemStack stack) {
        return machineBlock.getMachineProxy().getMetaMap().get(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getMachine(stack).getTag();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 3))
            return false;

        if (world.getBlockState(pos).getBlock() == machineBlock) {
            machineBlock.onBlockPlacedBy(world, pos, newState, player, stack);
            machineBlock.initFromItem(world, pos, stack);
        }

        return true;
    }

    @Override
    public ToolTip getToolTip(ItemStack stack, EntityPlayer player, boolean adv) {
        return getMachine(stack).getToolTip(stack, player, adv);
    }
}
