/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.api.crafting.ICokeOvenRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockOven;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileCokeOven extends TileMultiBlockOven implements ISidedInventory {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_LIQUID_OUTPUT = 2;
    public static final int SLOT_LIQUID_INPUT = 3;
    private static final int COOK_STEP_LENGTH = 50;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 4);
    private static final int TANK_CAPACITY = 64 * FluidTools.BUCKET_VOLUME;
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final TankManager tankManager = new TankManager();
    private final StandardTank tank;
//    private final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 1);
//    private final IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 2, false);

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
        super(4, patterns);
        tank = new StandardTank(TANK_CAPACITY, this);
        tankManager.add(tank);
    }

    public static void placeCokeOven(World world, BlockPos pos, int creosote, ItemStack input, ItemStack output) {
        MultiBlockPattern pattern = TileCokeOven.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<Character, IBlockState>();
        blockMapping.put('B', EnumMachineAlpha.COKE_OVEN.getDefaultState());
        blockMapping.put('W', EnumMachineAlpha.COKE_OVEN.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileCokeOven) {
            TileCokeOven master = (TileCokeOven) tile;
            master.tank.setFluid(Fluids.CREOSOTE.get(creosote));
            master.inv.setInventorySlotContents(TileCokeOven.SLOT_INPUT, input);
            master.inv.setInventorySlotContents(TileCokeOven.SLOT_OUTPUT, output);
        }
    }

    @Override
    public EnumMachineAlpha getMachineType() {
        return EnumMachineAlpha.COKE_OVEN;
    }

    @Nullable
    public TankManager getTankManager() {
        TileCokeOven mBlock = (TileCokeOven) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) getTankManager();
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        return (isStructureValid() && FluidTools.interactWithFluidHandler(heldItem, getTankManager(), player)) || super.blockActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
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
    public void update() {
        super.update();

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

                // FIXME
//                if (clock % FluidTools.BUCKET_FILL_TIME == 0)
//                    FluidTools.fillContainers(this, this, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT, Fluids.CREOSOTE.get());
            }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock != null && isStructureValid()) {
            GuiHandler.openGui(EnumGui.COKE_OVEN, player, worldObj, masterBlock.getPos());
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
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        return data;
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
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == SLOT_OUTPUT || index == SLOT_LIQUID_OUTPUT;
    }
}
