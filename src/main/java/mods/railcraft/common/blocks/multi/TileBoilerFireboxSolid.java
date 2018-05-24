/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryFactory;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.steam.SolidFuelProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerFireboxSolid extends TileBoilerFirebox<TileBoilerFireboxSolid> {

    private static final int SLOT_BURN = 2;
    private static final int SLOT_FUEL_A = 3;
    private static final int SLOT_FUEL_B = 4;
    private static final int SLOT_FUEL_C = 5;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 6);
    private static final Predicate<ItemStack> NOT_FUEL = StandardStackFilters.FUEL.negate();
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> {
        if (tile instanceof TileSteamOven)
            return true;
        if (tile instanceof TileCokeOven)
            return true;
        if (tile instanceof TileBoiler)
            return false;
        IInventoryObject inventoryObject = InventoryFactory.get(tile);
        return inventoryObject != null && inventoryObject.getNumSlots() >= 27;
    }, InventorySorter.SIZE_DESCENDING);
    private InventoryMapper invBurn = InventoryMapper.make(this, SLOT_BURN, 1);
    private InventoryMapper invStock = InventoryMapper.make(this, SLOT_FUEL_A, 3);
    private InventoryMapper invFuel = InventoryMapper.make(this, SLOT_BURN, 4);
    private boolean needsFuel;

    public TileBoilerFireboxSolid() {
        super(6);
        boiler.setFuelProvider(new SolidFuelProvider(this, SLOT_BURN));
    }

    public static void placeSolidBoiler(World world, BlockPos pos, int width, int height, boolean highPressure, int water, List<ItemStack> fuel) {
        for (MultiBlockPattern pattern : TileBoiler.patterns) {
            if (pattern.getPatternHeight() - 3 == height && pattern.getPatternWidthX() - 2 == width) {
                Map<Character, IBlockState> blockMapping = new HashMap<>();
                //TODO
//                blockMapping.put('F', EnumMachineBeta.BOILER_FIREBOX_SOLID.getDefaultState());
//                blockMapping.put('H', highPressure ? EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getDefaultState() : EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getDefaultState());
                TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
                if (tile instanceof TileBoilerFireboxSolid) {
                    TileBoilerFireboxSolid master = (TileBoilerFireboxSolid) tile;
                    master.tankWater.setFluid(Fluids.WATER.get(water));
                    InventoryMapper masterFuel = InventoryMapper.make(master.inventory, SLOT_BURN, 4);
                    for (ItemStack stack : fuel) {
                        InvTools.moveItemStack(stack, masterFuel);
                    }
                }
                return;
            }
        }
    }

//    @Override
//    public EnumMachineBeta getMachineType() {
//        return EnumMachineBeta.BOILER_FIREBOX_SOLID;
//    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileBoilerFireboxSolid mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.BOILER_SOLID, player, world, mBlock.getPos());
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
    public void update() {
        super.update();

        if (world.isRemote)
            return;

        if (isMaster && clock % 4 == 0)
            needsFuel = !InvTools.numItemsMoreThan(invFuel, 64);

        if (needsFuel()) {
            TileBoilerFireboxSolid mBlock = getMasterBlock();

            if (mBlock != null)
                InvTools.moveOneItem(invCache.getAdjacentInventories(), mBlock.invFuel, StandardStackFilters.FUEL);
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_LIQUID_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!isStructureValid())
            return false;
        if (slot >= SLOT_BURN)
            return FuelPlugin.getBurnTime(stack) > 0;
        else if (slot == SLOT_LIQUID_INPUT)
            return Fluids.WATER.isContained(stack);
        return false;
    }

    @Override
    public boolean needsFuel() {
        TileBoilerFireboxSolid mBlock = getMasterBlock();
        return mBlock != null && mBlock.needsFuel;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @NotNull
    @Override
    public EnumGui getGui() {
        return EnumGui.BOILER_SOLID;
    }
}
