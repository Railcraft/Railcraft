/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.crafting.BlastFurnaceCraftingManager;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryFactory;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static mods.railcraft.common.blocks.multi.BlockBlastFurnace.ICON;
import static mods.railcraft.common.util.inventory.InvTools.incSize;
import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

public final class TileBlastFurnace extends TileMultiBlockOven<TileBlastFurnace> implements ISidedInventory {

    public static final Predicate<ItemStack> INPUT_FILTER = stack -> !InvTools.isEmpty(stack) && BlastFurnaceCraftingManager.getInstance().getRecipe(stack) != null;
    public static final Predicate<ItemStack> FUEL_FILTER = stack -> BlastFurnaceCraftingManager.getInstance().getCookTime(stack) > 0;
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int FUEL_PER_TICK = 5;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 3);
    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

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

    private final InventoryMapper invFuel = InventoryMapper.make(this, SLOT_FUEL, 1);
    //    private final InventoryMapper invInput = new InventoryMapper(this, SLOT_INPUT, 1);
    //    private final InventoryMapper invOutput = new InventoryMapper(this, SLOT_OUTPUT, 1);
    private final AdjacentInventoryCache invCache = new AdjacentInventoryCache(tileCache, tile -> {
        if (tile instanceof TileBlastFurnace)
            return false;
        IInventoryObject tileInv = InventoryFactory.get(tile);
        return tileInv != null && tileInv.getNumSlots() >= 27;
    }, InventorySorter.SIZE_DESCENDING);
    /**
     * The number of ticks that the furnace will keep burning
     */
    public int fuelTimeLeft;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would
     * keep the furnace burning for
     */
    public int currentItemBurnTime;
    public boolean clientBurning;
    private int finishedAt;
    private ItemStack lastInput = ItemStack.EMPTY;
    @Nullable
    private IBlastFurnaceRecipe currentRecipe;

    public static void placeBlastFurnace(World world, BlockPos pos, ItemStack input, ItemStack output, ItemStack fuel) {
        MultiBlockPattern pattern = TileBlastFurnace.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<>();
        blockMapping.put('B', RailcraftBlocks.BLAST_FURNACE.getDefaultState());
        blockMapping.put('W', RailcraftBlocks.BLAST_FURNACE.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileBlastFurnace) {
            TileBlastFurnace master = (TileBlastFurnace) tile;
            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_INPUT, input);
            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_OUTPUT, output);
            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_FUEL, fuel);
        }
    }

    public TileBlastFurnace() {
        super(3, patterns);
    }

    @Override
    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState self = getBlockState();
        IBlockState state = world.getBlockState(pos);
        switch (mapPos) {
            case 'O':
                if (self != state)
                    return true;
                break;
            case 'B':
            case 'W':
                if (self == state)
                    return true;
                break;
            case 'A':
                if (state.getBlock().isAir(state, world, pos) || state.getMaterial() == Material.LAVA)
                    return true;
                break;
        }
        return false;
    }

    @Override
    public int getTotalCookTime() {
        ItemStack input = getStackInSlot(SLOT_INPUT);
        if (InvTools.isEmpty(input))
            return 1;
        IBlastFurnaceRecipe recipe = RailcraftCraftingManager.getBlastFurnaceCraftings().getRecipe(input);
        if (recipe != null)
            return recipe.getCookTime();
        return 1;
    }

    public int getBurnProgressScaled(int i) {
        if (fuelTimeLeft <= 0 || currentItemBurnTime <= 0)
            return 0;
        int scale = fuelTimeLeft * i / currentItemBurnTime;
        scale = Math.min(scale, i);
        scale = Math.max(scale, 0);
        return scale;
    }

    private void setLavaIdle() {
        BlockPos offsetPos = getPos().add(0, 1, 0);
        if (world.isAirBlock(offsetPos))
            world.setBlockState(offsetPos, Blocks.LAVA.getStateFromMeta(7), 3);
    }

    private void setLavaBurn() {
        BlockPos offsetPos = getPos().add(0, 1, 0);
        if (world.isAirBlock(offsetPos))
            world.setBlockState(offsetPos, Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
        offsetPos = offsetPos.up();
        if (world.isAirBlock(offsetPos))
            world.setBlockState(offsetPos, Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
    }

    /*private void destroyLava() {
        int xLava = x + 1;
        int yLava = y + 2;
        int zLava = z + 1;
        if (world.getBlock(xLava, yLava, zLava).getMaterial() == Material.LAVA)
            world.setBlockToAir(xLava, yLava, zLava);
        yLava -= 1;
        if (world.getBlock(xLava, yLava, zLava).getMaterial() == Material.LAVA)
            world.setBlockToAir(xLava, yLava, zLava);
    }*/

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld()))
            return;

        TileBlastFurnace mBlock = getMasterBlock();

        if (mBlock != null)
            InvTools.moveOneItem(invCache.getAdjacentInventories(), mBlock.invFuel, FUEL_FILTER);

        if (isMaster()) {
            updateMaster();
        }
    }

    void updateMaster() {
        boolean wasBurning = isBurning();

        // cooking check
        if (clock > finishedAt + 10 && cookTime <= 0)
            setCooking(false);

        if (fuelTimeLeft >= FUEL_PER_TICK)
            fuelTimeLeft -= FUEL_PER_TICK;
        else
            fuelTimeLeft = 0;

        if (isBurning())
            setLavaBurn();
        else
            setLavaIdle();

        processRecipe();
        loadFuel();

        if (wasBurning != isBurning())
            sendUpdateToClient();
    }

    void processRecipe() {
        ItemStack input = getStackInSlot(SLOT_INPUT);
        if (input != lastInput) {
            resetCooking();
            lastInput = input;
            currentRecipe = BlastFurnaceCraftingManager.getInstance().getRecipe(input);
        }

        if (currentRecipe == null) {
            return;
        }

        ItemStack outputSlot = getStackInSlot(SLOT_OUTPUT);
        ItemStack nextOutput = currentRecipe.getOutput();

        if (!InvTools.canMerge(outputSlot, nextOutput, getInventoryStackLimit())) {
            return;
        }

        if (!isBurning()) {
            return;
        }

        setCooking(true);
        cookTime++;
        if (cookTime < currentRecipe.getCookTime()) {
            return;
        }

        cookTime = 0;
        finishedAt = clock;

        if (InvTools.isEmpty(outputSlot))
            setInventorySlotContents(SLOT_OUTPUT, nextOutput);
        else
            incSize(outputSlot, nextOutput.getCount());
        decrStackSize(SLOT_INPUT, 1);

        // TODO fix mess
//        if (!InvTools.isEmpty(input)) {
//            ItemStack outputSlot = getStackInSlot(SLOT_OUTPUT);
//            IBlastFurnaceRecipe recipe = BlastFurnaceCraftingManager.getInstance().getRecipe(input);
//
//            if (recipe != null) {
//                if (paused) return;
//
//                ItemStack nextOutput = recipe.getOutput();
//
//                if (InvTools.isItemEqual(outputSlot, nextOutput) && nextOutput.getCount() + outputSlot.getCount() <= Math.min(inv.getInventoryStackLimit(), outputSlot.getMaxStackSize())) {
//
//                    if (isBurning()) {
//                        cookTime++;
//                        setCooking(true);
//
//                        if (cookTime >= recipe.getCookTime()) {
//                            cookTime = 0;
//                            finishedAt = clock;
//                            if (InvTools.isEmpty(outputSlot))
//                                setInventorySlotContents(SLOT_OUTPUT, recipe.getOutput());
//                            else
//                                incSize(outputSlot, nextOutput.getCount());
//                            decrStackSize(SLOT_INPUT, 1);
//                        }
//                    }
//                }
//            } else {
//                resetCooking();
//            }
//        } else {
//            resetCooking();
//        }
    }

    void loadFuel() {
        if (getStackInSlot(SLOT_INPUT).isEmpty()) {
            return;
        }

        if (fuelTimeLeft > FUEL_PER_TICK * 2) {
            return;
        }
        ItemStack fuel = getStackInSlot(SLOT_FUEL);
        if (fuel.isEmpty()) {
            return;
        }
        int itemBurnTime = BlastFurnaceCraftingManager.getInstance().getCookTime(fuel);
        if (itemBurnTime <= 0) {
            return;
        }
        currentItemBurnTime = itemBurnTime + fuelTimeLeft;
        fuelTimeLeft = currentItemBurnTime;
        setInventorySlotContents(SLOT_FUEL, InvTools.depleteItem(fuel));
    }

    void resetCooking() {
        cookTime = 0;
        setCooking(false);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        TileBlastFurnace masterBlock = getMasterBlock();
        if (masterBlock != null) {
            GuiHandler.openGui(EnumGui.BLAST_FURNACE, player, world, masterBlock.getX(), masterBlock.getY(), masterBlock.getZ());
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("fuelTimeLeft", fuelTimeLeft);
        data.setInteger("currentItemBurnTime", currentItemBurnTime);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        fuelTimeLeft = data.getInteger("fuelTimeLeft");
        currentItemBurnTime = data.getInteger("currentItemBurnTime");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(fuelTimeLeft > 0);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
        clientBurning = data.readBoolean();
    }

    @Override
    public boolean needsFuel() {
        ItemStack fuel = getStackInSlot(SLOT_FUEL);
        return sizeOf(fuel) < 8;
    }

    // Must be called on server side and on master block
    public boolean isBurning() {
        return fuelTimeLeft > 0;
    }

    @Override
    public boolean isMasterBurning() {
        TileBlastFurnace mBlock = getMasterBlock();
        if (mBlock != null)
            if (world.isRemote)
                return mBlock.clientBurning;
            else
                return mBlock.isBurning();
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
                return FUEL_FILTER.test(stack);
            case SLOT_INPUT:
                return INPUT_FILTER.test(stack);
        }
        return false;
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
        return index == SLOT_OUTPUT;
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @NotNull
    @Nonnull
    @Override
    public EnumGui getGui() {
        return EnumGui.BLAST_FURNACE;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getPatternMarker() == 'W'
                ? isMasterBurning()
                ? base.withProperty(ICON, 2)
                : base.withProperty(ICON, 1)
                : base.withProperty(ICON, 0);
    }
}
