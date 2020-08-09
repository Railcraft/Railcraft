/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
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
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class FluidLogic extends Logic implements IFluidHandlerImplementor {
    private static final int NETWORK_UPDATE_INTERVAL = 64;
    protected final TankManager tankManager = new TankManager();
    private Set<Integer> tanksToSync = new HashSet<>();
    private boolean hidden;

    public FluidLogic(Adapter adapter) {
        super(adapter);
    }

    public FluidLogic addTank(StandardTank tank) {
        tankManager.add(tank);
        return this;
    }

    public FluidLogic setTankSync(int tankIndex) {
        tanksToSync.add(tankIndex);
        return this;
    }

    public FluidLogic setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    protected void updateServer() {
        super.updateServer();

        if (clock(NETWORK_UPDATE_INTERVAL))
            sendUpdateToClient();
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

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte((byte) tanksToSync.size());
        for (int tankIndex : tanksToSync) {
            data.writeInt(tankIndex);
            data.writeFluidStack(tankManager.get(tankIndex).getFluid());
        }
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        int dataSize = data.readByte();
        for (int ii = 0; ii < dataSize; ii++) {
            int tankIndex = data.readInt();
            FluidStack fluidStack = data.readFluidStack();
            tankManager.get(tankIndex).setFluid(fluidStack);
        }
    }
}
