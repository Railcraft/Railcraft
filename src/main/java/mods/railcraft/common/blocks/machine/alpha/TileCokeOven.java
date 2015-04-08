/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockOven;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileCokeOven extends TileMultiBlockOven implements IFluidHandler, ISidedInventory {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_LIQUID_OUTPUT = 2;
    public static final int SLOT_LIQUID_INPUT = 3;
    private static final int COOK_STEP_LENGTH = 50;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 4);
    private static final int TANK_CAPACITY = 64 * FluidHelper.BUCKET_VOLUME;
    private final static List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final TankManager tankManager = new TankManager();
    private final StandardTank tank;
    private final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1);
    private final IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 2, false);
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
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'W', 'A', 'W', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
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
    public int cookTimeTotal = 3600;
    private int finishedAt;

    public TileCokeOven() {
        super("railcraft.gui.coke.oven", 4, patterns);
        tank = new StandardTank(TANK_CAPACITY, this);
        tankManager.add(tank);
    }

    public static void placeCokeOven(World world, int x, int y, int z, int creosote, ItemStack input, ItemStack output) {
        for (MultiBlockPattern pattern : TileCokeOven.patterns) {
            Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
            blockMapping.put('B', EnumMachineAlpha.COKE_OVEN.ordinal());
            blockMapping.put('W', EnumMachineAlpha.COKE_OVEN.ordinal());
            TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineAlpha(), blockMapping);
            if (tile instanceof TileCokeOven) {
                TileCokeOven master = (TileCokeOven) tile;
                master.tank.setFluid(Fluids.CREOSOTE.get(creosote));
                master.inv.setInventorySlotContents(TileCokeOven.SLOT_INPUT, input);
                master.inv.setInventorySlotContents(TileCokeOven.SLOT_OUTPUT, output);
            }
            return;
        }
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.COKE_OVEN;
    }

    public TankManager getTankManager() {
        TileCokeOven mBlock = (TileCokeOven) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public IIcon getIcon(int side) {
        if (getPatternMarker() == 'W' && isStructureValid()) {
            if (isBurning())
                return getMachineType().getTexture(7);
            return getMachineType().getTexture(6);
        }
        return getMachineType().getTexture(0);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, int side) {
        if (isStructureValid() && FluidHelper.handleRightClick(this, ForgeDirection.getOrientation(side), player, false, true))
            return true;
        else if (FluidItemHelper.isContainer(player.inventory.getCurrentItem()))
            return true;
        return super.blockActivated(player, side);
    }

    @Override
    public int getTotalCookTime() {
        TileCokeOven mBlock = (TileCokeOven) getMasterBlock();
        if (mBlock != null)
            return mBlock.cookTimeTotal;
        return 3600;
    }

    @Override
    public int getBurnProgressScaled(int i) {
        return ((getTotalCookTime() - getCookTime()) * i) / getTotalCookTime();
    }

    @Override
    public boolean needsFuel() {
        ItemStack fuel = getStackInSlot(SLOT_INPUT);
        return fuel == null || fuel.stackSize < 8;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isHost(getWorld()))
            if (isMaster()) {
                if (clock > finishedAt + COOK_STEP_LENGTH + 5)
                    if (cookTime <= 0)
                        setCooking(false);

                ItemStack input = getStackInSlot(SLOT_INPUT);
                if (input != null && input.stackSize > 0 && !InvTools.isSynthetic(input)) {
                    if (!paused && clock % COOK_STEP_LENGTH == 0) {
                        ItemStack output = getStackInSlot(SLOT_OUTPUT);
                        ICokeOvenRecipe recipe = RailcraftCraftingManager.cokeOven.getRecipe(input);

                        if (recipe != null)
                            if ((output == null || (output.isItemEqual(recipe.getOutput()) && output.stackSize + recipe.getOutput().stackSize <= output.getMaxStackSize()))
                                    && tank.fill(recipe.getFluidOutput(), false) >= recipe.getFluidOutput().amount) {
                                cookTimeTotal = recipe.getCookTime();
                                cookTime += COOK_STEP_LENGTH;
                                setCooking(true);

                                if (cookTime >= recipe.getCookTime()) {
                                    cookTime = 0;
                                    finishedAt = clock;
                                    decrStackSize(SLOT_INPUT, 1);
                                    if (output == null)
                                        setInventorySlotContents(SLOT_OUTPUT, recipe.getOutput());
                                    else
                                        output.stackSize += recipe.getOutput().stackSize;
                                    tank.fill(recipe.getFluidOutput(), true);
                                    sendUpdateToClient();
                                }
                            } else {
                                cookTime = 0;
                                setCooking(false);
                            }
                        else {
                            cookTime = 0;
                            setCooking(false);
                            setInventorySlotContents(SLOT_INPUT, null);
                            dropItem(input);
                        }
                    }
                } else {
                    cookTime = 0;
                    setCooking(false);
                }

                ItemStack topSlot = getStackInSlot(SLOT_LIQUID_INPUT);
                if (topSlot != null && !FluidItemHelper.isContainer(topSlot)) {
                    setInventorySlotContents(SLOT_LIQUID_INPUT, null);
                    dropItem(topSlot);
                }

                ItemStack bottomSlot = getStackInSlot(SLOT_LIQUID_OUTPUT);
                if (bottomSlot != null && !FluidItemHelper.isContainer(bottomSlot)) {
                    setInventorySlotContents(SLOT_LIQUID_OUTPUT, null);
                    dropItem(bottomSlot);
                }

                if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
                    FluidHelper.fillContainers(this, this, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT, Fluids.CREOSOTE.get());
            }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock != null && isStructureValid()) {
            GuiHandler.openGui(EnumGui.COKE_OVEN, player, worldObj, masterBlock.xCoord, masterBlock.yCoord, masterBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        TankManager tMan = getTankManager();
        if (tMan != null)
            return tMan.drain(0, maxDrain, doDrain);
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null)
            return null;
        if (!Fluids.CREOSOTE.is(resource))
            return null;
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid == null || Fluids.CREOSOTE.is(fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection dir) {
        TankManager tMan = getTankManager();
        if (tMan != null)
            return tMan.getTankInfo();
        return FakeTank.INFO;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        switch (slot) {
            case SLOT_INPUT:
                return RailcraftCraftingManager.cokeOven.getRecipe(stack) != null;
            case SLOT_LIQUID_INPUT:
                return FluidItemHelper.isRoomInContainer(stack);
            default:
                return false;
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
        return slot == SLOT_OUTPUT || slot == SLOT_LIQUID_OUTPUT;
    }
}
