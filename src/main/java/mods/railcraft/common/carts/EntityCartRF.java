/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.ICartContentsTextureProvider;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class EntityCartRF extends CartBase implements ICartContentsTextureProvider {
    private static final int DATA_ID_RF = 25;
    private static final int RF_CAP = 2000000;

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

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(DATA_ID_RF, 0);
    }

    public int addRF(int amount) {
        if (amount <= 0)
            return 0;
        if (getRF() >= RF_CAP)
            return 0;
        if (RF_CAP - getRF() >= amount) {
            setRF(getRF() + amount);
            return amount;
        }
        int used = RF_CAP - getRF();
        setRF(RF_CAP);
        return used;
    }

    public int removeRF(int request) {
        if (request <= 0)
            return 0;
        if (getRF() >= request) {
            setRF(getRF() - request);
            return request;
        }
        int ret = getRF();
        setRF(0);
        return ret;
    }

    public void setRF(int amount) {
        dataWatcher.updateObject(DATA_ID_RF, amount);
    }

    public int getRF() {
        return dataWatcher.getWatchableObjectInt(DATA_ID_RF);
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

        if (getRF() > RF_CAP)
            setRF(RF_CAP);
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
        setRF(nbt.getInteger("rf"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("rf", getRF());
    }

//    @Override
//    public Block func_145820_n() {
//        return Blocks.redstone_block;
//    }

    @Override
    public double getDrag() {
        return CartConstants.STANDARD_DRAG;
    }

    @Override
    public IIcon getBlockTextureOnSide(int side) {
        return null;
    }
}
