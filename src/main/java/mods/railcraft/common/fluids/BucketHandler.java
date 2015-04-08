/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

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

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos, ItemStack stack) {
        Block block = WorldPlugin.getBlock(world, pos.blockX, pos.blockY, pos.blockZ);
        FluidStack fluidStack = FluidHelper.drainBlock(block, world, pos.blockX, pos.blockY, pos.blockZ, false);
        if (fluidStack == null)
            return null;

        if (!allowedFluids.contains(fluidStack.getFluid()))
            return null;

        FluidItemHelper.FillReturn filled = FluidItemHelper.fillContainer(stack, fluidStack);
        if (filled.amount > 0)
            FluidHelper.drainBlock(block, world, pos.blockX, pos.blockY, pos.blockZ, true);
        return filled.container;
    }
}
