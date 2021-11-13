package mods.railcraft.common.blocks.machine.zeta;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.beta.MetalTank;
import mods.railcraft.common.blocks.machine.tank.GenericMultiTankBase;
import mods.railcraft.common.fluids.FluidHelper;

public class AdvancedMultiTanks {

	public static MetalTank ALUMINIUM;
	public static MetalTank STAINLESS;
	public static MetalTank TITANIUM;
	public static MetalTank TUNGSTENSTEEL;
	public static MetalTank PAlLADIUM;
	public static MetalTank IRIDIUM;
	public static MetalTank OSMIUM;
	public static MetalTank NEUTRONIUM;

    public final static int CAPACITY_PER_BLOCK_ALUMINIUM = 64 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_STAINLESS = 96 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_TITANIUM = 128 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_TUNGSTENSTEEL = 160 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_PAlLADIUM = 192 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_IRIDIUM = 256 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_OSMIUM = 384 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_NEUTRONIUM = 512 * FluidHelper.BUCKET_VOLUME;
	
	public static void initTanks() {
		ALUMINIUM = createTank("aluminium", CAPACITY_PER_BLOCK_ALUMINIUM, EnumMachineZeta.TANK_ALUMINIUM_WALL, EnumMachineZeta.TANK_ALUMINIUM_GAUGE, EnumMachineZeta.TANK_ALUMINIUM_VALVE);
		STAINLESS = createTank("stainless", CAPACITY_PER_BLOCK_STAINLESS, EnumMachineZeta.TANK_STAINLESS_WALL, EnumMachineZeta.TANK_STAINLESS_GAUGE, EnumMachineZeta.TANK_STAINLESS_VALVE);
		TITANIUM = createTank("titanium", CAPACITY_PER_BLOCK_TITANIUM, EnumMachineZeta.TANK_TITANIUM_WALL, EnumMachineZeta.TANK_TITANIUM_GAUGE, EnumMachineZeta.TANK_TITANIUM_VALVE);
		TUNGSTENSTEEL = createTank("tungstensteel", CAPACITY_PER_BLOCK_TUNGSTENSTEEL, EnumMachineZeta.TANK_TUNGSTENSTEEL_WALL, EnumMachineZeta.TANK_TUNGSTENSTEEL_GAUGE, EnumMachineZeta.TANK_TUNGSTENSTEEL_VALVE);
		PAlLADIUM = createTank("palladium", CAPACITY_PER_BLOCK_PAlLADIUM, EnumMachineZeta.TANK_PALLADIUM_WALL, EnumMachineZeta.TANK_PALLADIUM_GAUGE, EnumMachineZeta.TANK_PALLADIUM_VALVE);
		IRIDIUM = createTank("iridium", CAPACITY_PER_BLOCK_IRIDIUM, EnumMachineEta.TANK_IRIDIUM_WALL, EnumMachineEta.TANK_IRIDIUM_GAUGE, EnumMachineEta.TANK_IRIDIUM_VALVE);
		OSMIUM = createTank("osmium", CAPACITY_PER_BLOCK_OSMIUM, EnumMachineEta.TANK_OSMIUM_WALL, EnumMachineEta.TANK_OSMIUM_GAUGE, EnumMachineEta.TANK_OSMIUM_VALVE);
		NEUTRONIUM = createTank("neutronium", CAPACITY_PER_BLOCK_NEUTRONIUM, EnumMachineEta.TANK_NEUTRONIUM_WALL, EnumMachineEta.TANK_NEUTRONIUM_GAUGE, EnumMachineEta.TANK_NEUTRONIUM_VALVE);
	}
	
	private static MetalTank createTank(String material, int capacity, IEnumMachine tankWall, IEnumMachine tankGauge, IEnumMachine tankValve) {
		return new GenericMultiTankBase(material, capacity, tankWall, tankValve, tankGauge);   
	}
	
}
