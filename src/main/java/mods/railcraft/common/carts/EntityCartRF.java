/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class EntityCartRF extends CartBase {

    private static final int RF_CAP = 2000000;
    private int amountRF;

    public EntityCartRF(World world) {
        super(world);
    }

    public EntityCartRF(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    public int addRF(int rf) {
        if (rf <= 0)
            return 0;
        if (amountRF >= RF_CAP)
            return 0;
        if (RF_CAP - amountRF >= rf) {
            amountRF += rf;
            return rf;
        }
        int used = RF_CAP - amountRF;
        amountRF = RF_CAP;
        return used;
    }

    public int removeRF(int request) {
        if (request <= 0)
            return 0;
        if (amountRF >= request) {
            amountRF -= request;
            return request;
        }
        int ret = amountRF;
        amountRF = 0;
        return ret;
    }

    public int getRF() {
        return amountRF;
    }

    public int getMaxRF() {
        return RF_CAP;
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isNotHost(worldObj))
            return;

        if (amountRF > RF_CAP)
            amountRF = RF_CAP;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_RF, player, worldObj, this);
        return true;
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Override
    public final float getMaxCartSpeedOnRail() {
        int numLocomotives = Train.getTrain(this).getNumRunningLocomotives();
        if (numLocomotives == 0)
            return super.getMaxCartSpeedOnRail();
        return Math.min(1.2F, 0.08F + (numLocomotives - 1) * 0.075F);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        amountRF = nbt.getInteger("rf");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("rf", amountRF);
    }

    @Override
    public Block func_145820_n() {
        return Blocks.redstone_block;
    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }
}
