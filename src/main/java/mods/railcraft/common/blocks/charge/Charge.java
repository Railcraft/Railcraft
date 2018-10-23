/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.annotations.Beta;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 10/19/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class Charge {
    public static final double CHARGE_PER_DAMAGE = 1000.0;

    /**
     * The distribution network is the charge network used by standard consumers, wires, tracks, and batteries.
     */
    public static IManager distribution = new IManager() {
    };

    /**
     * The transmission network is the charge network used by low maintenance transmission lines and transformers,
     * consumers should not access this network directly.
     *
     * Not currently implemented.
     */
    @Beta
    public static IManager trasnmission = new IManager() {
    };

    /**
     * This is how you get access to the meat of the charge network.
     */
    public interface IManager {

        /**
         * The network is the primary means of interfacing with charge.
         */
        default IChargeNetwork network(World world) {
            return new IChargeNetwork() {
            };
        }
    }

    enum Network {
        DISTRIBUTION, TRANSMISSION
    }
}
