package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class TileAdminFeeder extends TileMachineBase implements IElectricGrid {

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.0);

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        double capacity = chargeHandler.getCapacity();
        try {
            chargeHandler.setCharge(capacity);
        } catch (Error err) {
            chargeHandler.addCharge(capacity - chargeHandler.getCharge());
            Game.logErrorAPI("Railcraft", err, IElectricGrid.class);
        }
        chargeHandler.tick();
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineEpsilon.ADMIN_FEEDER;
    }

    @Override
    public IIcon getIcon(int side) {
        return getMachineType().getTexture(0);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
    }

}
