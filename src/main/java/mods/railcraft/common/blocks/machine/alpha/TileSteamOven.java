/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.alpha;

import buildcraft.api.statements.IActionExternal;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.blocks.machine.TileMultiBlock;
import mods.railcraft.common.blocks.machine.TileMultiBlockInventory;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.buildcraft.actions.Actions;
import mods.railcraft.common.plugins.buildcraft.triggers.IHasWork;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.sounds.SoundHelper;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static net.minecraft.util.EnumFacing.*;

@Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public class TileSteamOven extends TileMultiBlockInventory implements ISidedInventory, ISteamUser, IHasWork, ITileRotate {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private static final EnumFacing[] UP_DOWN_AXES = {UP, DOWN};
    private static final int STEAM_PER_BATCH = 8000;
    private static final int TOTAL_COOK_TIME = 256;
    private static final int COOK_STEP = 16;
    private static final int ITEMS_SMELTED = 9;
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int TANK_CAPACITY = 8 * FluidTools.BUCKET_VOLUME;
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank;
    private final InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 9);
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 9, false);
    private final Set<Object> actions = new HashSet<Object>();

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
    private boolean finishedCycle;
    private EnumFacing facing = NORTH;
    private boolean paused;

    public TileSteamOven() {
        super(18, patterns);
        tank = new FilteredTank(TANK_CAPACITY, this);
        tank.setFilter(Fluids.STEAM::get);
        tankManager.add(tank);
    }

    public static void placeSteamOven(World world, BlockPos pos, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        MultiBlockPattern pattern = TileSteamOven.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<Character, IBlockState>();
        blockMapping.put('B', EnumMachineAlpha.STEAM_OVEN.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileSteamOven) {
            TileSteamOven master = (TileSteamOven) tile;
            for (int slot = 0; slot < 9; slot++) {
                if (input != null && slot < input.size())
                    master.inv.setInventorySlotContents(TileSteamOven.SLOT_INPUT + slot, input.get(slot));
                if (output != null && slot < output.size())
                    master.inv.setInventorySlotContents(TileSteamOven.SLOT_OUTPUT + slot, output.get(slot));
            }
        }
    }

    @Override
    public EnumMachineAlpha getMachineType() {
        return EnumMachineAlpha.STEAM_OVEN;
    }

    @Nullable
    public TankManager getTankManager() {
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
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

    @Override
    @Nonnull
    public EnumFacing getFacing() {
        TileSteamOven masterOven = (TileSteamOven) getMasterBlock();
        if (masterOven != null)
            return masterOven.facing;
        return facing;
    }

    @Override
    public void setFacing(@Nonnull EnumFacing facing) {
        TileSteamOven masterOven = (TileSteamOven) getMasterBlock();
        if (masterOven != null) {
            masterOven.facing = facing;
        }
        this.facing = facing;
    }

    private boolean hasFinishedCycle() {
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        return mBlock != null && mBlock.finishedCycle;
    }

    private void setHasFinishedCycle(boolean finished) {
        if (finishedCycle != finished) {
            finishedCycle = finished;
            sendUpdateToClient();
        }
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld())) {
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
                                    SoundHelper.playSound(worldObj, null, getPos(), RailcraftSoundEvents.MECHANICAL_STEAM_BURST, SoundCategory.BLOCKS, 1F, (float) (1 + MiscTools.RANDOM.nextGaussian() * 0.1));
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
            if (stack != null && FurnaceRecipes.instance().getSmeltingResult(stack) != null)
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
                    ItemStack output = FurnaceRecipes.instance().getSmeltingResult(stack);
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
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (placer != null)
            facing = MiscTools.getHorizontalSideFacingPlayer(placer);
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
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
    public EnumFacing[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();
        if (masterBlock != null) {
            GuiHandler.openGui(EnumGui.STEAN_OVEN, player, worldObj, masterBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        data.setInteger("cookTime", cookTime);
        data.setByte("facing", (byte) facing.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
        cookTime = data.getInteger("cookTime");
        facing = EnumFacing.getFront(data.getByte("facing"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeByte(facing.ordinal());
        data.writeBoolean(finishedCycle);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        EnumFacing f = EnumFacing.getFront(data.readByte());
        finishedCycle = data.readBoolean();
        if (facing != f) {
            facing = f;
            markBlockForUpdate();
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
        return index >= SLOT_OUTPUT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isItemValidForSlot(int slot, @Nullable ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (stack == null)
            return false;
        if (slot >= SLOT_OUTPUT)
            return false;
        return FurnaceRecipes.instance().getSmeltingResult(stack) != null;
    }

    @Override
    public boolean hasWork() {
        TileSteamOven mBlock = (TileSteamOven) getMasterBlock();
        return mBlock != null && mBlock.cookTime > 0;
    }

    private void processActions() {
        paused = actions.stream().anyMatch(a -> a == Actions.PAUSE);
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

        Texture(int index) {
            this.index = index;
        }
    }
}
