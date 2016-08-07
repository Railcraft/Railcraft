/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.client.particles.ParticleDrip;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.items.ModItems;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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

    @Deprecated
    public static boolean handleRightClick(IFluidHandler tank, @Nullable EnumFacing side, @Nullable EntityPlayer player, boolean fill, boolean drain) {
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

    public static void processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
        TankManager tankManger = new TankManager();
        tankManger.add(tank);
        processContainers(tankManger, inv, inputSlot, outputSlot, fluidToFill, processFilled, processEmpty);
    }

    public static void processContainers(TankManager tank, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill) {
        processContainers(tank, inv, inputSlot, outputSlot, fluidToFill, true, true);
    }

    public static void processContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
        ItemStack input = inv.getStackInSlot(inputSlot);

        if (input == null)
            return;

        if (processFilled && drainContainers(fluidHandler, inv, inputSlot, outputSlot))
            return;

        if (processEmpty && fluidToFill != null)
            fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill);
    }

    public static boolean fillContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill) {
        if (fluidToFill == null)
            return false;
        ItemStack input = inv.getStackInSlot(inputSlot);
        ItemStack output = inv.getStackInSlot(outputSlot);
        FluidItemHelper.FillReturn fill = FluidItemHelper.fillContainer(input, new FluidStack(fluidToFill, PROCESS_VOLUME));
        if (fill.container != null && hasPlaceToPutContainer(output, fill.container)) {
            FluidStack drain = fluidHandler.drain(null, fill.amount, false);
            if (drain != null && drain.amount == fill.amount) {
                fill = FluidItemHelper.fillContainer(input, drain);
                if (fill.container != null && fill.amount == drain.amount) {
                    fluidHandler.drain(null, fill.amount, true);
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
                int used = fluidHandler.fill(null, drain.fluidDrained, false);
                if (drain.isAtomic ? used == drain.fluidDrained.amount : drain.fluidDrained.amount > 0) {
                    fluidHandler.fill(null, drain.fluidDrained, true);
                    storeContainer(inv, inputSlot, outputSlot, drain.container);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasPlaceToPutContainer(@Nullable ItemStack output, @Nullable ItemStack container) {
        return output == null || output.stackSize < output.getMaxStackSize() && InvTools.isItemEqual(container, output);
    }

    /**
     * We can assume that if null is passed for the container that the container
     * was consumed by the process and we should just remove the input container.
     */
    private static void storeContainer(IInventory inv, int inputSlot, int outputSlot, @Nullable ItemStack container) {
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
        ItemStack empty = new ItemStack(Items.BUCKET);
        return registerContainer(liquid, filled, empty);
    }

    public static boolean registerBottle(FluidStack liquid, ItemStack filled) {
        ItemStack empty = new ItemStack(Items.GLASS_BOTTLE);
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

    private static boolean registerContainer(FluidStack fluidStack, ItemStack filled, @Nullable ItemStack empty) {
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
            if (data.filledContainer.getItem() == Items.POTIONITEM && data.emptyContainer.getItem() == Items.GLASS_BOTTLE && Fluids.WATER.is(data.fluid)) {
                data.fluid.amount = 333;
                return;
            }
        }
    }

    @Nullable
    public static FluidStack drainBlock(World world, BlockPos pos, boolean doDrain) {
        return drainBlock(WorldPlugin.getBlockState(world, pos), world, pos, doDrain);
    }

    @Nullable
    public static FluidStack drainBlock(IBlockState state, World world, BlockPos pos, boolean doDrain) {
        FluidStack fluid;
        if ((fluid = drainForgeFluid(state, world, pos, doDrain)) != null)
            return fluid;
        else if ((fluid = drainVanillaFluid(state, world, pos, doDrain, Fluids.WATER, Blocks.WATER, Blocks.FLOWING_WATER)) != null)
            return fluid;
        else if ((fluid = drainVanillaFluid(state, world, pos, doDrain, Fluids.LAVA, Blocks.LAVA, Blocks.FLOWING_LAVA)) != null)
            return fluid;
        return null;
    }

    @Nullable
    private static FluidStack drainForgeFluid(IBlockState state, World world, BlockPos pos, boolean doDrain) {
        if (state.getBlock() instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
            if (fluidBlock.canDrain(world, pos))
                return fluidBlock.drain(world, pos, doDrain);
        }
        return null;
    }

    @Nullable
    private static FluidStack drainVanillaFluid(IBlockState state, World world, BlockPos pos, boolean doDrain, Fluids fluid, Block... blocks) {
        boolean matches = false;
        for (Block block : blocks) {
            if (state.getBlock() == block)
                matches = true;
        }
        if (!matches)
            return null;
        if (!(state.getBlock() instanceof BlockLiquid))
            return null;
        int level = state.getValue(BlockLiquid.LEVEL);
        if (level != 0)
            return null;
        if (doDrain)
            WorldPlugin.isBlockAir(world, pos);
        return fluid.getBucket();
    }

    public static boolean isFullFluidBlock(World world, BlockPos pos) {
        return isFullFluidBlock(WorldPlugin.getBlockState(world, pos), world, pos);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isFullFluidBlock(IBlockState state, World world, BlockPos pos) {
        if (state.getBlock() instanceof BlockLiquid)
            return state.getValue(BlockLiquid.LEVEL) == 0;
        if (state.getBlock() instanceof IFluidBlock)
            return Math.abs(((IFluidBlock) state.getBlock()).getFilledPercentage(world, pos)) == 1.0;
        return false;
    }

    @Nullable
    public static Fluid getFluid(Block block) {
        if (block instanceof IFluidBlock)
            return ((IFluidBlock) block).getFluid();
        else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
            return FluidRegistry.WATER;
        else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
            return FluidRegistry.LAVA;
        return null;
    }

    @Nullable
    public static Fluid getFluid(IBlockState state) {
        return getFluid(state.getBlock());
    }

    public static int getFluidId(@Nullable FluidStack stack) {
        if (stack == null)
            return -1;
        if (stack.getFluid() == null)
            return -1;
        return FluidRegistry.getFluidID(stack.getFluid().getName());
    }

    @SideOnly(Side.CLIENT)
    public static void drip(World world, BlockPos pos, IBlockState state, Random rand, float particleRed, float particleGreen, float particleBlue) {
        if (rand.nextInt(10) == 0 && world.isSideSolid(pos.down(), EnumFacing.UP) && !WorldPlugin.getBlockMaterial(world, pos.down(2)).blocksMovement()) {
            double px = (double) ((float) pos.getX() + rand.nextFloat());
            double py = (double) pos.getY() - 1.05D;
            double pz = (double) ((float) pos.getZ() + rand.nextFloat());

            Particle fx = new ParticleDrip(world, new Vec3d(px, py, pz), particleRed, particleGreen, particleBlue);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }
}
