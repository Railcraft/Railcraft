/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.blocks.RailcraftBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.alpha.TileCokeOven;
import mods.railcraft.common.blocks.machine.alpha.TileSteamOven;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.triggers.INeedsFuel;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.filters.InvertedStackFilter;
import mods.railcraft.common.util.misc.ITileFilter;
import mods.railcraft.common.util.steam.SolidFuelProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileBoilerFireboxSolid extends TileBoilerFirebox implements INeedsFuel {

    public static void placeSolidBoiler(World world, int x, int y, int z, int width, int height, boolean highPressure, int water, List<ItemStack> fuel) {
        for (MultiBlockPattern pattern : TileBoiler.patterns) {
            if (pattern.getPatternHeight() - 3 == height && pattern.getPatternWidthX() - 2 == width) {
                Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
                blockMapping.put('F', EnumMachineBeta.BOILER_FIREBOX_SOLID.ordinal());
                blockMapping.put('H', highPressure ? EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.ordinal() : EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.ordinal());
                TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineBeta(), blockMapping);
                if (tile instanceof TileBoilerFireboxSolid) {
                    TileBoilerFireboxSolid master = (TileBoilerFireboxSolid) tile;
                    master.tankWater.setFluid(Fluids.WATER.get(water));
                    IInventory invFuel = new InventoryMapper(master.inventory, SLOT_BURN, 4);
                    for (ItemStack stack : fuel) {
                        InvTools.moveItemStack(stack, invFuel);
                    }
                }
                return;
            }
        }
    }

    private static final int SLOT_BURN = 2;
    private static final int SLOT_FUEL_A = 3;
    private static final int SLOT_FUEL_B = 4;
    private static final int SLOT_FUEL_C = 5;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 6);
    private static final IStackFilter NOT_FUEL = new InvertedStackFilter(StackFilter.FUEL);
    private IInventory invBurn = new InventoryMapper(this, SLOT_BURN, 1);
    private IInventory invStock = new InventoryMapper(this, SLOT_FUEL_A, 3);
    private IInventory invFuel = new InventoryMapper(this, SLOT_BURN, 4);
    private boolean needsFuel = false;
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(this, tileCache, new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            if (tile instanceof TileSteamOven)
                return true;
            if (tile instanceof TileCokeOven)
                return true;
            if (tile instanceof TileBoiler)
                return false;
            if (tile instanceof IInventory)
                return ((IInventory) tile).getSizeInventory() >= 27;
            return false;
        }

    }, InventorySorter.SIZE_DECENDING);

    public TileBoilerFireboxSolid() {
        super(6);
        boiler.setFuelProvider(new SolidFuelProvider(this, SLOT_BURN));
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.BOILER_FIREBOX_SOLID;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.BOILER_SOLID, player, worldObj, mBlock.xCoord, mBlock.yCoord, mBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    protected void process() {
        if (clock % 4 == 0) {
            InvTools.moveOneItem(invStock, invBurn);
            InvTools.moveOneItem(invBurn, invWaterOutput, NOT_FUEL);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (worldObj.isRemote)
            return;

        if (isMaster && clock % 4 == 0)
            needsFuel = !InvTools.numItemsMoreThan(invFuel, 64);

        if (needsFuel()) {
            TileBoilerFireboxSolid mBlock = (TileBoilerFireboxSolid) getMasterBlock();

            if (mBlock != null)
                InvTools.moveOneItem(invCache.getAdjacentInventories(), mBlock.invFuel, StackFilter.FUEL);
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_LIQUID_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if(!isStructureValid())
            return false;
        if (slot >= SLOT_BURN)
            return FuelPlugin.getBurnTime(stack) > 0;
        else if (slot == SLOT_LIQUID_INPUT)
            return Fluids.WATER.isContained(stack);
        return false;
    }

    @Override
    public boolean needsFuel() {
        TileBoilerFireboxSolid mBlock = (TileBoilerFireboxSolid) getMasterBlock();
        if (mBlock != null)
            return mBlock.needsFuel;
        return false;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

}
