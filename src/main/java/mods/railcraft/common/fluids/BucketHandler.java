/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class BucketHandler {
    public static BucketHandler INSTANCE = new BucketHandler();
    public final Set<Fluid> allowedFluids = new HashSet<Fluid>();

    private BucketHandler() {
        allowedFluids.add(Fluids.CREOSOTE.get());
    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {

        ItemStack result = fillCustomBucket(event.world, event.target, event.current);

        if (result == null)
            return;

        event.result = result;
        event.setResult(Result.ALLOW);
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition mop, ItemStack stack) {
        BlockPos blockPos = mop.getBlockPos();
        IBlockState state = WorldPlugin.getBlockState(world, blockPos);
        FluidStack fluidStack = FluidHelper.drainBlock(state, world, blockPos, false);
        if (fluidStack == null)
            return null;

        if (!allowedFluids.contains(fluidStack.getFluid()))
            return null;

        FluidItemHelper.FillReturn filled = FluidItemHelper.fillContainer(stack, fluidStack);
        if (filled.amount > 0)
            FluidHelper.drainBlock(state, world, blockPos, true);
        return filled.container;
    }
}
