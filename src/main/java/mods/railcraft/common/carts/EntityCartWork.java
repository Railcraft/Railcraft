/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EntityCartWork extends CartBase {

    public EntityCartWork(World world) {
        super(world);
    }

    public EntityCartWork(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.WORK;
    }

    @Override
    public boolean doInteract(EntityPlayer entityplayer) {
        if (Game.isHost(worldObj)) {
            GuiHandler.openGui(EnumGui.CART_WORK, entityplayer, worldObj, this);
        }
        return true;
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.CRAFTING_TABLE.getDefaultState();
    }
}
