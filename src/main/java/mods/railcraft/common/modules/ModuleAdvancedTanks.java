package mods.railcraft.common.modules;

import java.util.HashMap;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.tank.GenericMultiTankBase;
import mods.railcraft.common.blocks.machine.zeta.EnumMachineEta;
import mods.railcraft.common.blocks.machine.zeta.EnumMachineZeta;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.item.ItemStack;

public class ModuleAdvancedTanks extends RailcraftModule {

	public static GenericMultiTankBase ALUMINIUM;
	public static GenericMultiTankBase STAINLESS;
	public static GenericMultiTankBase TITANIUM;
	public static GenericMultiTankBase TUNGSTENSTEEL;
	public static GenericMultiTankBase PALLADIUM;
	public static GenericMultiTankBase IRIDIUM;
	public static GenericMultiTankBase OSMIUM;
	public static GenericMultiTankBase NEUTRONIUM;

    public final static int CAPACITY_PER_BLOCK_ALUMINIUM = 64 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_STAINLESS = 128 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_TITANIUM = 256 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_TUNGSTENSTEEL = 512 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_PALLADIUM = 1024 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_IRIDIUM = 1536 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_OSMIUM = 2048 * FluidHelper.BUCKET_VOLUME;
    public final static int CAPACITY_PER_BLOCK_NEUTRONIUM = 3072 * FluidHelper.BUCKET_VOLUME;

    public static final HashMap<String, IEnumMachine> cacheTankType = new HashMap<String, IEnumMachine>();
    public static final HashMap<String, GenericMultiTankBase> cacheTankMaterial = new HashMap<String, GenericMultiTankBase>();
	
	public static void initTanks() {
		ALUMINIUM = createTank("aluminium", CAPACITY_PER_BLOCK_ALUMINIUM, EnumMachineZeta.TANK_ALUMINIUM_WALL, EnumMachineZeta.TANK_ALUMINIUM_GAUGE, EnumMachineZeta.TANK_ALUMINIUM_VALVE);
		STAINLESS = createTank("stainless", CAPACITY_PER_BLOCK_STAINLESS, EnumMachineZeta.TANK_STAINLESS_WALL, EnumMachineZeta.TANK_STAINLESS_GAUGE, EnumMachineZeta.TANK_STAINLESS_VALVE);
		TITANIUM = createTank("titanium", CAPACITY_PER_BLOCK_TITANIUM, EnumMachineZeta.TANK_TITANIUM_WALL, EnumMachineZeta.TANK_TITANIUM_GAUGE, EnumMachineZeta.TANK_TITANIUM_VALVE);
		TUNGSTENSTEEL = createTank("tungstensteel", CAPACITY_PER_BLOCK_TUNGSTENSTEEL, EnumMachineZeta.TANK_TUNGSTENSTEEL_WALL, EnumMachineZeta.TANK_TUNGSTENSTEEL_GAUGE, EnumMachineZeta.TANK_TUNGSTENSTEEL_VALVE);
		PALLADIUM = createTank("palladium", CAPACITY_PER_BLOCK_PALLADIUM, EnumMachineZeta.TANK_PALLADIUM_WALL, EnumMachineZeta.TANK_PALLADIUM_GAUGE, EnumMachineZeta.TANK_PALLADIUM_VALVE);
		IRIDIUM = createTank("iridium", CAPACITY_PER_BLOCK_IRIDIUM, EnumMachineEta.TANK_IRIDIUM_WALL, EnumMachineEta.TANK_IRIDIUM_GAUGE, EnumMachineEta.TANK_IRIDIUM_VALVE);
		OSMIUM = createTank("osmium", CAPACITY_PER_BLOCK_OSMIUM, EnumMachineEta.TANK_OSMIUM_WALL, EnumMachineEta.TANK_OSMIUM_GAUGE, EnumMachineEta.TANK_OSMIUM_VALVE);
		NEUTRONIUM = createTank("neutronium", CAPACITY_PER_BLOCK_NEUTRONIUM, EnumMachineEta.TANK_NEUTRONIUM_WALL, EnumMachineEta.TANK_NEUTRONIUM_GAUGE, EnumMachineEta.TANK_NEUTRONIUM_VALVE);
	}

	@Override
	public void initFirst() {
		initTanks();
		initTankOfType(ALUMINIUM);
		initTankOfType(STAINLESS);
		initTankOfType(TITANIUM);
		initTankOfType(TUNGSTENSTEEL);		
		initTankOfType(PALLADIUM);
		initTankOfType(IRIDIUM);
		initTankOfType(OSMIUM);
		initTankOfType(NEUTRONIUM);
	}
	
    private void initTankOfType(GenericMultiTankBase tankType) {
        defineTank(tankType.TANK_WALL);
        defineTank(tankType.TANK_GAUGE);
        defineTank(tankType.TANK_VALVE);
    }
    
    private boolean defineTank(IEnumMachine type, Object... recipe) {    	
    	if (type instanceof EnumMachineZeta) {
    		if (((EnumMachineZeta) type).register()) {
                addColorRecipes(type);
                return true;
            }
    	}
    	else if (type instanceof EnumMachineEta) {
    		if (((EnumMachineEta) type).register()) {
                addColorRecipes(type);
                return true;
            }
    	}       
        return false;
    }
    
    private void addColorRecipes(IEnumMachine type) {
        for (EnumColor color : EnumColor.VALUES) {
            ItemStack output = getColorTank(type, color, 8);
            CraftingPlugin.addShapedRecipe(output,
                    "OOO",
                    "ODO",
                    "OOO",
                    'O', type.getItem(),
                    'D', color.getDye());
        }
    }

    private ItemStack getColorTank(IEnumMachine type, EnumColor color, int qty) {
        ItemStack stack = type.getItem(qty);
        return InvTools.setItemColor(stack, color);
    }
	
	private static GenericMultiTankBase createTank(String material, int capacity, IEnumMachine tankWall, IEnumMachine tankGauge, IEnumMachine tankValve) {
		GenericMultiTankBase tank = new GenericMultiTankBase(material, capacity, tankWall, tankValve, tankGauge);
		cacheTankMaterial.put(tankWall.getTag(), tank);
		cacheTankMaterial.put(tankGauge.getTag(), tank);
		cacheTankMaterial.put(tankValve.getTag(), tank);
		return tank;
	}
}
