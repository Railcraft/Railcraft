/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IBatteryBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 7/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IChargeBlock {

    /**
     * Asks the Block to provide a charge definition for the requesting network.
     *
     * This method can be called by any network, respond accordingly.
     *
     * It is generally to be considered an error to return the same charge definition to multiple networks.
     * Most blocks will probably be members of the {@code Charge.distribution} network only.
     *
     * Only "transformer" blocks that pass charge from one network to another should respond to multiple networks.
     *
     * If there is any way to better enforce/indicate this requirement, I haven't discovered it.
     * I expect this will be a frequent source of bugs caused by improper implementation.
     *
     * @param network The network type which is requesting a charge definition. Most blocks should only respond to one
     *                type of network.
     */
    @Nullable ChargeDef getChargeDef(Charge network, IBlockState state, IBlockAccess world, BlockPos pos);

    /**
     * The Charge Meter calls this to get a node for meter readings.
     *
     * Most blocks don't need to touch this, but Multi-blocks may want to redirect to the master block.
     */
    default Charge.IAccess getMeterAccess(IBlockState state, World world, BlockPos pos) {
        return Charge.distribution.network(world).access(pos);
    }

    default void registerNode(IBlockState state, World world, BlockPos pos) {
        Charge.distribution.network(world).addNode(state, world, pos);
    }

    default void deregisterNode(World world, BlockPos pos) {
        Charge.distribution.network(world).removeNode(pos);
    }

    enum ConnectType {
        BLOCK, SLAB, TRACK, WIRE
    }

    final class ChargeDef {
        private final ConnectType connectType;
        private final double losses;
        private final @Nullable IBatteryBlock.Spec batterySpec;

        public ChargeDef(ConnectType connectType, double losses) {
            this(connectType, losses, null);
        }

        public ChargeDef(ConnectType connectType, @Nullable IBatteryBlock.Spec batterySpec) {
            this(connectType, 0.0, batterySpec);
        }

        public ChargeDef(ConnectType connectType, double losses, @Nullable IBatteryBlock.Spec batterySpec) {
            this.connectType = connectType;
            this.losses = losses;
            this.batterySpec = batterySpec;
        }

        public double getLosses() {
            return losses;
        }

        public @Nullable IBatteryBlock.Spec getBatterySpec() {
            return batterySpec;
        }

        ConnectType getConnectType() {
            return connectType;
        }

        @Override
        public String toString() {
            String string = String.format("ChargeDef{%s, losses=%.2f}", connectType, losses);
            if (batterySpec != null)
                string += "|" + batterySpec;
            return string;
        }

    }

}
