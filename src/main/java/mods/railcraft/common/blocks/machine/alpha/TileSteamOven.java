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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockInventory;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FakeTank;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.UP;

public class TileSteamOven extends TileMultiBlockInventory implements IFluidHandler, ISidedInventory, ISteamUser, IHasWork {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private static final ForgeDirection[] UP_DOWN_AXES = new ForgeDirection[]{UP, DOWN};
    private static final int STEAM_PER_BATCH = 8000;
    private static final int TOTAL_COOK_TIME = 256;
    private static final int COOK_STEP = 16;
    private static final int ITEMS_SMELTED = 9;
    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int TANK_CAPACITY = 8 * FluidHelper.BUCKET_VOLUME;
    private final static List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final TankManager tankManager = new TankManager();
    private final StandardTank tank;
    private final IInventory invInput = new InventoryMapper(this, SLOT_INPUT, 9);
    private final IInventory invOutput = new InventoryMapper(this, SLOT_OUTPUT, 9, false);
    private final Set<IActionExternal> actions = new HashSet<IActionExternal>();
    static {
        char[][][] map = {
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},},
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'*', 'O', 'O', '*'}
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'*', 'O', 'O', '*'}
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},},};
        patterns.add(new MultiBlockPattern(map));
    }
    public int cookTime;
    public boolean finishedCycle = false;
    private ForgeDirection facing = ForgeDirection.NORTH;
    private boolean paused = false;
    public TileSteamOven() {
        super("railcraft.gui.steam.oven", 18, patterns);
        tank = new FilteredTank(TANK_CAPACITY, Fluids.STEAM.get(), this);
        tankManager.add(tank);
    }

    public static void placeSteamOven(World world, int x, int y, int z, List<ItemStack> input, List<ItemStack> output) {
        for (MultiBlockPattern pattern : TileSteamOven.patterns) {
            Map<Character, Integer> blockMapping = new HashMap<Character, Integer>();
            blockMapping.put('B', EnumMachineAlpha.STEAM_OVEN.ordinal());
            TileEntity tile = pattern.placeStructure(world, x, y, z, RailcraftBlocks.getBlockMachineAlpha(), blockMapping);
            if (tile instanceof TileSteamOven) {
                TileSteamOven master = (TileSteamOven) tile;
                for (int slot = 0; slot < 9; slot++) {
                    if (input != null && slot < input.size())
                        master.inv.setInventorySlotContents(TileSteamOven.SLOT_INPUT + slot, input.get(slot));
                    if (output != null && slot < output.size())
                        master.inv.setInventorySlotContents(TileSteamOven.SLOT_OUTPUT + slot, output.get(slot));
                }
            }
            return;
        }
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.STEAM_OVEN;
    }

    public TankManager getTankManager() {
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public IIcon getIcon(int side) {
        if (isStructureValid() && side == getFacing().ordinal())
            switch (side) {
                case 2:
                    if (getPatternPositionY() == 2) {
                        if (getPatternPositionX() == 2)
                            return Texture.DOOR_TL.getIcon();
                        return Texture.DOOR_TR.getIcon();
                    }
                    if (getPatternPositionX() == 2)
                        return Texture.DOOR_BL.getIcon();
                    return Texture.DOOR_BR.getIcon();
                case 3:
                    if (getPatternPositionY() == 2) {
                        if (getPatternPositionX() == 1)
                            return Texture.DOOR_TL.getIcon();
                        return Texture.DOOR_TR.getIcon();
                    }
                    if (getPatternPositionX() == 1)
                        return Texture.DOOR_BL.getIcon();
                    return Texture.DOOR_BR.getIcon();
                case 4:
                    if (getPatternPositionY() == 2) {
                        if (getPatternPositionZ() == 1)
                            return Texture.DOOR_TL.getIcon();
                        return Texture.DOOR_TR.getIcon();
                    }
                    if (getPatternPositionZ() == 1)
                        return Texture.DOOR_BL.getIcon();
                    return Texture.DOOR_BR.getIcon();
                case 5:
                    if (getPatternPositionY() == 2) {
                        if (getPatternPositionZ() == 2)
                            return Texture.DOOR_TL.getIcon();
                        return Texture.DOOR_TR.getIcon();
                    }
                    if (getPatternPositionZ() == 2)
                        return Texture.DOOR_BL.getIcon();
                    return Texture.DOOR_BR.getIcon();
            }
        if (side > 1)
            return Texture.SIDE.getIcon();
        return Texture.CAP.getIcon();
    }

    public int getCookProgressScaled(int i) {
        int scale = (getCookTime() * i) / TOTAL_COOK_TIME;
        scale = Math.min(scale, i);
        scale = Math.max(scale, 0);
        return scale;
    }

    public int getCookTime() {
        TileSteamOven masterOven = (TileSteamOven) getMasterBlock();
        if (masterOven != null)
            return masterOven.cookTime;
        return -1;
    }

    public ForgeDirection getFacing() {
        TileSteamOven masterOven = (TileSteamOven) getMasterBlock();
        if (masterOven != null)
            return masterOven.facing;
        return facing;
    }

    public boolean hasFinishedCycle() {
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        return mBlock != null && mBlock.finishedCycle;
    }

    public void setHasFinishedCycle(boolean finished) {
        if (finishedCycle != finished) {
            finishedCycle = finished;
            sendUpdateToClient();
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld())) {
            if (hasFinishedCycle())
                EffectManager.instance.steamEffect(worldObj, this, +0.25);
            return;
        }

        if (isMaster()) {
            if (clock % 16 == 0)
                processActions();
            if (clock % COOK_STEP == 0) {
                setHasFinishedCycle(false);
                if (!paused)
                    if (hasRecipe()) {
                        if (cookTime <= 0 && drainSteam())
                            cookTime = 1;
                        else if (cookTime > 0) {
                            cookTime += COOK_STEP;
                            if (cookTime >= TOTAL_COOK_TIME)
                                if (smeltItems()) {
                                    cookTime = 0;
                                    setHasFinishedCycle(true);
                                    SoundHelper.playSound(worldObj, xCoord, yCoord, zCoord, SoundHelper.SOUND_STEAM_BURST, 1, (float) (1 + MiscTools.getRand().nextGaussian() * 0.1));
                                }
                        }
                    } else
                        cookTime = 0;
            }
        }
    }

    private boolean drainSteam() {
        FluidStack steam = tank.drain(STEAM_PER_BATCH, false);
        if (steam != null && steam.amount >= STEAM_PER_BATCH) {
            tank.drain(STEAM_PER_BATCH, true);
            return true;
        }
        return false;
    }

    private boolean hasRecipe() {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = invInput.getStackInSlot(slot);
            if (stack != null && FurnaceRecipes.smelting().getSmeltingResult(stack) != null)
                return true;
        }
        return false;
    }

    private boolean smeltItems() {
        int count = 0;
        boolean changed = true;
        boolean smelted = false;
        while (count < ITEMS_SMELTED && changed) {
            changed = false;
            for (int slot = 0; slot < 9 && count < ITEMS_SMELTED; slot++) {
                ItemStack stack = invInput.getStackInSlot(slot);
                if (stack != null) {
                    ItemStack output = FurnaceRecipes.smelting().getSmeltingResult(stack);
                    if (output != null && InvTools.isRoomForStack(output, invOutput)) {
                        ItemStack remainder = InvTools.moveItemStack(output.copy(), invOutput);
                        if (remainder == null) {
                            invInput.decrStackSize(slot, 1);
                            changed = true;
                            count++;
                        }
                    }
                }
            }
            smelted |= changed;
        }
        return smelted;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        facing = MiscTools.getHorizontalSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (axis == UP || axis == DOWN)
            return false;
        TileSteamOven master = (TileSteamOven) getMasterBlock();
        if (master != null) {
            if (master.facing == axis)
                master.facing = axis.getOpposite();
            else
                master.facing = axis;
            master.scheduleMasterRetest();
            return true;
        }
        return false;
    }

    @Override
    public ForgeDirection[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock != null) {
            GuiHandler.openGui(EnumGui.STEAN_OVEN, player, worldObj, masterBlock.xCoord, masterBlock.yCoord, masterBlock.zCoord);
            return true;
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        data.setInteger("cookTime", cookTime);
        data.setByte("facing", (byte) facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
        cookTime = data.getInteger("cookTime");
        facing = ForgeDirection.getOrientation(data.getByte("facing"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(facing.ordinal());
        data.writeBoolean(finishedCycle);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        byte f = data.readByte();
        finishedCycle = data.readBoolean();
        if (f != facing.ordinal()) {
            facing = ForgeDirection.getOrientation(f);
            markBlockForUpdate();
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null) return 0;
        TankManager tMan = getTankManager();
        if (tMan == null)
            return 0;
        return tMan.fill(0, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid == null || Fluids.STEAM.is(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection dir) {
        TankManager tMan = getTankManager();
        if (tMan != null)
            return tMan.getTankInfo();
        return FakeTank.INFO;
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
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        return slot >= SLOT_OUTPUT;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (stack == null)
            return false;
        if (slot >= SLOT_OUTPUT)
            return false;
        return FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
    }

    @Override
    public boolean hasWork() {
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        if (mBlock != null)
            return mBlock.cookTime > 0;
        return false;
    }

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
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        if (mBlock != null)
            mBlock.actions.add(action);
    }

    enum Texture {

        DOOR_TL(6), DOOR_TR(7), DOOR_BL(8), DOOR_BR(9), SIDE(2), CAP(0);
        private final int index;

        private Texture(int index) {
            this.index = index;
        }

        public IIcon getIcon() {
            return EnumMachineAlpha.STEAM_OVEN.getTexture(index);
        }

    }
}
