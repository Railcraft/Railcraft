package mods.railcraft.common.fluids;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

public final class CustomContainerHandler {

    public static final CustomContainerHandler INSTANCE = new CustomContainerHandler();

    private final Table<IRegistryDelegate<Item>, String, IRegistryDelegate<Item>> containerTable = HashBasedTable.create();

    private CustomContainerHandler() {
        containerTable.put(Items.GLASS_BOTTLE.delegate, "water", Items.POTIONITEM.delegate);
    }

    void addContainer(ItemFluidContainer item) {
        containerTable.put(item.empty.delegate, item.fluid.getTag(), item.delegate);
    }

    /**
     * Listening to the use of vanilla containers to fill our containers.
     *
     * Don't know if it will cause issues with plugins, however. They should listen beforehand.
     *
     * @param event The interact event
     */
    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        World world = event.getWorld();
        if (Game.isClient(world))
            return;
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult trace = trace(player, ((EntityPlayerMP) player).interactionManager.getBlockReachDistance());
        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK)
            return;
        BlockPos pos = trace.getBlockPos();
        EnumHand hand = event.getHand();
        Block block = WorldPlugin.getBlock(world, pos);
        ItemStack stack = player.getHeldItem(hand);
        if (isEmpty(stack) || !(block instanceof IFluidBlock))
            return;
        IFluidBlock fluidBlock = (IFluidBlock) block;
        if (!fluidBlock.canDrain(world, pos)) {
            return;
        }
        FluidStack fluidStack = fluidBlock.drain(world, pos, false);
        if (fluidStack == null || fluidStack.amount != Fluid.BUCKET_VOLUME)
            return;
        IRegistryDelegate<Item> contained = containerTable.get(stack.getItem().delegate, fluidStack.getFluid().getName());
        if (contained != null) {
            fluidBlock.drain(world, pos, true);
            if (!player.capabilities.isCreativeMode)
                player.setHeldItem(hand, new ItemStack(contained.get()));
            event.setCanceled(true);
        }
    }

    @Nullable
    private static RayTraceResult trace(EntityLivingBase entity, double length) {
        Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length));
        return entity.world.rayTraceBlocks(startPos, endPos, true);
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        IRegistryDelegate<Item> item = event.getObject().getItem().delegate;
        if (containerTable.containsRow(item)) {
            event.addCapability(RailcraftConstantsAPI.locationOf("glass_bottle_empty_container"), new EmptyContainerCapabilityDispatcher(item, event.getObject()));
        }
    }

    class EmptyContainerCapabilityDispatcher extends FluidBucketWrapper {

        private final IRegistryDelegate<Item> item;

        EmptyContainerCapabilityDispatcher(IRegistryDelegate<Item> item, ItemStack container) {
            super(container);
            this.item = item;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return containerTable.contains(item, fluid.getFluid().getName());
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }

        @Nullable
        @Override
        public FluidStack getFluid() {
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (container.getCount() != 1 || resource == null || !canFillFluidType(resource)) {
                return 0;
            }

            if (resource.amount < (resource.getFluid() == FluidRegistry.WATER ? FluidTools.WaterBottleEventHandler.INSTANCE.amount : Fluid.BUCKET_VOLUME))
                return 0;

            if (doFill) {
                setFluid(resource.getFluid());
            }

            return (resource.getFluid() == FluidRegistry.WATER ? FluidTools.WaterBottleEventHandler.INSTANCE.amount : Fluid.BUCKET_VOLUME);
        }

        @Override
        protected void setFluid(@Nullable Fluid fluid) {
            if (fluid != null) {
                container.deserializeNBT(new ItemStack(containerTable.get(item, fluid.getName()).get()).serializeNBT());
            }
        }
    }
}
