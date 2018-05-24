/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mods.railcraft.common.blocks.multi.BlockSteamOven.FACING;
import static mods.railcraft.common.blocks.multi.BlockSteamOven.ICON;
import static net.minecraft.util.EnumFacing.*;

@Optional.Interface(iface = "mods.railcraft.common.plugins.buildcraft.triggers.IHasWork", modid = "BuildCraftAPI|statements")
public final class TileSteamOven extends TileMultiBlockOven<TileSteamOven> implements ISidedInventory, ISteamUser, ITileRotate {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 9;
    private static final int STEAM_PER_BATCH = 8000;
    private static final int TOTAL_COOK_TIME = 256;
    private static final int COOK_STEP = 16;
    private static final int ITEMS_SMELTED = 9;
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private static final int TANK_CAPACITY = 8 * FluidTools.BUCKET_VOLUME;
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();
    private final TankManager tankManager = new TankManager();
    private final FilteredTank tank;
    private final InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 9);
    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 9, false);

    static {
        char[][][] map = {
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},
                },
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
                        {'*', 'O', 'O', '*'},
                },
        };
        patterns.add(new MultiBlockPattern(map));
    }

    public int cookTime;
    private boolean finishedCycle;
    private EnumFacing facing = NORTH;
    //TODO ???
    private boolean paused = false;

    public TileSteamOven() {
        super(18, patterns);
        tank = new FilteredTank(TANK_CAPACITY, this);
        tank.setFilter(Fluids.STEAM::get);
        tankManager.add(tank);
    }

    public static void placeSteamOven(World world, BlockPos pos, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        MultiBlockPattern pattern = TileSteamOven.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<>();
        blockMapping.put('B', RailcraftBlocks.STEAM_OVEN.getDefaultState());
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

    @Nullable
    public TankManager getTankManager() {
        TileSteamOven mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.tankManager;
        return null;
    }

    @Override
    public EnumFacing getFacing() {
        TileSteamOven masterOven = getMasterBlock();
        if (masterOven != null)
            return masterOven.facing;
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        TileSteamOven masterOven = getMasterBlock();
        if (masterOven != null)
            masterOven.facing = facing;
        this.facing = facing;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld())) {
            if (isMasterCooking())
                EffectManager.instance.steamEffect(world, this, +0.25);
            return;
        }

        if (isMaster()) {
            if (clock % COOK_STEP == 0) {
                setCooking(false);
                if (!paused)
                    if (hasRecipe()) {
                        if (cookTime <= 0 && drainSteam())
                            cookTime = 1;
                        else if (cookTime > 0) {
                            cookTime += COOK_STEP;
                            if (cookTime >= TOTAL_COOK_TIME)
                                if (smeltItems()) {
                                    cookTime = 0;
                                    setCooking(true);
                                    SoundHelper.playSound(world, null, getPos(), RailcraftSoundEvents.MECHANICAL_STEAM_BURST, SoundCategory.BLOCKS, 1F, (float) (1 + MiscTools.RANDOM.nextGaussian() * 0.1));
                                }
                        }
                    } else
                        cookTime = 0;
            }
        }
    }

    @Override
    public int getTotalCookTime() {
        return TOTAL_COOK_TIME;
    }

    private boolean drainSteam() {
        FluidStack steam = tank.drain(STEAM_PER_BATCH, false);
        if (steam != null && steam.amount >= STEAM_PER_BATCH) {
            tank.drain(STEAM_PER_BATCH, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean needsFuel() {
        FluidStack steam = tank.drain(STEAM_PER_BATCH, false);
        return steam == null || steam.amount < STEAM_PER_BATCH;
    }

    private boolean hasRecipe() {
        for (IExtInvSlot slot : InventoryIterator.getVanilla(invInput)) {
            ItemStack stack = slot.getStack();
            if (!InvTools.isEmpty(stack) && !InvTools.isEmpty(FurnaceRecipes.instance().getSmeltingResult(stack)))
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
                if (!InvTools.isEmpty(stack)) {
                    ItemStack output = FurnaceRecipes.instance().getSmeltingResult(stack);
                    if (!InvTools.isEmpty(output) && InvTools.isRoomForStack(output, invOutput)) {
                        ItemStack remainder = InvTools.moveItemStack(output.copy(), invOutput);
                        if (InvTools.isEmpty(remainder)) {
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
    public boolean rotateBlock(EnumFacing face) {
        if (face.getAxis() == Axis.Y)
            return false;
        TileSteamOven master = getMasterBlock();
        if (master != null) {
            if (master.facing == face)
                master.facing = face.getOpposite();
            else
                master.facing = face;
            master.scheduleMasterRetest();
            return true;
        }
        return false;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return Plane.HORIZONTAL.facings();
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileSteamOven masterBlock = getMasterBlock();
        if (masterBlock != null) {
            GuiHandler.openGui(EnumGui.STEAN_OVEN, player, world, masterBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        data.setByte("facing", (byte) facing.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
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
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!super.isItemValidForSlot(slot, stack))
            return false;
        if (InvTools.isEmpty(stack))
            return false;
        if (slot >= SLOT_OUTPUT)
            return false;
        return !InvTools.isEmpty(FurnaceRecipes.instance().getSmeltingResult(stack));
    }

    @Override
    public boolean hasWork() {
        TileSteamOven mBlock = getMasterBlock();
        return mBlock != null && mBlock.cookTime > 0;
    }

    @NotNull
    @Override
    public EnumGui getGui() {
        return EnumGui.STEAN_OVEN;
    }

    enum Icon implements IStringSerializable {

        DOOR_TL, DOOR_TR, DOOR_BL, DOOR_BR, DEFAULT;

        IBlockState getActual(IBlockState state) {
            return state.withProperty(ICON, this);
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        EnumFacing side = getFacing();
        base = base.withProperty(FACING, side);
        if (!isStructureValid()) {
            return Icon.DEFAULT.getActual(base);
        }
        BlockPos pos = getPatternPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        switch (side) {
            case NORTH:
                if (y == 2) {
                    if (x == 2)
                        return Icon.DOOR_TL.getActual(base);
                    return Icon.DOOR_TR.getActual(base);
                }
                if (x == 2)
                    return Icon.DOOR_BL.getActual(base);
                return Icon.DOOR_BR.getActual(base);
            case WEST:
                if (y == 2) {
                    if (z == 1)
                        return Icon.DOOR_TL.getActual(base);
                    return Icon.DOOR_TR.getActual(base);
                }
                if (z == 1)
                    return Icon.DOOR_BL.getActual(base);
                return Icon.DOOR_BR.getActual(base);
            case SOUTH:
                if (y == 2) {
                    if (x == 1)
                        return Icon.DOOR_TL.getActual(base);
                    return Icon.DOOR_TR.getActual(base);
                }
                if (x == 1)
                    return Icon.DOOR_BL.getActual(base);
                return Icon.DOOR_BR.getActual(base);
            case EAST:
                if (y == 2) {
                    if (z == 2)
                        return Icon.DOOR_TL.getActual(base);
                    return Icon.DOOR_TR.getActual(base);
                }
                if (z == 2)
                    return Icon.DOOR_BL.getActual(base);
                return Icon.DOOR_BR.getActual(base);
            default:
                return Icon.DEFAULT.getActual(base);
        }
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        //TODO: front/top no fluid?
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTankManager());
        return super.getCapability(capability, facing);
    }
}
