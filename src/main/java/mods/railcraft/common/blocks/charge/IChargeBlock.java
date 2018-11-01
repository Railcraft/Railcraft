/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.ForwardingMap;
import mods.railcraft.api.charge.IBatteryBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IChargeBlock {

    @Nullable
    ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos);

    default void registerNode(IBlockState state, World world, BlockPos pos) {
        ChargeDef chargeDef = getChargeDef(state, world, pos);
        if (chargeDef != null)
            Charge.distribution.network(world).addNode(world, pos, chargeDef);
    }

    default void deregisterNode(World world, BlockPos pos) {
        Charge.distribution.network(world).removeNode(pos);
    }

    enum ConnectType {

        TRACK {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                Map<BlockPos, EnumSet<ConnectType>> positions = new ConnectionMap();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);
                EnumSet<ConnectType> notWire = EnumSet.complementOf(EnumSet.of(ConnectType.WIRE));
                EnumSet<ConnectType> track = EnumSet.of(ConnectType.TRACK);

                positions.put(new BlockPos(x + 1, y, z), notWire);
                positions.put(new BlockPos(x - 1, y, z), notWire);

                positions.put(new BlockPos(x + 1, y + 1, z), track);
                positions.put(new BlockPos(x + 1, y - 1, z), track);

                positions.put(new BlockPos(x - 1, y + 1, z), track);
                positions.put(new BlockPos(x - 1, y - 1, z), track);

                positions.put(new BlockPos(x, y - 1, z), all);

                positions.put(new BlockPos(x, y, z + 1), notWire);
                positions.put(new BlockPos(x, y, z - 1), notWire);

                positions.put(new BlockPos(x, y + 1, z + 1), track);
                positions.put(new BlockPos(x, y - 1, z + 1), track);

                positions.put(new BlockPos(x, y + 1, z - 1), track);
                positions.put(new BlockPos(x, y - 1, z - 1), track);
                return positions;
            }

        },
        WIRE {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                Map<BlockPos, EnumSet<ConnectType>> positions = new ConnectionMap();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);
                EnumSet<ConnectType> notTrack = EnumSet.complementOf(EnumSet.of(ConnectType.TRACK));

                positions.put(new BlockPos(x + 1, y, z), notTrack);
                positions.put(new BlockPos(x - 1, y, z), notTrack);
                positions.put(new BlockPos(x, y + 1, z), all);
                positions.put(new BlockPos(x, y - 1, z), notTrack);
                positions.put(new BlockPos(x, y, z + 1), notTrack);
                positions.put(new BlockPos(x, y, z - 1), notTrack);
                return positions;
            }

        },
        BLOCK {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos) {
                Map<BlockPos, EnumSet<ConnectType>> positions = new ConnectionMap();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);

                for (EnumFacing facing : EnumFacing.VALUES) {
                    positions.put(pos.offset(facing), all);
                }
                return positions;
            }

        };

        public abstract Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos);

    }

    class ConnectionMap extends ForwardingMap<BlockPos, EnumSet<ConnectType>> {

        private final Map<BlockPos, EnumSet<ConnectType>> delegate;

        ConnectionMap() {
            delegate = new HashMap<>();
        }

        @Override
        protected Map<BlockPos, EnumSet<ConnectType>> delegate() {
            return delegate;
        }

        @Override
        public EnumSet<ConnectType> get(@Nullable Object key) {
            EnumSet<ConnectType> ret = super.get(key);
            return ret == null ? EnumSet.noneOf(ConnectType.class) : ret;
        }
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
                string += "|" + batterySpec.toString();
            return string;
        }

    }

}
