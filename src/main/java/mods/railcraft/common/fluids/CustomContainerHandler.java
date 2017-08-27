package mods.railcraft.common.fluids;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import javax.annotation.Nullable;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

public final class CustomContainerHandler {

    public static final CustomContainerHandler INSTANCE = new CustomContainerHandler();

    private final Table<RegistryDelegate<Item>, String, RegistryDelegate<Item>> containerTable = HashBasedTable.create();

    private CustomContainerHandler() {
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
        RegistryDelegate<Item> contained = containerTable.get(stack.getItem().delegate, fluidStack.getFluid().getName());
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
        Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().xCoord * length, entity.getLookVec().yCoord * length, entity.getLookVec().zCoord * length));
        return entity.worldObj.rayTraceBlocks(startPos, endPos, true);
    }
}
