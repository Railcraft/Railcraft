/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockOven;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileBlastFurnace extends TileMultiBlockOven implements ISidedInventory {
    public static final IStackFilter INPUT_FILTER = new IStackFilter() {
        @Override
        public boolean matches(ItemStack stack) {
            return RailcraftCraftingManager.blastFurnace.getRecipe(stack) != null;
        }
    };
    public static final IStackFilter FUEL_FILTER = new IStackFilter() {
        @Override
        public boolean matches(ItemStack stack) {
            return stack != null && InvTools.isItemEqual(stack, RailcraftCraftingManager.blastFurnace.getFuels());
        }
    };
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int FUEL_PER_TICK = 5;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 3);
    private final static List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final IInventory invFuel = new InventoryMapper(this, SLOT_FUEL, 1);
    private final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1);
    private final IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1);
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(this, tileCache, new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            if (tile instanceof TileBlastFurnace)
                return false;
            if (tile instanceof IInventory)
                return ((IInventory) tile).getSizeInventory() >= 27;
            return false;
        }
    }, InventorySorter.SIZE_DECENDING);
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
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'W', 'B', 'W', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
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
                }
        };
        patterns.add(new MultiBlockPattern(map, 2, 1, 2));
    }
    /**
     * The number of ticks that the furnace will keep burning
     */
    public int burnTime = 0;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would
     * keep the furnace burning for
     */
    public int currentItemBurnTime = 0;
    public boolean clientBurning = false;
    private int finishedAt;

    public TileBlastFurnace() {
        super("railcraft.gui.blast.furnace", 3, patterns);
    }

    public static void placeBlastFurnace(World world, int x, int y, int z, ItemStack input, ItemStack output, ItemStack fuel) {
        for (MultiBlockPattern pattern : TileBlastFurnace.patterns) {
            Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
            blockMapping.put('B', EnumMachineAlpha.BLAST_FURNACE.ordinal());
            blockMapping.put('W', EnumMachineAlpha.BLAST_FURNACE.ordinal());
            TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineAlpha(), blockMapping);
            if (tile instanceof TileBlastFurnace) {
                TileBlastFurnace master = (TileBlastFurnace) tile;
                master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_INPUT, input);
                master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_OUTPUT, output);
            }
            return;
        }
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.BLAST_FURNACE;
    }

    @Override
    public IIcon getIcon(int side) {
        if (side > 1 && getPatternMarker() == 'W' && isStructureValid()) {
            if (isBurning())
                return getMachineType().getTexture(7);
            return getMachineType().getTexture(6);
        }
        return getMachineType().getTexture(0);
    }

    @Override
    protected boolean isMapPositionValid(int i, int j, int k, char mapPos) {
        Block block = worldObj.getBlock(i, j, k);
        switch (mapPos) {
            case 'O':
                if (block != RailcraftBlocks.getBlockMachineAlpha() || worldObj.getBlockMetadata(i, j, k) != getBlockMetadata())
                    return true;
                break;
            case 'B':
            case 'W':
                if (block == RailcraftBlocks.getBlockMachineAlpha() && worldObj.getBlockMetadata(i, j, k) == getBlockMetadata())
                    return true;
                break;
            case 'A':
                if (block.isAir(worldObj, i, j, k) || block.getMaterial() == Material.lava)
                    return true;
                break;
        }
        return false;
    }

    @Override
    public int getTotalCookTime() {
        ItemStack input = getStackInSlot(SLOT_INPUT);
        IBlastFurnaceRecipe recipe = RailcraftCraftingManager.blastFurnace.getRecipe(input);
        if (recipe != null)
            return recipe.getCookTime();
        return 1;
    }

    @Override
    public int getBurnProgressScaled(int i) {
        if (burnTime <= 0 || currentItemBurnTime <= 0)
            return 0;
        int scale = burnTime * i / currentItemBurnTime;
        scale = Math.min(scale, i);
        scale = Math.max(scale, 0);
        return scale;
    }

    private void setLavaIdle() {
        int xLava = xCoord + 1;
        int yLava = yCoord + 1;
        int zLava = zCoord + 1;
        if (worldObj.isAirBlock(xLava, yLava, zLava))
            worldObj.setBlock(xLava, yLava, zLava, Blocks.lava, 7, 3);
    }

    private void setLavaBurn() {
        int xLava = xCoord + 1;
        int yLava = yCoord + 1;
        int zLava = zCoord + 1;
        if (worldObj.isAirBlock(xLava, yLava, zLava))
            worldObj.setBlock(xLava, yLava, zLava, Blocks.flowing_lava, 1, 3);
        yLava += 1;
        if (worldObj.isAirBlock(xLava, yLava, zLava))
            worldObj.setBlock(xLava, yLava, zLava, Blocks.flowing_lava, 1, 3);
    }

    //    private void destroyLava() {
//        int xLava = xCoord + 1;
//        int yLava = yCoord + 2;
//        int zLava = zCoord + 1;
//        if (worldObj.getBlock(xLava, yLava, zLava).getMaterial() == Material.lava)
//            worldObj.setBlockToAir(xLava, yLava, zLava);
//        yLava -= 1;
//        if (worldObj.getBlock(xLava, yLava, zLava).getMaterial() == Material.lava)
//            worldObj.setBlockToAir(xLava, yLava, zLava);
//    }
    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        TileBlastFurnace mBlock = (TileBlastFurnace) getMasterBlock();

        if (mBlock != null)
            InvTools.moveOneItem(invCache.getAdjacentInventories(), mBlock.invFuel, FUEL_FILTER);

        if (isMaster()) {
            boolean wasBurning = isBurning();
            if (clock > finishedAt + 10)
                if (cookTime <= 0)
                    setCooking(false);

            if (burnTime >= FUEL_PER_TICK)
                burnTime -= FUEL_PER_TICK;
            else
                burnTime = 0;

            if (isBurning())
                setLavaBurn();
            else
                setLavaIdle();

            ItemStack input = getStackInSlot(SLOT_INPUT);
            if (input != null && input.stackSize > 0) {

                ItemStack output = getStackInSlot(SLOT_OUTPUT);
                IBlastFurnaceRecipe recipe = RailcraftCraftingManager.blastFurnace.getRecipe(input);

                if (recipe != null && recipe.isRoomForOutput(output)) {
                    if (paused) return;

                    if (burnTime <= FUEL_PER_TICK * 2) {
                        ItemStack fuel = getStackInSlot(SLOT_FUEL);
                        if (FUEL_FILTER.matches(fuel)) {
                            int itemBurnTime = FuelPlugin.getBurnTime(fuel);
                            if (itemBurnTime > 0) {
                                currentItemBurnTime = itemBurnTime + burnTime;
                                burnTime = currentItemBurnTime;
                                setInventorySlotContents(SLOT_FUEL, InvTools.depleteItem(fuel));
                            }
                        }
                    }

                    if (isBurning()) {
                        cookTime++;
                        setCooking(true);

                        if (cookTime >= recipe.getCookTime()) {
                            cookTime = 0;
                            finishedAt = clock;
                            if (output == null)
                                setInventorySlotContents(SLOT_OUTPUT, recipe.getOutput());
                            else
                                output.stackSize += recipe.getOutputStackSize();
                            decrStackSize(SLOT_INPUT, 1);
                        }
                    }
                } else {
                    cookTime = 0;
                    setCooking(false);
                }
            } else {
                cookTime = 0;
                setCooking(false);
            }

            if (wasBurning != isBurning())
                sendUpdateToClient();
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock != null) {
            GuiHandler.openGui(EnumGui.BLAST_FURNACE, player, worldObj, masterBlock.xCoord, masterBlock.yCoord, masterBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("burnTime", burnTime);
        data.setInteger("currentItemBurnTime", currentItemBurnTime);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        burnTime = data.getInteger("burnTime");
        currentItemBurnTime = data.getInteger("currentItemBurnTime");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(burnTime > 0);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        clientBurning = data.readBoolean();
    }

    @Override
    public boolean needsFuel() {
        ItemStack fuel = getStackInSlot(SLOT_FUEL);
        return fuel == null || fuel.stackSize < 8;
    }

    @Override
    public boolean isBurning() {
        TileBlastFurnace mBlock = (TileBlastFurnace) getMasterBlock();
        if (mBlock != null)
            if (worldObj.isRemote)
                return mBlock.clientBurning;
            else
                return mBlock.burnTime > 0;
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        switch (slot) {
            case SLOT_OUTPUT:
                return false;
            case SLOT_FUEL:
                return FUEL_FILTER.matches(stack);
            case SLOT_INPUT:
                return INPUT_FILTER.matches(stack);
        }
        return false;
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
        return slot == SLOT_OUTPUT;
    }
}
