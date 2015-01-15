/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mods.railcraft.common.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class FluidHelper {

    public static final int BUCKET_FILL_TIME = 8;
    public static final int NETWORK_UPDATE_INTERVAL = 128;
    public static final int BUCKET_VOLUME = 1000;
    private static final List<IFluidRegistrar> adapters = new ArrayList<IFluidRegistrar>();

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

            FluidStack liquid = getFluidStackInContainer(current);

            if (fill && liquid != null) {
                int used = tank.fill(side, liquid, true);

                if (used > 0) {
                    if (!player.capabilities.isCreativeMode) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                        player.inventory.markDirty();
                    }
                    return true;
                }

            } else if (drain) {

                FluidStack available = tank.drain(side, Integer.MAX_VALUE, false);
                if (available != null) {
                    ItemStack filled = fillContainer(available, current);

                    liquid = getFluidStackInContainer(filled);
                    if (liquid != null) {

                        if (current.stackSize > 1) {
                            if (!player.inventory.addItemStackToInventory(filled))
                                return false;
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                            player.inventory.markDirty();
                        } else {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, InvTools.depleteItem(current));
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
                            player.inventory.markDirty();
                        }

                        tank.drain(side, liquid.amount, true);
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
        ItemStack input = inv.getStackInSlot(inputSlot);
        ItemStack output = inv.getStackInSlot(outputSlot);
        ItemStack filled = getFilledContainer(fluidToFill, input);
        if (filled != null && (output == null || (output.stackSize < output.getMaxStackSize() && InvTools.isItemEqual(filled, output)))) {
            FluidStack fluidInContainer = getFluidStackInContainer(filled);
            FluidStack drain = fluidHandler.drain(ForgeDirection.UNKNOWN, fluidInContainer, false);
            if (drain != null && drain.amount == fluidInContainer.amount) {
                fluidHandler.drain(ForgeDirection.UNKNOWN, fluidInContainer, true);
                if (output == null)
                    inv.setInventorySlotContents(outputSlot, filled);
                else
                    output.stackSize++;
                inv.decrStackSize(inputSlot, 1);
                return true;
            }
        }
        return false;
    }

    public static boolean drainContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot) {
        ItemStack input = inv.getStackInSlot(inputSlot);
        ItemStack output = inv.getStackInSlot(outputSlot);

        if (input != null) {
            FluidStack fluidInContainer = getFluidStackInContainer(input);
            ItemStack emptyItem = input.getItem().getContainerItem(input);
            if (fluidInContainer != null && (emptyItem == null || output == null || (output.stackSize < output.getMaxStackSize() && InvTools.isItemEqual(output, emptyItem)))) {
                int used = fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, false);
                if (used >= fluidInContainer.amount) {
                    fluidHandler.fill(ForgeDirection.UNKNOWN, fluidInContainer, true);
                    if (emptyItem != null)
                        if (output == null)
                            inv.setInventorySlotContents(outputSlot, emptyItem);
                        else
                            output.stackSize++;
                    inv.decrStackSize(inputSlot, 1);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBucket(ItemStack stack) {
        return FluidContainerRegistry.isBucket(stack);
    }

    public static boolean isContainer(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof IFluidContainerItem)
            return ((IFluidContainerItem)stack.getItem()).getCapacity(stack) > 0;
        return FluidContainerRegistry.isContainer(stack);
    }

    public static boolean isFilledContainer(ItemStack stack) {
        return FluidContainerRegistry.isFilledContainer(stack);
    }

    public static boolean isEmptyContainer(ItemStack stack) {
        return FluidContainerRegistry.isEmptyContainer(stack);
    }

    public static ItemStack getFilledContainer(Fluid fluid, ItemStack empty) {
        if (fluid == null || empty == null) return null;
        return FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, Integer.MAX_VALUE), empty);
    }

    public static ItemStack getFilledContainer(FluidStack fluid, ItemStack empty) {
        if (fluid == null || empty == null) return null;
        fluid = fluid.copy();
        fluid.amount = Integer.MAX_VALUE;
        return FluidContainerRegistry.fillFluidContainer(fluid, empty);
    }

    public static ItemStack fillContainer(FluidStack liquid, ItemStack empty) {
        if (liquid == null || empty == null) return null;
        return FluidContainerRegistry.fillFluidContainer(liquid, empty);
    }

    public static FluidStack getFluidStackInContainer(ItemStack stack) {
        return FluidContainerRegistry.getFluidForFilledItem(stack);
    }

    public static Fluid getFluidInContianer(ItemStack stack) {
        FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
        return fluidStack != null ? fluidStack.getFluid() : null;
    }

    public static boolean containsFluidStack(ItemStack stack, FluidStack fluidStack) {
        return FluidContainerRegistry.containsFluid(stack, fluidStack);
    }

    public static boolean containsFluid(ItemStack stack, Fluid fluid) {
        return FluidContainerRegistry.containsFluid(stack, new FluidStack(fluid, 1));
    }

    public static boolean isFluidEqual(FluidStack L1, FluidStack L2) {
        if (L1 == null || L2 == null)
            return false;
        return L1.isFluidEqual(L2);
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
        for (IFluidRegistrar adapter : adapters) {
            adapter.registerContainer(container);
        }
    }

    public static Collection<ItemStack> getContainersFilledWith(FluidStack fluidStack) {
        List<ItemStack> containers = new ArrayList<ItemStack>();
        for (FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            FluidStack inContainer = getFluidStackInContainer(data.filledContainer);
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
            IFluidBlock fluidBlock = (IFluidBlock)block;
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

}
