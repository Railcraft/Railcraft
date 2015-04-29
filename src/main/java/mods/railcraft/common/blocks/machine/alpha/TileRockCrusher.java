/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import buildcraft.api.statements.IActionExternal;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockInventory;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.inventory.wrappers.InventoryCopy;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import mods.railcraft.common.util.sounds.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileRockCrusher extends TileMultiBlockInventory implements IEnergyHandler, IHasWork, ISidedInventory {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private final static int PROCESS_TIME = 100;
    private final static int CRUSHING_POWER_COST_PER_TICK = 160;
    private final static int SUCKING_POWER_COST = 5000;
    private final static int KILLING_POWER_COST = 10000;
    private final static int MAX_RECEIVE = 5000;
    private final static int MAX_ENERGY = CRUSHING_POWER_COST_PER_TICK * PROCESS_TIME;
    private final static int[] SLOTS_INPUT = InvTools.buildSlotArray(SLOT_INPUT, 9);
    private final static int[] SLOTS_OUTPUT = InvTools.buildSlotArray(SLOT_OUTPUT, 9);
    private final static List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final IInventory invInput = new InventoryMapper(this, 0, 9);
    private final IInventory invOutput = new InventoryMapper(this, 9, 9, false);
    private final Set<IActionExternal> actions = new HashSet<IActionExternal>();
    static {
        char[][][] map1 = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'D', 'B', 'O'},
                        {'O', 'B', 'D', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'a', 'd', 'f', 'O'},
                        {'O', 'c', 'e', 'h', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map1));

        char[][][] map2 = {
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'D', 'D', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'a', 'f', 'O'},
                        {'O', 'b', 'g', 'O'},
                        {'O', 'c', 'h', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                }
        };
        patterns.add(new MultiBlockPattern(map2));
    }
    private int processTime;
    private EnergyStorage energyStorage;
    private boolean isWorking = false;
    private boolean paused = false;

    public TileRockCrusher() {
        super(EnumMachineAlpha.ROCK_CRUSHER.getTag() + ".name", 18, patterns);

        if (RailcraftConfig.machinesRequirePower())
            energyStorage = new EnergyStorage(MAX_ENERGY, MAX_RECEIVE, KILLING_POWER_COST);
    }

    public static void placeRockCrusher(World world, int x, int y, int z, int patternIndex, List<ItemStack> input, List<ItemStack> output) {
        MultiBlockPattern pattern = TileRockCrusher.patterns.get(patternIndex);
        Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
        blockMapping.put('B', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('D', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('a', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('b', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('c', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('d', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('e', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('f', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        blockMapping.put('h', EnumMachineAlpha.ROCK_CRUSHER.ordinal());
        TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineAlpha(), blockMapping);
        if (tile instanceof TileRockCrusher) {
            TileRockCrusher master = (TileRockCrusher) tile;
            for (int slot = 0; slot < 9; slot++) {
                if (input != null && slot < input.size())
                    master.inv.setInventorySlotContents(TileRockCrusher.SLOT_INPUT + slot, input.get(slot));
                if (output != null && slot < output.size())
                    master.inv.setInventorySlotContents(TileRockCrusher.SLOT_OUTPUT + slot, output.get(slot));
            }
        }
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.ROCK_CRUSHER;
    }

    @Override
    protected boolean isMapPositionValid(int x, int y, int z, char mapPos) {
        Block block = WorldPlugin.getBlock(worldObj, x, y, z);
        switch (mapPos) {
            case 'O': // Other
                if (block == getBlockType() && worldObj.getBlockMetadata(x, y, z) == getBlockMetadata())
                    return false;
                break;
            case 'D': // Window
            case 'B': // Block
            case 'a': // Block
            case 'b': // Block
            case 'c': // Block
            case 'd': // Block
            case 'e': // Block
            case 'f': // Block
            case 'g': // Block
            case 'h': // Block
                if (block != getBlockType() || worldObj.getBlockMetadata(x, y, z) != getBlockMetadata())
                    return false;
                break;
            case 'A': // Air
                if (!worldObj.isAirBlock(x, y, z))
                    return false;
                break;
        }
        return true;
    }

    private boolean useMasterEnergy(int amount, boolean doRemove) {
        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
        if (mBlock != null)
            if (mBlock.energyStorage == null)
                return true;
            else
                return mBlock.energyStorage.extractEnergy(amount, !doRemove) == amount;
        return false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isHost(getWorld())) {

            if (isStructureValid()) {
                EntityItem item = TileEntityHopper.func_145897_a(worldObj, xCoord, yCoord + 1, zCoord);
                if (item != null && useMasterEnergy(SUCKING_POWER_COST, false)) {
                    ItemStack stack = item.getEntityItem().copy();
                    if (InventoryManipulator.get(invInput).addStack(stack) != null)
                        useMasterEnergy(SUCKING_POWER_COST, true);
                    item.setDead();
                }

                EntityLivingBase entity = MiscTools.getEntityAt(worldObj, EntityLivingBase.class, xCoord, yCoord + 1, zCoord);
                if (entity != null && useMasterEnergy(KILLING_POWER_COST, false))
                    if (entity.attackEntityFrom(RailcraftDamageSource.CRUSHER, 10))
                        useMasterEnergy(KILLING_POWER_COST, true);
            }

            if (isMaster()) {
                if (clock % 16 == 0)
                    processActions();

                if (paused)
                    return;

                ItemStack input = null;
                IRockCrusherRecipe recipe = null;
                for (int i = 0; i < 9; i++) {
                    input = invInput.getStackInSlot(i);
                    if (input != null) {
                        recipe = RailcraftCraftingManager.rockCrusher.getRecipe(input);
                        if (recipe != null)
                            break;
                    }
                }

                if (recipe != null)
                    if (processTime >= PROCESS_TIME) {
                        isWorking = false;
                        IInventory tempInv = new InventoryCopy(invOutput);
                        boolean hasRoom = true;
                        List<ItemStack> outputs = recipe.getRandomizedOuputs();
                        for (ItemStack output : outputs) {
                            output = InvTools.moveItemStack(output, tempInv);
                            if (output != null) {
                                hasRoom = false;
                                break;
                            }
                        }

                        if (hasRoom) {
                            for (ItemStack output : outputs) {
                                InvTools.moveItemStack(output, invOutput);
                            }

                            InvTools.removeOneItem(invInput, input);

                            SoundHelper.playSound(worldObj, xCoord, yCoord, zCoord, "mob.irongolem.death", 1.0f, worldObj.rand.nextFloat() * 0.25F + 0.7F);

                            processTime = 0;
                        }
                    } else {
                        isWorking = true;
                        if (energyStorage != null) {
                            int energy = energyStorage.extractEnergy(CRUSHING_POWER_COST_PER_TICK, true);
                            if (energy >= CRUSHING_POWER_COST_PER_TICK) {
                                processTime++;
                                energyStorage.extractEnergy(CRUSHING_POWER_COST_PER_TICK, false);
                            }
                        } else
                            processTime++;
                    }
                else {
                    processTime = 0;
                    isWorking = false;
                }
            }
        }
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock mBlock = getMasterBlock();
        if (mBlock != null) {
            GuiHandler.openGui(EnumGui.ROCK_CRUSHER, player, worldObj, mBlock.xCoord, mBlock.yCoord, mBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    public IIcon getIcon(int side) {
        if (isStructureValid()) {
            if (side > 1 && getPatternMarker() == 'D')
                return getMachineType().getTexture(6);
            if (side == 1) {
                char m = getPatternMarker();
                return getMachineType().getTexture(m - 'a' + 7);
            }
        }
        if (side > 1)
            return getMachineType().getTexture(0);
        return getMachineType().getTexture(side);
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("processTime", processTime);

        if (energyStorage != null)
            energyStorage.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        processTime = data.getInteger("processTime");

        if (energyStorage != null)
            energyStorage.readFromNBT(data);
    }

    public int getProcessTime() {
        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
        if (mBlock != null)
            return mBlock.processTime;
        return -1;
    }

    public void setProcessTime(int processTime) {
        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
        if (mBlock != null)
            mBlock.processTime = processTime;
    }

    public int getProgressScaled(int i) {
        return (getProcessTime() * i) / PROCESS_TIME;
    }

    @Override
    public boolean hasWork() {
        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
        if (mBlock != null)
            return mBlock.isWorking;
        return false;
    }

    //    public void setPaused(boolean p) {
//        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
//        if (mBlock != null) {
//            mBlock.paused = p;
//        }
//    }
    private void processActions() {
        paused = false;
        for (IActionExternal action : actions) {
            if (action == Actions.PAUSE)
                paused = true;
        }
        actions.clear();
    }

    @Override
    public void actionActivated(IActionExternal action) {
        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
        if (mBlock != null)
            mBlock.actions.add(action);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == ForgeDirection.UP.ordinal())
            return SLOTS_INPUT;
        return SLOTS_OUTPUT;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot >= 9;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (slot < 9)
            return RailcraftCraftingManager.rockCrusher.getRecipe(stack) != null;
        return false;
    }

    public EnergyStorage getEnergyStorage() {
        TileRockCrusher mBlock = (TileRockCrusher) getMasterBlock();
        if (mBlock != null && mBlock.energyStorage != null)
            return mBlock.energyStorage;
        return energyStorage;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (getEnergyStorage() == null)
            return 0;
        return getEnergyStorage().receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        if (getEnergyStorage() == null)
            return 0;
        return getEnergyStorage().getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        if (getEnergyStorage() == null)
            return 0;
        return getEnergyStorage().getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return RailcraftConfig.machinesRequirePower();
    }
}
