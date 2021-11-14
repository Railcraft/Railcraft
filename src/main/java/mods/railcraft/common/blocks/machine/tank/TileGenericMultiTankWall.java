/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.tank;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.beta.MetalTank;
import mods.railcraft.common.blocks.machine.beta.TileTankIronWall;
import mods.railcraft.common.modules.ModuleAdvancedTanks;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileGenericMultiTankWall extends TileTankIronWall {
 
    private MetalTank tankType;
	private IEnumMachine wallType;


	public TileGenericMultiTankWall() {
		
	}
	
	public TileGenericMultiTankWall(MetalTank thisTankType, IEnumMachine thisWallType) {
		tankType = thisTankType;
		wallType = thisWallType;
		markDirty();
	}
	
    @Override
    public IEnumMachine getMachineType() {
        return wallType;
    }

    @Override
    public MetalTank getTankType() {
        return tankType;
    }

    @Override
    public int getCapacityPerBlock() {
        return wallType.getCapacity();
    }

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setString("Machine.Type", wallType.getTag());
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		wallType = ModuleAdvancedTanks.cacheTankType.get(data.getString("Machine.Type"));
		tankType = ModuleAdvancedTanks.cacheTankMaterial.get(data.getString("Machine.Type"));
		super.readFromNBT(data);
	}
	
}
