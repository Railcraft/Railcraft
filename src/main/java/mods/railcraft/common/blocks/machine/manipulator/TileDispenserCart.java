/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryCopy;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
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
        GuiHandler.openGui(EnumGui.CART_DISPENSER, player, worldObj, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (timeSinceLastSpawn < Integer.MAX_VALUE)
            timeSinceLastSpawn++;
    }

    public void onPulse() {
        EntityMinecart cart = CartToolsAPI.getMinecartOnSide(worldObj, getPos(), 0, facing);
        if (cart == null) {
            if (timeSinceLastSpawn > RailcraftConfig.getCartDispenserMinDelay() * 20)
                for (int ii = 0; ii < getSizeInventory(); ii++) {
                    ItemStack cartStack = getStackInSlot(ii);
                    if (cartStack != null) {
                        BlockPos pos = getPos().offset(facing);
                        boolean minecartItem = cartStack.getItem() instanceof IMinecartItem;
                        if (cartStack.getItem() instanceof ItemMinecart || minecartItem) {
                            boolean canPlace = true;
                            if (minecartItem)
                                canPlace = ((IMinecartItem) cartStack.getItem()).canBePlacedByNonPlayer(cartStack);
                            if (canPlace) {
                                ItemStack placedStack = cartStack.copy();
                                EntityMinecart placedCart = CartTools.placeCart(getOwner(), placedStack, (WorldServer) worldObj, pos);
                                if (placedCart != null) {
                                    decrStackSize(ii, 1);
                                    timeSinceLastSpawn = 0;
                                    break;
                                }
                            }
                        } else {
                            float rx = MiscTools.RANDOM.nextFloat() * 0.8F + 0.1F;
                            float ry = MiscTools.RANDOM.nextFloat() * 0.8F + 0.1F;
                            float rz = MiscTools.RANDOM.nextFloat() * 0.8F + 0.1F;
                            EntityItem item = new EntityItem(worldObj, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, cartStack);
                            float factor = 0.05F;
                            item.motionX = (float) MiscTools.RANDOM.nextGaussian() * factor;
                            item.motionY = (float) MiscTools.RANDOM.nextGaussian() * factor + 0.2F;
                            item.motionZ = (float) MiscTools.RANDOM.nextGaussian() * factor;
                            if (worldObj.spawnEntityInWorld(item))
                                setInventorySlotContents(ii, null);
                        }
                    }
                }
        } else if (!cart.isDead && cart.getCartItem() != null) {
            InventoryCopy testInv = new InventoryCopy(this);
            ItemStack cartStack = cart.getCartItem();
            if (cart.hasCustomName())
                cartStack.setStackDisplayName(cart.getName());
            ItemStack remainder = InvTools.moveItemStack(cartStack.copy(), testInv);
            if (remainder == null) {
                InvTools.moveItemStack(cartStack, this);
                if (cart.isBeingRidden())
                    CartTools.removePassengers(cart);
                cart.setDead();
            }
        }
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, Block block) {
        super.onNeighborBlockChange(state, block);
        if (Game.isClient(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
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
