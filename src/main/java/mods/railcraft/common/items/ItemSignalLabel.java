package mods.railcraft.common.items;

import mods.railcraft.api.signals.AbstractPair;
import mods.railcraft.api.signals.IControllerTile;
import mods.railcraft.api.signals.IReceiverTile;
import mods.railcraft.api.signals.ISignalBlockTile;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Vexatos
 */
public class ItemSignalLabel extends ItemRailcraft {

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(new ItemStack(this), Items.paper, "nuggetSteel");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (Game.isHost(worldIn) && playerIn.isSneaking() && stack.hasDisplayName()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            Set<AbstractPair> pairs = new HashSet<AbstractPair>();
            if (tile instanceof IReceiverTile) {
                pairs.add(((IReceiverTile) tile).getReceiver());
            }
            if (tile instanceof IControllerTile) {
                pairs.add(((IControllerTile) tile).getController());
            }
            if(tile instanceof ISignalBlockTile) {
                pairs.add(((ISignalBlockTile) tile).getSignalBlock());
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
                    --stack.stackSize;
                    PlayerPlugin.swingItem(playerIn);
                    worldIn.markBlockForUpdate(pos);
                    return true;
                }
            }
        }
        return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
    }
}
