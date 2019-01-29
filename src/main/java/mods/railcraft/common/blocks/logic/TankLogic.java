/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.fluids.IFluidHandlerImplementor;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.FluidUtil;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TankLogic extends Logic implements IFluidHandlerImplementor {
    protected final TankManager tankManager = new TankManager();

    public TankLogic(Adapter adapter) {
        super(adapter);
    }

    public TankLogic addTank(StandardTank tank) {
        tankManager.add(tank);
        return this;
    }

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public boolean interact(EntityPlayer player, EnumHand hand) {
        return FluidUtil.interactWithFluidHandler(player, hand, getTankManager())
                || super.interact(player, hand);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }
}
