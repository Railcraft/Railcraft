/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.client.particles.ParticleDrip;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings({"WeakerAccess"})
public final class FluidTools {
    public static final int BUCKET_FILL_TIME = 8;
    public static final int NETWORK_UPDATE_INTERVAL = 128;
    public static final int BUCKET_VOLUME = 1000;
    public static final int PROCESS_VOLUME = BUCKET_VOLUME * 4;

    private FluidTools() {
    }

    @Contract("null -> null; !null -> !null")
    public static @Nullable FluidStack copy(@Nullable FluidStack fluidStack) {
        return fluidStack == null ? null : fluidStack.copy();
    }

    @Contract("null, null -> true; null, !null -> false; !null, null -> false")
    public static boolean matches(@Nullable FluidStack left, @Nullable FluidStack right) {
        // FluidStack#equals calls isFluidEqual
        return Objects.equals(left, right);
//        return left == null ? right == null : left.isFluidEqual(right);
    }

    public static String toString(@Nullable FluidStack fluidStack) {
        if (fluidStack == null)
            return "null";
        return fluidStack.amount + "x" + fluidStack.getFluid().getName();
    }

    public static @Nullable IFluidHandler getFluidHandler(ICapabilityProvider object) {
        return getFluidHandler(null, object);
    }

    public static @Nullable IFluidHandler getFluidHandler(@Nullable EnumFacing side, ICapabilityProvider object) {
        return object.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    public static boolean hasFluidHandler(@Nullable EnumFacing side, ICapabilityProvider object) {
        return object.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    public static boolean interactWithFluidHandler(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler) {
        if (Game.isHost(player.world))
            return FluidUtil.interactWithFluidHandler(player, hand, fluidHandler);
        return FluidItemHelper.isContainer(player.getHeldItem(hand));
    }

    public enum ProcessType {
        FILL_ONLY,
        DRAIN_ONLY,
        FILL_THEN_DRAIN,
        DRAIN_THEN_FILL
    }

    public enum ProcessState {
        FILLING,
        DRAINING,
        RESET
    }

    private static void sendToProcessing(IInventory inv) {
        InventoryMapper.make(inv, 0, 1).moveOneItemTo(InventoryMapper.make(inv, 1, 1).ignoreItemChecks());
    }

    private static void sendToOutput(IInventory inv) {
        InventoryMapper.make(inv, 1, 1).moveOneItemTo(InventoryMapper.make(inv, 2, 1).ignoreItemChecks());
    }

    private static ProcessState tryFill(IInventory inv, StandardTank tank, ItemStack container) {
        FluidActionResult filled = FluidUtil.tryFillContainer(container, tank, Fluid.BUCKET_VOLUME, null, true);
        if (!filled.isSuccess()) {
            sendToOutput(inv);
            return ProcessState.RESET;
        }
        inv.setInventorySlotContents(1, InvTools.makeSafe(filled.getResult()));
        return ProcessState.FILLING;
    }

    private static ProcessState tryDrain(IInventory inv, StandardTank tank, ItemStack container) {
        FluidActionResult drained = FluidUtil.tryEmptyContainer(container, tank, Fluid.BUCKET_VOLUME, null, true);
        if (!drained.isSuccess()) {
            sendToOutput(inv);
            return ProcessState.RESET;
        }
        inv.setInventorySlotContents(1, InvTools.makeSafe(drained.getResult()));
        return ProcessState.DRAINING;
    }

    /**
     * Expects a three slot inventory, with input as slot 0, processing as slot 1, and output as slot 2.
     * Will handle moving an item through all stages from input to output for either filling or draining.
     */
    public static ProcessState processContainer(IInventory inv, StandardTank tank, ProcessType type, ProcessState state) {
        ItemStack container = inv.getStackInSlot(1);
        if (InvTools.isEmpty(container) || FluidUtil.getFluidHandler(container) == null) {
            sendToProcessing(inv);
            return ProcessState.RESET;
        }
        if (state == ProcessState.RESET) {
            if (type == ProcessType.FILL_ONLY) {
                return tryFill(inv, tank, container);
            } else if (type == ProcessType.DRAIN_ONLY) {
                return tryDrain(inv, tank, container);
            } else if (type == ProcessType.FILL_THEN_DRAIN) {
                if (FluidUtil.tryFillContainer(container, tank, Fluid.BUCKET_VOLUME, null, false).isSuccess())
                    return tryFill(inv, tank, container);
                else
                    return tryDrain(inv, tank, container);
            } else if (type == ProcessType.DRAIN_THEN_FILL) {
                if (FluidUtil.tryEmptyContainer(container, tank, Fluid.BUCKET_VOLUME, null, false).isSuccess())
                    return tryDrain(inv, tank, container);
                else
                    return tryFill(inv, tank, container);
            }
        }
        if (state == ProcessState.FILLING)
            return tryFill(inv, tank, container);
        if (state == ProcessState.DRAINING)
            return tryDrain(inv, tank, container);
        return state;
    }

    /**
     * Process containers in input/output slot like the in the tank cart.
     *
     * @param tank       Fluid tank
     * @param inv        The inventory that contains input/output slots
     * @param inputSlot  The input slot number
     * @param outputSlot The output slot number
     * @return {@code true} if changes have been done to the tank
     * @deprecated The two slot functions are deprecated in favor of the three slot function in order to support partial containers. All usage should be migrated.
     */
    @Deprecated
    public static boolean processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot) {
        return processContainers(tank, inv, inputSlot, outputSlot, tank.getFluidType(), true, true);
    }

    @Deprecated
    public static boolean processContainers(StandardTank tank, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
        TankManager tankManger = new TankManager();
        tankManger.add(tank);
        return processContainers(tankManger, inv, inputSlot, outputSlot, fluidToFill, processFilled, processEmpty);
    }

    @Deprecated
    public static boolean processContainers(TankManager tank, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill) {
        return processContainers(tank, inv, inputSlot, outputSlot, fluidToFill, true, true);
    }

    @Deprecated
    public static boolean processContainers(IFluidHandler fluidHandler, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill, boolean processFilled, boolean processEmpty) {
        ItemStack input = inv.getStackInSlot(inputSlot);

        if (InvTools.isEmpty(input))
            return false;

        if (processFilled && drainContainers(fluidHandler, inv, inputSlot, outputSlot))
            return true;

        if (processEmpty && fluidToFill != null)
            return fillContainers(fluidHandler, inv, inputSlot, outputSlot, fluidToFill);
        return false;
    }

    @Deprecated
    public static boolean fillContainers(IFluidHandler source, IInventory inv, int inputSlot, int outputSlot, @Nullable Fluid fluidToFill) {
        ItemStack input = inv.getStackInSlot(inputSlot);
        //need an empty container
        if (InvTools.isEmpty(input))
            return false;
        ItemStack output = inv.getStackInSlot(outputSlot);
        FluidActionResult container = FluidUtil.tryFillContainer(input, source, BUCKET_VOLUME, null, false);
        //check failure
        if (!container.isSuccess())
            return false;
        //check filled fluid type
        if (fluidToFill != null && !InvTools.isEmpty(container.getResult())) {
            FluidStack fluidStack = FluidUtil.getFluidContained(container.getResult());
            if (fluidStack != null && fluidStack.getFluid() != fluidToFill)
                return false;
        }
        //check place for container
        if (!InvTools.canMerge(output, container.getResult()))
            return false;
        //do actual things here
        container = FluidUtil.tryFillContainer(input, source, BUCKET_VOLUME, null, true);
        storeContainer(inv, inputSlot, outputSlot, container.getResult());
        return true;
    }

    @Deprecated
    public static boolean drainContainers(IFluidHandler dest, IInventory inv, int inputSlot, int outputSlot) {
        ItemStack input = inv.getStackInSlot(inputSlot);
        //need a valid container
        if (InvTools.isEmpty(input))
            return false;
        ItemStack output = inv.getStackInSlot(outputSlot);
        FluidActionResult container = FluidUtil.tryEmptyContainer(input, dest, BUCKET_VOLUME, null, false);
        //check failure
        if (!container.isSuccess())
            return false;
        //check place for container
        if (!InvTools.canMerge(output, container.getResult()))
            return false;
        //do actual things here
        container = FluidUtil.tryEmptyContainer(input, dest, BUCKET_VOLUME, null, true);
        storeContainer(inv, inputSlot, outputSlot, container.getResult());
        return true;
    }

    /**
     * We can assume that if null is passed for the container that the container
     * was consumed by the process and we should just remove the input container.
     */
    @Deprecated
    private static void storeContainer(IInventory inv, int inputSlot, int outputSlot, @Nullable ItemStack container) {
        if (InvTools.isEmpty(container)) {
            inv.decrStackSize(inputSlot, 1);
            return;
        }
        ItemStack output = inv.getStackInSlot(outputSlot);
        if (InvTools.isEmpty(output))
            inv.setInventorySlotContents(outputSlot, container);
        else
            InvTools.inc(output);
        inv.decrStackSize(inputSlot, 1);
    }

    public static void initWaterBottle(boolean nerf) {
        WaterBottleEventHandler.INSTANCE.amount = nerf ? 333 : 1000;
        MinecraftForge.EVENT_BUS.register(WaterBottleEventHandler.INSTANCE);
    }

    public static @Nullable FluidStack drainBlock(World world, BlockPos pos, boolean doDrain) {
        return drainBlock(WorldPlugin.getBlockState(world, pos), world, pos, doDrain);
    }

    public static @Nullable FluidStack drainBlock(IBlockState state, World world, BlockPos pos, boolean doDrain) {
        FluidStack fluid;
        if ((fluid = drainForgeFluid(state, world, pos, doDrain)) != null)
            return fluid;
        else if ((fluid = drainVanillaFluid(state, world, pos, doDrain, Fluids.WATER, Blocks.WATER, Blocks.FLOWING_WATER)) != null)
            return fluid;
        else if ((fluid = drainVanillaFluid(state, world, pos, doDrain, Fluids.LAVA, Blocks.LAVA, Blocks.FLOWING_LAVA)) != null)
            return fluid;
        return null;
    }

    private static @Nullable FluidStack drainForgeFluid(IBlockState state, World world, BlockPos pos, boolean doDrain) {
        if (state.getBlock() instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) state.getBlock();
            if (fluidBlock.canDrain(world, pos))
                return fluidBlock.drain(world, pos, doDrain);
        }
        return null;
    }

    private static @Nullable FluidStack drainVanillaFluid(IBlockState state, World world, BlockPos pos, boolean doDrain, Fluids fluid, Block... blocks) {
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

    public static @Nullable Fluid getFluid(Block block) {
        if (block instanceof IFluidBlock)
            return ((IFluidBlock) block).getFluid();
        else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
            return FluidRegistry.WATER;
        else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
            return FluidRegistry.LAVA;
        return null;
    }

    public static @Nullable Fluid getFluid(IBlockState state) {
        return getFluid(state.getBlock());
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

    public static boolean testProperties(boolean all, @Nullable IFluidHandler fluidHandler, Predicate<IFluidTankProperties> test) {
        if (fluidHandler == null)
            return false;
        IFluidTankProperties[] properties = fluidHandler.getTankProperties();
        if (all)
            return Arrays.stream(properties).allMatch(test);
        return Arrays.stream(properties).anyMatch(test);
    }

    public static Collection<IFluidHandler> findNeighbors(AdjacentTileCache cache, Predicate<? super TileEntity> filter, EnumFacing... sides) {
        List<IFluidHandler> targets = new ArrayList<>();
        for (EnumFacing side : sides) {
            TileEntity tile = cache.getTileOnSide(side);
            if (tile == null) continue;
            if (!TankManager.TANK_FILTER.apply(tile, side.getOpposite())) continue;
            if (!filter.test(tile)) continue;
            IFluidHandler tank = FluidTools.getFluidHandler(side.getOpposite(), tile);
            if (tank != null)
                targets.add(tank);
        }
        return targets;
    }

    static final class WaterBottleEventHandler {
        static final WaterBottleEventHandler INSTANCE = new WaterBottleEventHandler();
        int amount;

        private WaterBottleEventHandler() {
        }

        @SubscribeEvent
        public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
            if (event.getObject().getItem() == Items.POTIONITEM && PotionUtils.getPotionFromItem(event.getObject()) == PotionTypes.WATER) {
                event.addCapability(RailcraftConstantsAPI.locationOf("water_bottle_container"), new WaterBottleCapabilityDispatcher(event.getObject()));
            }
        }
    }

    private static final class WaterBottleCapabilityDispatcher extends FluidBucketWrapper {
        WaterBottleCapabilityDispatcher(ItemStack container) {
            super(container);
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return 0;
        }

        @Override
        protected void setFluid(@Nullable FluidStack fluid) {
            if (fluid == null) {
                container = new ItemStack(Items.GLASS_BOTTLE);
            }
        }

        @Override
        public @Nullable FluidStack drain(FluidStack resource, boolean doDrain) {
            if (InvTools.sizeOf(container) != 1 || resource == null || resource.amount < WaterBottleEventHandler.INSTANCE.amount) {
                return null;
            }

            FluidStack fluidStack = getFluid();
            if (fluidStack != null && fluidStack.isFluidEqual(resource)) {
                if (doDrain) {
                    setFluid((FluidStack) null);
                }
                return fluidStack;
            }

            return null;
        }

        @Override
        public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
            if (container.getCount() != 1 || maxDrain < WaterBottleEventHandler.INSTANCE.amount) {
                return null;
            }

            FluidStack fluidStack = getFluid();
            if (fluidStack != null) {
                if (doDrain) {
                    setFluid((FluidStack) null);
                }
                return fluidStack;
            }

            return null;
        }

        @Override
        public FluidStack getFluid() {
            return new FluidStack(FluidRegistry.WATER, WaterBottleEventHandler.INSTANCE.amount);
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new FluidTankProperties[]{new FluidTankProperties(getFluid(), WaterBottleEventHandler.INSTANCE.amount)};
        }
    }
}
