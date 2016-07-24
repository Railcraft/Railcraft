/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.wire;

import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileWire extends RailcraftTileEntity implements IElectricGrid {

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.WIRE, 0.25);

//    @Override
//    public void update() {
//        super.update();
//
//        if (Game.isClient(getWorld()))
//            return;
//
//        chargeHandler.tick();
//    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }
}
