/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryComposite;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.steam.SolidFuelProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerFireboxSolid extends TileBoilerFirebox {

    private static final int SLOT_BURN = 2;
    private static final int SLOT_FUEL_A = 3;
    private static final int SLOT_FUEL_B = 4;
    private static final int SLOT_FUEL_C = 5;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 6);
    private static final Predicate<ItemStack> NOT_FUEL = StackFilters.FUEL.negate();
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> {
        if (tile instanceof TileSteamOven)
            return true;
        if (tile instanceof TileCokeOven)
            return true;
        if (tile instanceof TileBoiler)
            return false;
        return InventoryComposite.of(tile).slotCount() >= 27;
    }, InventorySorter.SIZE_DESCENDING);
    private final InventoryMapper invBurn = InventoryMapper.make(this, SLOT_BURN, 1);
    private final InventoryMapper invStock = InventoryMapper.make(this, SLOT_FUEL_A, 3);
    private final InventoryMapper invFuel = InventoryMapper.make(this, SLOT_BURN, 4);
    private boolean needsFuel;

    public TileBoilerFireboxSolid() {
        super(6);
        boiler.setFuelProvider(new SolidFuelProvider(this, SLOT_BURN));
    }

    public static void placeSolidBoiler(World world, BlockPos pos, int width, int height, boolean highPressure, int water, List<ItemStack> fuel) {
        for (StructurePattern pattern : TileBoiler.patterns) {
            if (pattern.getPatternHeight() - 3 == height && pattern.getPatternWidthX() - 2 == width) {
                Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
                blockMapping.put('F', RailcraftBlocks.BOILER_FIREBOX_SOLID.getDefaultState());
                blockMapping.put('H', highPressure ? RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH.getDefaultState() : RailcraftBlocks.BOILER_TANK_PRESSURE_LOW.getDefaultState());
                Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
                //FIXME
//                if (tile instanceof TileBoilerFireboxSolid) {
//                    TileBoilerFireboxSolid master = (TileBoilerFireboxSolid) tile;
//                    master.tankWater.setFluid(Fluids.WATER.get(water));
//                    InventoryMapper masterFuel = InventoryMapper.make(master.inventory, SLOT_BURN, 4);
//                    for (ItemStack stack : fuel) {
//                        masterFuel.addStack(stack);
//                    }
//                }
                return;
            }
        }
    }

    @Override
    protected void process() {
        if (clock % 4 == 0) {
            invStock.moveOneItemTo(invBurn);
            invBurn.moveOneItemTo(invWaterOutput, NOT_FUEL);
        }
    }

    @Override
    public void update() {
        super.update();

        if (world.isRemote)
            return;

        if (isMaster && clock % 4 == 0)
            needsFuel = invFuel.countItems() < 64;

        if (needsFuel()) {
            TileBoilerFireboxSolid mBlock = (TileBoilerFireboxSolid) getMasterBlock();

            if (mBlock != null)
                invCache.getAdjacentInventories().moveOneItemTo(mBlock.invFuel, StackFilters.FUEL);
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
        TileBoilerFireboxSolid mBlock = (TileBoilerFireboxSolid) getMasterBlock();
        return mBlock != null && mBlock.needsFuel;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.BOILER_SOLID;
    }
}
