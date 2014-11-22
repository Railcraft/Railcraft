/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryCopy;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.WorldServer;

public class TileDispenserCart extends TileMachineItem {

    protected ForgeDirection direction = ForgeDirection.NORTH;
    protected boolean powered;
    protected int timeSinceLastSpawn;

    public TileDispenserCart() {
        super(3);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.DISPENSER_CART;
    }

    @Override
    public IIcon getIcon(int side) {
        if (direction.ordinal() == side)
            return getMachineType().getTexture(3);
        if (side != 0 && side != 1)
            return getMachineType().getTexture(2);
        return getMachineType().getTexture(1);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.CART_DISPENSER, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        direction = MiscTools.getSideFacingTrack(worldObj, xCoord, yCoord, zCoord);
        if (direction == ForgeDirection.UNKNOWN)
            direction = MiscTools.getSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (timeSinceLastSpawn < Integer.MAX_VALUE)
            timeSinceLastSpawn++;
    }

    public void onPulse() {
        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0, direction);
        if (cart == null) {
            if (timeSinceLastSpawn > RailcraftConfig.getCartDispenserMinDelay() * 20)
                for (int ii = 0; ii < getSizeInventory(); ii++) {
                    ItemStack cartStack = getStackInSlot(ii);
                    if (cartStack != null) {
                        int x = MiscTools.getXOnSide(xCoord, direction);
                        int y = MiscTools.getYOnSide(yCoord, direction);
                        int z = MiscTools.getZOnSide(zCoord, direction);
                        boolean minecartItem = cartStack.getItem() instanceof IMinecartItem;
                        if (cartStack.getItem() instanceof ItemMinecart || minecartItem) {
                            boolean canPlace = true;
                            if (minecartItem)
                                canPlace = ((IMinecartItem) cartStack.getItem()).canBePlacedByNonPlayer(cartStack);
                            if (canPlace) {
                                ItemStack placedStack = cartStack.copy();
                                EntityMinecart placedCart = CartUtils.placeCart(getOwner(), placedStack, (WorldServer) worldObj, x, y, z);
                                if (placedCart != null) {
                                    decrStackSize(ii, 1);
                                    timeSinceLastSpawn = 0;
                                    break;
                                }
                            }
                        } else {
                            float rx = MiscTools.getRand().nextFloat() * 0.8F + 0.1F;
                            float ry = MiscTools.getRand().nextFloat() * 0.8F + 0.1F;
                            float rz = MiscTools.getRand().nextFloat() * 0.8F + 0.1F;
                            EntityItem item = new EntityItem(worldObj, x + rx, y + ry, z + rz, cartStack);
                            float factor = 0.05F;
                            item.motionX = (float) MiscTools.getRand().nextGaussian() * factor;
                            item.motionY = (float) MiscTools.getRand().nextGaussian() * factor + 0.2F;
                            item.motionZ = (float) MiscTools.getRand().nextGaussian() * factor;
                            if (worldObj.spawnEntityInWorld(item))
                                setInventorySlotContents(ii, null);
                        }
                    }
                }
        } else if (!cart.isDead && cart.getCartItem() != null) {
            IInventory testInv = new InventoryCopy(this);
            ItemStack cartStack = cart.getCartItem();
            if (cart.hasCustomInventoryName())
                cartStack.setStackDisplayName(cart.getCommandSenderName());
            ItemStack remainder = InvTools.moveItemStack(cartStack.copy(), testInv);
            if (remainder == null) {
                InvTools.moveItemStack(cartStack, this);
                if (cart.riddenByEntity != null)
                    cart.riddenByEntity.mountEntity(null);
                cart.setDead();
            }
        }
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        if (Game.isNotHost(getWorld()))
            return;
        boolean newPower = PowerPlugin.isBlockBeingPowered(worldObj, xCoord, yCoord, zCoord);
        if (!powered && newPower) {
            powered = newPower;
            onPulse();
        } else
            powered = newPower;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("powered", powered);
        data.setByte("direction", (byte) direction.ordinal());

        data.setInteger("time", timeSinceLastSpawn);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        powered = data.getBoolean("powered");
        direction = ForgeDirection.getOrientation(data.getByte("direction"));

        timeSinceLastSpawn = data.getInteger("time");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte(direction.ordinal());
//        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);

        direction = ForgeDirection.getOrientation(data.readByte());
//        powered = data.readBoolean();

        markBlockForUpdate();
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
