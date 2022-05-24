/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.fuel.INeedsFuel;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

/**
 * Referenced by CrafterLogic to determine if a crafter is ready for operation.
 * Created by CovertJaguar on 7/21/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class FurnaceLogic extends Logic implements INeedsFuel {
    /**
     * The number of ticks that the furnace will keep burning
     */

    @SyncToGui
    private double burnTime;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would
     * keep the furnace burning for
     */
    @SyncToGui
    private double currentItemBurnTime;

    protected FurnaceLogic(Adapter adapter) {
        super(adapter);
    }

    public void loadFuel() {}

    /**
     * Indicates the furnace is at operating temperature, if such a thing is modelled.
     *
     * Called by CrafterLogic
     *
     * @return is hot
     */
    public boolean isHot() {
        return isBurning();
    }

    /**
     * Indicates the furnace is consuming fuel.
     *
     * @return is burning
     */
    public boolean isBurning() {
        return getBurnTime() > 0.0;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setDouble("burnTime", getBurnTime());
        data.setDouble("currentItemBurnTime", getCurrentItemBurnTime());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        setBurnTime(data.getDouble("burnTime"));
        setCurrentItemBurnTime(data.getDouble("currentItemBurnTime"));
    }

    @Override
    public void writeGuiData(RailcraftOutputStream data) throws IOException {
        super.writeGuiData(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeDouble(getBurnTime());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        setBurnTime(data.readDouble());
    }

    public int getBurnProgressScaled(int i) {
        if (getBurnTime() <= 0 || getCurrentItemBurnTime() <= 0)
            return 0;
        double scale = getBurnTime() * i / getCurrentItemBurnTime();
        scale = Math.min(scale, i);
        scale = Math.max(scale, 0);
        return MathHelper.ceil(scale);
    }

    /**
     * The number of ticks that the furnace will keep burning
     */
    public double getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(double burnTime) {
        burnTime = Math.max(0, burnTime);
        boolean wasBurning = isBurning();
        this.burnTime = burnTime;
        if (wasBurning != isBurning())
            sendUpdateOrUpdateModels();
    }

    /**
     * The number of ticks that a fresh copy of the currently-burning item would
     * keep the furnace burning for
     */
    public double getCurrentItemBurnTime() {
        return currentItemBurnTime;
    }

    public void setCurrentItemBurnTime(double currentItemBurnTime) {
        this.currentItemBurnTime = currentItemBurnTime;
    }
}
