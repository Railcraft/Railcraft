/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.ISignalTileBlock;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static mods.railcraft.common.util.inventory.InvTools.dec;

/**
 * @author Vexatos
 */
public class ItemSignalLabel extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(new ItemStack(this), Items.PAPER, "nuggetSteel");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (Game.isHost(worldIn) && playerIn.isSneaking() && stack.hasDisplayName()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            Set<AbstractPair> pairs = new HashSet<>();
            if (tile instanceof IReceiverTile) {
                pairs.add(((IReceiverTile) tile).getReceiver());
            }
            if (tile instanceof IControllerTile) {
                pairs.add(((IControllerTile) tile).getController());
            }
            if (tile instanceof ISignalTileBlock) {
                pairs.add(((ISignalTileBlock) tile).getSignalBlock());
            }
            if (!pairs.isEmpty()) {
                String newName = stack.getDisplayName();
                boolean done = false;
                for (AbstractPair pair : pairs) {
                    if (!newName.equals(pair.getName())) {
                        pair.setName(newName);
                        done = true;
                    }
                }
                if (done) {
                    dec(stack);
                    PlayerPlugin.swingArm(playerIn, hand);
                    IBlockState state = WorldPlugin.getBlockState(worldIn, pos);
                    worldIn.notifyBlockUpdate(pos, state, state, 3);
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
