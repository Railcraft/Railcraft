package mods.railcraft.common.blocks.machine.delta;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import mods.railcraft.api.electricity.IElectricDistributor;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.electricity.IElectricGrid.ChargeHandler.ConnectType;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.delta.TileWire.AddonType;
import mods.railcraft.common.util.misc.Game;

public class TileCatenary extends TileMachineBase implements IElectricDistributor {

	private final ChargeHandler chargeHandler = new ChargeHandler(this, 0.05);
	
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
		return EnumMachineDelta.CATENARY;
	}
	
	@Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        chargeHandler.tick();
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

}
