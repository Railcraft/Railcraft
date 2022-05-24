/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
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
import mods.railcraft.common.blocks.logic.BoilerLogic;
import mods.railcraft.common.blocks.logic.InventoryLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileBoilerFireboxSolid extends TileBoilerFirebox {

    public static final int SLOT_FIREBOX = 3;
    public static final int SLOT_BUNKER_A = 4;
    public static final int SLOT_BUNKER_B = 5;
    public static final int SLOT_BUNKER_C = 6;
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> {
        if (tile instanceof TileSteamOven)
            return true;
        if (tile instanceof TileCokeOven)
            return true;
        if (tile instanceof TileBoiler)
            return false;
        return InventoryComposite.of(tile).slotCount() >= 27;
    }, InventorySorter.SIZE_DESCENDING);

    public TileBoilerFireboxSolid() {
        getLogic(StructureLogic.class)
                .flatMap(structureLogic -> structureLogic.getKernel(BoilerLogic.class))
                .ifPresent(boilerLogic -> {
                    InventoryLogic invLogic = new InventoryLogic(Logic.Adapter.of(this), 7) {
                        @Override
                        public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
                            return new InvWrapper(this) {
                                @Nonnull
                                @Override
                                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                                    if (slot != SLOT_OUTPUT_FLUID)
                                        return ItemStack.EMPTY;
                                    return super.extractItem(slot, amount, simulate);
                                }
                            };
                        }

                        @Override
                        public boolean isItemValidForSlot(int slot, ItemStack stack) {
                            if (!super.isItemValidForSlot(slot, stack))
                                return false;
                            if (slot >= SLOT_FIREBOX)
                                return FuelPlugin.getBurnTime(stack) > 0;
                            else if (slot == SLOT_INPUT_FLUID)
                                return Fluids.WATER.isContained(stack);
                            return false;
                        }
                    };

                    boilerLogic.addLogic(invLogic);
                    InventoryMapper firebox = InventoryMapper.make(invLogic, SLOT_FIREBOX, 1);
                    InventoryMapper bunker = InventoryMapper.make(invLogic, SLOT_BUNKER_A, 3);
                    InventoryMapper output = InventoryMapper.make(invLogic, SLOT_OUTPUT_FLUID, 1);
                    boilerLogic.setFuelProvider(new SolidFuelProvider(firebox, bunker, output) {
                        @Override
                        public void manageFuel() {
                            super.manageFuel();
                            if (needsFuel())
                                invCache.getAdjacentInventories().moveOneItemTo(bunker, StackFilters.FUEL);
                        }
                    });
                });
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
    public EnumGui getGui() {
        return EnumGui.BOILER_SOLID;
    }
}
