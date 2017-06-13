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
import mods.railcraft.common.gui.widgets.IIndicatorController;
import mods.railcraft.common.gui.widgets.IndicatorController;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import java.lang.invoke.MethodHandles;

public final class EntityCartRF extends CartBase {
    private static final DataParameter<Integer> RF = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.VARINT);
    private static final int RF_CAP = 2000000;
    public final IIndicatorController rfIndicator = new IndicatorController() {
        private int rf;

        @Override
        protected void refreshToolTip() {
            tip.text = String.format("%,d / %,d RF", rf, getMaxRF());
        }

        @Override
        public double getMeasurement() {
            double e = Math.min(rf, getMaxRF());
            return e / getMaxRF();
        }

        @Override
        public void setClientValue(double value) {
            rf = (int) value;
        }

        @Override
        public double getServerValue() {
            return getRF();
        }
    };

    public EntityCartRF(World world) {
        super(world);
    }

    public EntityCartRF(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.REDSTONE_FLUX;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RF, 0);
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
        dataManager.set(RF, amount);
    }

    public int getRF() {
        return dataManager.get(RF);
    }

    public int getMaxRF() {
        return RF_CAP;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isClient(worldObj))
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
//        return Blocks.REDSTONE_BLOCK;
//    }

}
