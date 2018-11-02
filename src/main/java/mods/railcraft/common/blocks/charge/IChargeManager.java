/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import net.minecraft.world.World;

/**
 * This is how you get access to the meat of the charge network.
 */
public interface IChargeManager {

    /**
     * The network is the primary means of interfacing with charge.
     */
    default IChargeNetwork network(World world) {
        return new IChargeNetwork() {
        };
    }
}
