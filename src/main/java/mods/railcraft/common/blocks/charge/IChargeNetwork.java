/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 10/19/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IChargeNetwork {

    /**
     * Queues the node to be added to the network
     *
     * @return return true if the network changed.
     */
    default boolean addNode(World world, BlockPos pos, IChargeBlock.ChargeDef chargeDef) {
        return false;
    }

    /**
     * Queues the node to be removed to the network
     */
    default void removeNode(BlockPos pos) {
    }

    default ChargeNetwork.ChargeGraph grid(BlockPos pos) {
        // TODO: Add dummy object
        return null;
    }

    /**
     * Get a grid access point for the position.
     *
     * @return A grid access point, may be a dummy object if there is no valid grid at the location.
     */
    default ChargeNetwork.ChargeNode access(BlockPos pos) {
        // TODO: Add dummy object
        return null;
    }

    /**
     * Ask the API to provide or create a battery object for a tile entity to hold onto,
     * this is the only way you should get a battery instance or you could end up with the tile and network holding
     * different instances.
     *
     * The result of this function should be provided to the
     * {@link mods.railcraft.common.blocks.charge.IChargeBlock.ChargeDef} battery supplier.
     *
     * Implementation Note: Is there anyway we could untangle the convoluted code flow for this?
     *
     * @return A battery instance either retrieved from the network or created fresh if one doesn't exist.
     */
    default IChargeBlock.ChargeBattery makeBattery(BlockPos pos, Supplier<IChargeBlock.ChargeBattery> supplier) {
        return supplier.get();
    }
}
