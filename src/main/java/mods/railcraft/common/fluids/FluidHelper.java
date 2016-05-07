/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FluidHelper {
    public static final int BUCKET_FILL_TIME = 8;
    public static final int NETWORK_UPDATE_INTERVAL = 128;
    public static final int BUCKET_VOLUME = 1000;
    public static final int PROCESS_VOLUME = BUCKET_VOLUME * 4;
    private static final List<FluidRegistrar> adapters = new ArrayList<FluidRegistrar>();

    static {
        adapters.add(ForestryFluidRegistrar.INSTANCE);
        adapters.add(ForgeFluidRegistrar.INSTANCE);
    }

    private FluidHelper() {
    }

    public static boolean handleRightClick(IFluidHandler tank, ForgeDirection side, EntityPlayer player, boolean fill, boolean drain) {
        if (player == null)
            return false;
        ItemStack current = player.inventory.getCurrentItem();
        if (current != null) {

            FluidItemHelper.DrainReturn drainReturn = FluidItemHelper.drainContainer(current, PROCESS_VOLUME);

            if (fill && drainReturn.fluidDrained != null) {
                int used = tank.fill(side, drainReturn.fluidDrained, false);

                if (used > 0) {
                    drainReturn = FluidItemHelper.drainContainer(current, used);
                    if (!player.capabilities.isCreativeMode) {
                        if (current.stackSize > 1) {
                            if (drainReturn.container != null && !player.inventory.addItemStackToInventory(drainReturn.container))
                                return false;
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                        } else {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, drainReturn.container);
                        }
                        player.inventory.markDirty();
                    }
                    tank.fill(side, drainReturn.fluidDrained, true);
                    return true;
                }
            } else if (drain) {

                FluidStack available = tank.drain(side, PROCESS_VOLUME, false);
                if (available != null) {
                    FluidItemHelper.FillReturn fillReturn = FluidItemHelper.fillContainer(current, available);
                    if (fillReturn.amount > 0) {
                        if (current.stackSize > 1) {
                            if (fillReturn.container != null && !player.inventory.addItemStackToInventory(fillReturn.container))
                                return false;
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                        } else {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, fillReturn.container);
                        }
                        player.inventory.markDirty();
                        tank.drain(side, fillReturn.amount, true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot) {
        processContainers(tank, inv, inputSlot, outputSlot, tank.getFluidType(), true, true);
    }

    public static void processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
        TankManager tankManger = new TankManager();
        tankManger.add(tank);
        processContainers(tankManger, inv, inputSlot, outputSlot, fluidToFill, processFilled, processEmpty);
    }

    public static void processContainers(TankManager tank, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill) {
        processContainers(tank, inv, inputSlot, outputSlot, fluidToFill, true, true);
    }

    public static void processContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
        ItemStack input = inv.getStackInSlot(inputSlot);

        if (input == null)
            return;

        if (processFilled && drainContainers(fluidHandler, inv, inputSlot, outputSlot))
            return;

        if (processEmpty && fluidToFill != null)
            fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill);
    }

    public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, Fluid fluidToFill) {
        if (fluidToFill == null)
            return false;
        ItemStack input = inv.getStackInSlot(inputSlot);
        ItemStack output = inv.getStackInSlot(outputSlot);
        FluidItemHelper.FillReturn fill = FluidItemHelper.fillContainer(input, new FluidStack(fluidToFill, PROCESS_VOLUME));
        if (fill.container != null && hasPlaceToPutContainer(output, fill.container)) {
            FluidStack drain = fluidHandler.drain(ForgeDirection.UNKNOWN, fill.amount, false);
            if (drain != null && drain.amount == fill.amount) {
                fill = FluidItemHelper.fillContainer(input, drain);
                if (fill.container != null && fill.amount == drain.amount) {
                    fluidHandler.drain(ForgeDirection.UNKNOWN, fill.amount, true);
                    storeContainer(inv, inputSlot, outputSlot, fill.container);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot) {
        ItemStack input = inv.getStackInSlot(inputSlot);
        ItemStack output = inv.getStackInSlot(outputSlot);
        if (input != null) {
            FluidItemHelper.DrainReturn drain = FluidItemHelper.drainContainer(input, PROCESS_VOLUME);
            if (drain.fluidDrained != null && (drain.container == null || hasPlaceToPutContainer(output, drain.container))) {
                int used = fluidHandler.fill(ForgeDirection.UNKNOWN, drain.fluidDrained, false);
                if ((drain.isAtomic && used == drain.fluidDrained.amount) || (!drain.isAtomic && drain.fluidDrained.amount > 0)) {
                    fluidHandler.fill(ForgeDirection.UNKNOWN, drain.fluidDrained, true);
                    storeContainer(inv, inputSlot, outputSlot, drain.container);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasPlaceToPutContainer(ItemStack output, ItemStack container) {
        if (output == null)
            return true;
        return output.stackSize < output.getMaxStackSize() && InvTools.isItemEqual(container, output);
    }

    /**
     * We can assume that if null is passed for the container that the container
     * was consumed by the process and we should just remove the input container.
     */
    private static void storeContainer(IInventory inv, int inputSlot, int outputSlot, ItemStack container) {
        if (container == null) {
            inv.decrStackSize(inputSlot, 1);
            return;
        }
        ItemStack output = inv.getStackInSlot(outputSlot);
        if (output == null)
            inv.setInventorySlotContents(outputSlot, container);
        else
            output.stackSize++;
        inv.decrStackSize(inputSlot, 1);
    }

    public static boolean registerBucket(FluidStack liquid, ItemStack filled) {
        ItemStack empty = new ItemStack(Items.bucket);
        return registerContainer(liquid, filled, empty);
    }

    public static boolean registerBottle(FluidStack liquid, ItemStack filled) {
        ItemStack empty = new ItemStack(Items.glass_bottle);
        return registerContainer(liquid, filled, empty);
    }

    public static boolean registerWax(FluidStack liquid, ItemStack filled) {
        ItemStack empty = ModItems.waxCapsule.get();
        return registerContainer(liquid, filled, empty);
    }

    public static boolean registerRefactory(FluidStack liquid, ItemStack filled) {
        ItemStack empty = ModItems.refractoryEmpty.get();
        return registerContainer(liquid, filled, empty);
    }

    public static boolean registerCan(FluidStack liquid, ItemStack filled) {
        ItemStack empty = ModItems.canEmpty.get();
        return registerContainer(liquid, filled, empty);
    }

    public static boolean registerCell(FluidStack liquid, ItemStack filled) {
        ItemStack empty = ModItems.cellEmpty.get();
        return registerContainer(liquid, filled, empty);
    }

    private static boolean registerContainer(FluidStack fluidStack, ItemStack filled, ItemStack empty) {
        if (empty != null) {
            FluidContainerData container = new FluidContainerData(fluidStack, filled, empty);
            registerContainer(container);
            return true;
        }
        return false;
    }

    public static void registerContainer(FluidContainerData container) {
        for (FluidRegistrar adapter : adapters) {
            adapter.registerContainer(container);
        }
    }

    public static Collection<ItemStack> getContainersFilledWith(FluidStack fluidStack) {
        List<ItemStack> containers = new ArrayList<ItemStack>();
        for (FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            FluidStack inContainer = FluidItemHelper.getFluidStackInContainer(data.filledContainer);
            if (inContainer != null && inContainer.containsFluid(fluidStack))
                containers.add(data.filledContainer.copy());
        }
        return containers;
    }

    public static void nerfWaterBottle() {
        for (FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if (data.filledContainer.getItem() == Items.potionitem && data.emptyContainer.getItem() == Items.glass_bottle && Fluids.WATER.is(data.fluid)) {
                data.fluid.amount = 333;
                return;
            }
        }
    }

    public static FluidStack drainBlock(World world, int x, int y, int z, boolean doDrain) {
        return drainBlock(world.getBlock(x, y, z), world, x, y, z, doDrain);
    }

    public static FluidStack drainBlock(Block block, World world, int x, int y, int z, boolean doDrain) {
        if (block instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) block;
            if (fluidBlock.canDrain(world, x, y, z))
                return fluidBlock.drain(world, x, y, z, doDrain);
        } else if (block == Blocks.water || block == Blocks.flowing_water) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta != 0)
                return null;
            if (doDrain)
                world.setBlockToAir(x, y, z);
            return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
        } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta != 0)
                return null;
            if (doDrain)
                world.setBlockToAir(x, y, z);
            return new FluidStack(FluidRegistry.LAVA, FluidContainerRegistry.BUCKET_VOLUME);
        }
        return null;
    }

    public static boolean isFullFluidBlock(World world, int x, int y, int z) {
        return isFullFluidBlock(WorldPlugin.getBlock(world, x, y, z), world, x, y, z);
    }

    public static boolean isFullFluidBlock(Block block, World world, int x, int y, int z) {
        if (block instanceof BlockLiquid || block instanceof IFluidBlock)
            return world.getBlockMetadata(x, y, z) == 0;
        return false;
    }

    public static Fluid getFluid(Block block) {
        if (block instanceof IFluidBlock)
            return ((IFluidBlock) block).getFluid();
        else if (block == Blocks.water || block == Blocks.flowing_water)
            return FluidRegistry.WATER;
        else if (block == Blocks.lava || block == Blocks.flowing_lava)
            return FluidRegistry.LAVA;
        return null;
    }

    public static int getFluidId(FluidStack stack) {
        if (stack == null)
            return -1;
        if (stack.getFluid() == null)
            return -1;
        return FluidRegistry.getFluidID(stack.getFluid().getName());
    }
}
