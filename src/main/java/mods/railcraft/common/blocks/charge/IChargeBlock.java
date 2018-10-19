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
import mods.railcraft.api.charge.IChargeBattery;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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
            ChargeManager.getNetwork(world).registerChargeNode(world, pos, chargeDef);
    }

    default void deregisterNode(World world, BlockPos pos) {
        ChargeManager.getNetwork(world).deregisterChargeNode(pos);
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
        private final double cost;
        private final @Nullable BiFunction<World, BlockPos, ChargeBattery> batterySupplier;

        public ChargeDef(ConnectType connectType, double cost) {
            this(connectType, cost, null);
        }

        public ChargeDef(ConnectType connectType, @Nullable BiFunction<World, BlockPos, ChargeBattery> batterySupplier) {
            this(connectType, 0.0, batterySupplier);
        }

        public ChargeDef(ConnectType connectType, double cost, @Nullable BiFunction<World, BlockPos, ChargeBattery> batterySupplier) {
            this.connectType = connectType;
            this.cost = cost * RailcraftConfig.chargeMaintenanceCostMultiplier();
            this.batterySupplier = batterySupplier;
        }

        public double getMaintenanceCost() {
            return cost;
        }

        ConnectType getConnectType() {
            return connectType;
        }

        @Nullable
        ChargeBattery getBattery(World world, BlockPos pos) {
            if (batterySupplier == null)
                return null;
            return batterySupplier.apply(world, pos);
        }

        @Override
        public String toString() {
            return String.format("ChargeDef{%s, cost=%f, hasBat=%s}", connectType, cost, batterySupplier != null);
        }
    }

    class ChargeBattery implements IChargeBattery {
        public static final double DEFAULT_MAX_CHARGE = 1000.0;
        public static final double DEFAULT_MAX_DRAW = 20.0;
        private final double capacity;

        private final double efficiency;
        private final double maxDraw;

        public ChargeBattery() {
            this(DEFAULT_MAX_CHARGE);
        }

        public ChargeBattery(double capacity) {
            this(capacity, DEFAULT_MAX_DRAW, 1.0);
        }

        public ChargeBattery(double capacity, double maxDraw, double efficiency) {
            this.capacity = capacity;
            this.efficiency = efficiency;
            this.maxDraw = maxDraw;
        }

        private boolean initialized;
        private double charge;

        public boolean isInfinite() {
            return false;
        }

        public final boolean isInitialized() {
            return initialized;
        }

        @Override
        public double getCharge() {
            return charge;
        }

        public double getEfficiency() {
            return efficiency;
        }

        public double getMaxDraw() {
            return maxDraw;
        }

        public void initCharge(double charge) {
            initialized = true;
            setCharge(charge);
        }

        @Override
        public void setCharge(double charge) {
            this.charge = charge;
        }

        @Override
        public double getCapacity() {
            return capacity;
        }

        @Override
        public void addCharge(double charge) {
            this.charge += charge;
        }

        /**
         * Remove up to the requested amount of charge and returns the amount
         * removed.
         *
         * @return charge removed
         */
        @Override
        public double removeCharge(double request) {
            double availableCharge = getAvailableCharge();
            if (availableCharge >= request) {
                charge -= request;
//                lastTickDraw += request;
                return request;
            }
            charge -= availableCharge;
//            lastTickDraw += availableCharge;
            return availableCharge;
        }

        public double getAvailableCharge() {
            return Math.min(charge, getMaxDraw());
        }

        @Override
        public String toString() {
            return String.format("%s@%s { i:%s; c:%.2f; }", getClass().getSimpleName(), Integer.toHexString(hashCode()), initialized, charge);
        }
    }
}
