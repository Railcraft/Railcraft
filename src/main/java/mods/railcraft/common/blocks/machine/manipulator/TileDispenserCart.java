/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.items.IMinecartItem;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryCopy;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class TileDispenserCart extends TileManipulator {

    protected boolean powered;
    protected int timeSinceLastSpawn;

    public TileDispenserCart() {
        super(3);
    }

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.DISPENSER_CART;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.CART_DISPENSER, player, world, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (timeSinceLastSpawn < Integer.MAX_VALUE)
            timeSinceLastSpawn++;
    }

    public void onPulse() {
        EntityMinecart cart = EntitySearcher.findMinecarts().around(getPos().offset(facing)).in(world).any();
        if (cart == null) {
            if (timeSinceLastSpawn > RailcraftConfig.getCartDispenserMinDelay() * 20)
                for (int ii = 0; ii < getSizeInventory(); ii++) {
                    ItemStack cartStack = getStackInSlot(ii);
                    if (!InvTools.isEmpty(cartStack)) {
                        BlockPos pos = getPos().offset(facing);
                        boolean minecartItem = cartStack.getItem() instanceof IMinecartItem;
                        if (cartStack.getItem() instanceof ItemMinecart || minecartItem) {
                            boolean canPlace = true;
                            if (minecartItem)
                                canPlace = ((IMinecartItem) cartStack.getItem()).canBePlacedByNonPlayer(cartStack);
                            if (canPlace) {
                                ItemStack placedStack = cartStack.copy();
                                EntityMinecart placedCart = CartTools.placeCart(getOwner(), placedStack, (WorldServer) world, pos);
                                if (placedCart != null) {
                                    decrStackSize(ii, 1);
                                    timeSinceLastSpawn = 0;
                                    break;
                                }
                            }
                        } else {
                            InvTools.spewItem(cartStack, world, pos.getX(), pos.getY(), pos.getZ());
                            setInventorySlotContents(ii, ItemStack.EMPTY);
                        }
                    }
                }
        } else if (!cart.isDead && !cart.getCartItem().isEmpty()) {
            InventoryCopy testInv = new InventoryCopy(this);
            ItemStack cartStack = cart.getCartItem();
            if (cart.hasCustomName())
                cartStack.setStackDisplayName(cart.getName());
            ItemStack remainder = testInv.addStack(cartStack.copy());
            if (remainder.isEmpty()) {
                getInventory().addStack(cartStack);
                if (cart.isBeingRidden())
                    CartTools.removePassengers(cart);
                cart.setDead();
            }
        }
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block, BlockPos neighborPos) {
        super.onNeighborBlockChange(state, block, neighborPos);
        if (Game.isClient(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(world, getPos());
        if (!powered && newPower) {
            powered = true;
            onPulse();
        } else
            powered = newPower;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("powered", powered);

        data.setInteger("time", timeSinceLastSpawn);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        powered = data.getBoolean("powered");

        timeSinceLastSpawn = data.getInteger("time");
    }

    public boolean getPowered() {
        return powered;
    }

    public void setPowered(boolean power) {
        powered = power;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
}
