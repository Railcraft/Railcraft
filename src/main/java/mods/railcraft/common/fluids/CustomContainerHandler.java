/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.fluids;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import org.jetbrains.annotations.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

public final class CustomContainerHandler {

    public static final CustomContainerHandler INSTANCE = new CustomContainerHandler();

    /* Empty item, fluid name -> filled item */
    final Table<IRegistryDelegate<Item>, String, IRegistryDelegate<Item>> containerTable = HashBasedTable.create();

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
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult trace = trace(player, player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue());
        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK)
            return;
        BlockPos pos = trace.getBlockPos();
        EnumHand hand = event.getHand();
        Block block = WorldPlugin.getBlock(world, pos);
        ItemStack held = player.getHeldItem(hand);
        if (isEmpty(held) || !(block instanceof IFluidBlock))
            return;
        IFluidBlock fluidBlock = (IFluidBlock) block;
        if (!fluidBlock.canDrain(world, pos)) {
            return;
        }
        FluidStack fluidStack = fluidBlock.drain(world, pos, false);
        if (fluidStack == null || fluidStack.amount != Fluid.BUCKET_VOLUME)
            return;
        IRegistryDelegate<Item> contained = containerTable.get(held.getItem().delegate, fluidStack.getFluid().getName());
        if (contained != null) {
            fluidBlock.drain(world, pos, true);
            if (!player.capabilities.isCreativeMode) {
                if (held.getCount() == 1) {
                    player.setHeldItem(hand, new ItemStack(contained.get()));
                } else {
                    InvTools.dec(held);
                    ItemStack toAdd = new ItemStack(contained.get());
                    if (!player.addItemStackToInventory(toAdd)) {
                        player.dropItem(toAdd, true);
                    }
                }
            }
            event.setCanceled(true);
        }
    }

    private static @Nullable RayTraceResult trace(EntityLivingBase entity, double length) {
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

    final class EmptyContainerCapabilityDispatcher extends FluidBucketWrapper {

        private final IRegistryDelegate<Item> item;

        EmptyContainerCapabilityDispatcher(IRegistryDelegate<Item> item, ItemStack container) {
            super(container);
            this.item = item;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return containerTable.contains(item, fluid.getFluid().getName());
        }

        @Override
        public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }

        @Override
        public @Nullable FluidStack getFluid() {
            return null;
        }

        @Override
        public @Nullable FluidStack drain(FluidStack resource, boolean doDrain) {
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
                setFluid(resource);
            }

            return (resource.getFluid() == FluidRegistry.WATER ? FluidTools.WaterBottleEventHandler.INSTANCE.amount : Fluid.BUCKET_VOLUME);
        }

        @Override
        protected void setFluid(@Nullable FluidStack fluid) {
            if (fluid != null) {
                container = new ItemStack(containerTable.get(item, fluid.getFluid().getName()).get());
            }
        }
    }
}
