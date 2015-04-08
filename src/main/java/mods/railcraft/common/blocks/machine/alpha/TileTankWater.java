/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileTank;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.gui.slots.SlotWaterOrEmpty;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileTankWater extends TileTank implements ISidedInventory {
    private final static int OUTPUT_RATE = 40;
    private final static int TANK_CAPACITY = FluidHelper.BUCKET_VOLUME * 400;
    private final static int REFILL_INTERVAL = 8;
    private final static float REFILL_RATE = 10f;
    private final static float REFILL_PENALTY_INSIDE = 0.5f;
    private final static float REFILL_PENALTY_SNOW = 0.5f;
    private final static float REFILL_BOOST_RAIN = 3.0f;
    private final static byte REFILL_RATE_MIN = 1;
    private final static int SLOT_INPUT = 0;
    private final static int SLOT_OUTPUT = 1;
    private final static int[] SLOTS = InvTools.buildSlotArray(0, 2);
    private final static ForgeDirection[] LIQUID_OUTPUTS = {ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH};
    private final static ITileFilter LIQUID_OUTPUT_FILTER = new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            if (tile instanceof TileTank)
                return false;
            else if (tile instanceof IFluidHandler)
                return true;
            return false;
        }
    };
    private final static List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final FilteredTank tank;

    static {
        char[][][] map = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'A', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },};
        patterns.add(new MultiBlockPattern(map, 2, 1, 2));
    }

    private IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1);
    private IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1);

    public TileTankWater() {
        super("gui.tank.water", 2, patterns);
        tank = new FilteredTank(TANK_CAPACITY, Fluids.WATER.get(), this);
        tankManager.add(tank);
    }

    public static void placeWaterTank(World world, int x, int y, int z, int water) {
        for (MultiBlockPattern pattern : TileTankWater.patterns) {
            Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
            blockMapping.put('B', EnumMachineAlpha.TANK_WATER.ordinal());
            TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineAlpha(), blockMapping);
            if (tile instanceof TileTankWater) {
                TileTankWater master = (TileTankWater) tile;
                master.tank.setFluid(Fluids.WATER.get(water));
            }
            return;
        }
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.TANK_WATER;
    }

    @Override
    public String getTitle() {
        return LocalizationPlugin.translate("railcraft.gui.tank.water");
    }

    @Override
    public Slot getInputSlot(IInventory inv, int id, int x, int y) {
        return new SlotWaterOrEmpty(inv, id, x, y);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        if (Game.isHost(worldObj)) {
            if (isStructureValid() && FluidHelper.handleRightClick(getTankManager(), ForgeDirection.getOrientation(side), player, true, true))
                return true;
        } else if (FluidItemHelper.isContainer(player.inventory.getCurrentItem()))
            return true;
        return super.blockActivated(player, side);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isHost(getWorld())) {
            if (isMaster()) {
                if (worldObj.provider.dimensionId != -1 && clock % REFILL_INTERVAL == 0) {
                    float rate = REFILL_RATE;
                    BiomeGenBase biome = worldObj.getBiomeGenForCoords(xCoord, zCoord);
                    float humidity = biome.rainfall;
                    rate *= humidity;
//                    String debug = "Biome=" + biome.biomeName + ", Humidity=" + humidity;

                    boolean outside = false;
                    for (int x = xCoord - 1; x <= xCoord + 1; x++) {
                        for (int z = zCoord - 1; z <= zCoord + 1; z++) {
                            outside = worldObj.canBlockSeeTheSky(x, yCoord + 3, z);
//                            System.out.println(x + ", " + (yCoord + 3) + ", " + z);
                            if (outside)
                                break;
                        }
                    }

//                    debug += ", Outside=" + outside;
                    if (!outside)
                        rate *= REFILL_PENALTY_INSIDE;
                    else if (worldObj.isRaining())
                        if (biome.getEnableSnow())
                            rate *= REFILL_PENALTY_SNOW; //                            debug += ", Snow=true";
                        else
                            rate *= REFILL_BOOST_RAIN; //                            debug += ", Rain=true";
                    int rateFinal = MathHelper.floor_float(rate);
                    if (rateFinal < REFILL_RATE_MIN)
                        rateFinal = REFILL_RATE_MIN;
//                    debug += ", Refill=" + rateFinal;
//                    System.out.println(debug);

                    FluidStack fillStack = Fluids.WATER.get(rateFinal);
                    fill(ForgeDirection.UP, fillStack, true);
                }

                if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
                    FluidHelper.processContainers(tankManager.get(0), this, SLOT_INPUT, SLOT_OUTPUT);
            }

            TankManager tMan = getTankManager();
            if (tMan != null)
                tMan.outputLiquid(tileCache, LIQUID_OUTPUT_FILTER, LIQUID_OUTPUTS, 0, OUTPUT_RATE);
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.TANK, player, worldObj, mBlock.xCoord, mBlock.yCoord, mBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    public IIcon getIcon(int side) {
        return EnumMachineAlpha.TANK_WATER.getTexture(side);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (from != ForgeDirection.UP || resource == null || !Fluids.WATER.is(resource))
            return 0;
        return super.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !Fluids.WATER.is(resource))
            return null;
        return super.drain(from, resource.amount, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return from == ForgeDirection.UP && Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return from != ForgeDirection.UP && Fluids.WATER.is(fluid);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        switch (slot) {
            case SLOT_INPUT:
                return FluidItemHelper.isRoomInContainer(stack, Fluids.WATER.get()) || FluidItemHelper.containsFluid(stack, Fluids.WATER.get());
        }
        return false;
    }
}
