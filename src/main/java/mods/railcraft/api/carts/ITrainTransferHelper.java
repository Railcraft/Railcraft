/*
 * Copyright (c) CovertJaguar, 2015 http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.api.carts;

import mods.railcraft.api.core.items.IStackFilter;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * This interface is the API facing wrapper for an internal helper class that makes it
 * simple to pass items and fluids around within a Train.
 * <p/>
 * The helper object can be accessed from CartTools and is aware of the IItemCart and IFluidCart interfaces.
 * <p/>
 * Created by CovertJaguar on 5/11/2015.
 *
 * @see mods.railcraft.api.carts.CartTools
 * @see mods.railcraft.api.carts.IItemCart
 * @see mods.railcraft.api.carts.IFluidCart
 */
public interface ITrainTransferHelper {
    // ***************************************************************************************************************************
    // Items
    // ***************************************************************************************************************************

    /**
     * Will attempt to push an ItemStack to the Train.
     *
     * @param requester the source EntityMinecart
     * @param stack     the ItemStack to be pushed
     * @return the ItemStack that remains after any pushed items were removed, or null if it was fully pushed
     * @see mods.railcraft.api.carts.IFluidCart
     */
    ItemStack pushStack(EntityMinecart requester, ItemStack stack);

    /**
     * Will request an item from the Train.
     *
     * @param requester the source EntityMinecart
     * @param filter    a IStackFilter that defines the requested item
     * @return the ItemStack pulled from the Train, or null if the request cannot be met
     * @see mods.railcraft.api.carts.IItemCart
     */
    ItemStack pullStack(EntityMinecart requester, IStackFilter filter);

    // ***************************************************************************************************************************
    // Fluids
    // ***************************************************************************************************************************

    /**
     * Will attempt to push fluid to the Train.
     *
     * @param requester  the source EntityMinecart
     * @param fluidStack the amount and type of Fluid to be pushed
     * @return the FluidStack that remains after any pushed Fluid was removed, or null if it was fully pushed
     * @see mods.railcraft.api.carts.IFluidCart
     */
    FluidStack pushFluid(EntityMinecart requester, FluidStack fluidStack);

    /**
     * Will request fluid from the Train.
     *
     * @param requester  the source EntityMinecart
     * @param fluidStack the amount and type of Fluid requested
     * @return the FluidStack pulled from the Train, or null if the request cannot be met
     * @see mods.railcraft.api.carts.IFluidCart
     */
    FluidStack pullFluid(EntityMinecart requester, FluidStack fluidStack);
}
