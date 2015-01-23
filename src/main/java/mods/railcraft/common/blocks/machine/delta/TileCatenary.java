package mods.railcraft.common.blocks.machine.delta;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import mods.railcraft.api.electricity.IElectricDistributor;
import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.api.electricity.IElectricGrid.ChargeHandler.ConnectType;
import mods.railcraft.common.blocks.machine.BoundingBoxManager;
import mods.railcraft.common.blocks.machine.BoundingBoxManager.BoundingBox;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.BoundingBoxManager.ReducedBoundingBox;
import mods.railcraft.common.blocks.machine.delta.TileWire.AddonType;
import mods.railcraft.common.plugins.forge.WorldPlugin;
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
    
    public static class CatenaryBoundingBox extends BoundingBox {

        @Override
        public AxisAlignedBB getBox(World world, int x, int y, int z) {
            return AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.625, z + 0.3125, x + 0.6875, y + 1, z + 0.6875);
        }

    }

}
