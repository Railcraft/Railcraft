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
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.util.inventory.AdjacentInventoryCache;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryFactory;
import mods.railcraft.common.util.inventory.InventorySorter;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.IInventoryObject;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.incSize;
import static mods.railcraft.common.util.inventory.InvTools.sizeOf;

public class TileBlastFurnace extends TileMultiBlockOven implements ISidedInventory {

    public static final Predicate<ItemStack> INPUT_FILTER = stack -> !InvTools.isEmpty(stack) && RailcraftCraftingManager.blastFurnace.getRecipe(stack) != null;
    public static final Predicate<ItemStack> FUEL_FILTER = StackFilters.anyOf(RailcraftCraftingManager.blastFurnace.getFuels());
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    private static final int FUEL_PER_TICK = 5;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 3);
    private static final List<MultiBlockPattern> patterns = new ArrayList<MultiBlockPattern>();

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
    public int burnTime;
    /**
     * The number of ticks that a fresh copy of the currently-burning item would
     * keep the furnace burning for
     */
    public int currentItemBurnTime;
    public boolean clientBurning;
    private int finishedAt;

    public TileBlastFurnace() {
        super(3, patterns);
    }

    public static void placeBlastFurnace(World world, BlockPos pos, ItemStack input, ItemStack output, ItemStack fuel) {
        MultiBlockPattern pattern = TileBlastFurnace.patterns.get(0);
        Map<Character, IBlockState> blockMapping = new HashMap<Character, IBlockState>();
        //TODO
//        blockMapping.put('B', EnumMachineAlpha.BLAST_FURNACE.getDefaultState());
//        blockMapping.put('W', EnumMachineAlpha.BLAST_FURNACE.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileBlastFurnace) {
            TileBlastFurnace master = (TileBlastFurnace) tile;
            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_INPUT, input);
            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_OUTPUT, output);
            master.inv.setInventorySlotContents(TileBlastFurnace.SLOT_FUEL, fuel);
        }
    }

    @Override
    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState state = worldObj.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        switch (mapPos) {
            case 'O':
                if (block != RailcraftBlocks.MACHINE_ALPHA.block() || meta != getBlockMetadata())
                    return true;
                break;
            case 'B':
            case 'W':
                if (block == RailcraftBlocks.MACHINE_ALPHA.block() && meta == getBlockMetadata())
                    return true;
                break;
            case 'A':
                if (block.isAir(state, worldObj, pos) || state.getMaterial() == Material.LAVA)
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
        BlockPos offsetPos = getPos().add(1, 1, 1);
        if (worldObj.isAirBlock(offsetPos))
            worldObj.setBlockState(offsetPos, Blocks.LAVA.getStateFromMeta(7), 3);
    }

    private void setLavaBurn() {
        BlockPos offsetPos = getPos().add(1, 1, 1);
        if (worldObj.isAirBlock(offsetPos))
            worldObj.setBlockState(offsetPos, Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
        offsetPos = offsetPos.up();
        if (worldObj.isAirBlock(offsetPos))
            worldObj.setBlockState(offsetPos, Blocks.FLOWING_LAVA.getStateFromMeta(1), 3);
    }

    /*private void destroyLava() {
        int xLava = xCoord + 1;
        int yLava = yCoord + 2;
        int zLava = zCoord + 1;
        if (worldObj.getBlock(xLava, yLava, zLava).getMaterial() == Material.LAVA)
            worldObj.setBlockToAir(xLava, yLava, zLava);
        yLava -= 1;
        if (worldObj.getBlock(xLava, yLava, zLava).getMaterial() == Material.LAVA)
            worldObj.setBlockToAir(xLava, yLava, zLava);
    }*/

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld()))
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
            if (!InvTools.isEmpty(input)) {

                ItemStack output = getStackInSlot(SLOT_OUTPUT);
                IBlastFurnaceRecipe recipe = RailcraftCraftingManager.blastFurnace.getRecipe(input);

                if (recipe != null && recipe.isRoomForOutput(output)) {
                    if (paused) return;

                    if (burnTime <= FUEL_PER_TICK * 2) {
                        ItemStack fuel = getStackInSlot(SLOT_FUEL);
                        if (fuel != null && FUEL_FILTER.test(fuel)) {
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
                            if (InvTools.isEmpty(output))
                                setInventorySlotContents(SLOT_OUTPUT, recipe.getOutput());
                            else
                                incSize(output, recipe.getOutputStackSize());
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
            GuiHandler.openGui(EnumGui.BLAST_FURNACE, player, worldObj, masterBlock.getX(), masterBlock.getY(), masterBlock.getZ());
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setInteger("burnTime", burnTime);
        data.setInteger("currentItemBurnTime", currentItemBurnTime);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        burnTime = data.getInteger("burnTime");
        currentItemBurnTime = data.getInteger("currentItemBurnTime");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(burnTime > 0);
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

    @Nullable
    @Override
    public EnumGui getGui() {
        return EnumGui.BLAST_FURNACE;
    }
}
