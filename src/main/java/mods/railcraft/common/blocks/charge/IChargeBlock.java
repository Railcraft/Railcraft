/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
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
        ChargeNetwork chargeNetwork = ChargeManager.getNetwork(world);
        if (chargeDef != null && !chargeNetwork.nodeMatches(pos, chargeDef)) {
            chargeNetwork.registerChargeNode(world, pos, chargeDef);
            Set<BlockPos> visitedNodes = new HashSet<>();
            visitedNodes.add(pos);
            Set<BlockPos> registeredNodes = new HashSet<>();
            registeredNodes.add(pos);
            Set<BlockPos> newNodes = new HashSet<>();
            newNodes.add(pos);
            while (!newNodes.isEmpty() && registeredNodes.size() < 500) {
                Set<BlockPos> currentNodes = new HashSet<>(newNodes);
                newNodes.clear();
                for (BlockPos current : currentNodes) {
                    IBlockState cState = WorldPlugin.getBlockState(world, current);
                    if (cState.getBlock() instanceof IChargeBlock) {
                        ((IChargeBlock) cState.getBlock()).forChargeConnections(world, current, chargeDef, (conPos, conDef) -> {
                            if (!visitedNodes.contains(conPos) && chargeNetwork.isUndefined(conPos)) {
                                visitedNodes.add(conPos);
                                newNodes.add(conPos);
                                if (chargeNetwork.registerChargeNode(world, conPos, conDef))
                                    registeredNodes.add(conPos);
                            }
                        });
                    }
                }
            }
            Game.log(Level.INFO, "Nodes registered: {0} Nodes visited: {1}", registeredNodes.size(), visitedNodes.size());
        }
    }

    default void deregisterNode(World world, BlockPos pos) {
        ChargeManager.getNetwork(world).deregisterChargeNode(pos);
    }

    default void forChargeConnections(World world, BlockPos pos, ChargeDef chargeDef, BiConsumer<BlockPos, ChargeDef> action) {
        Map<BlockPos, EnumSet<IChargeBlock.ConnectType>> possibleConnections = chargeDef.getConnectType().getPossibleConnectionLocations(pos);
        for (Map.Entry<BlockPos, EnumSet<IChargeBlock.ConnectType>> connection : possibleConnections.entrySet()) {
            IBlockState otherState = WorldPlugin.getBlockState(world, connection.getKey());
            if (otherState.getBlock() instanceof IChargeBlock) {
                ChargeDef other = ((IChargeBlock) otherState.getBlock()).getChargeDef(WorldPlugin.getBlockState(world, connection.getKey()), world, connection.getKey());
                if (other != null && other.getConnectType().getPossibleConnectionLocations(connection.getKey()).get(pos).contains(chargeDef.getConnectType())) {
                    action.accept(connection.getKey(), other);
                }
            }
        }
    }

    enum ConnectType {

        TRACK {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                Map<BlockPos, EnumSet<ConnectType>> positions = new HashMap<BlockPos, EnumSet<ConnectType>>();

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
                Map<BlockPos, EnumSet<ConnectType>> positions = new HashMap<BlockPos, EnumSet<ConnectType>>();

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
                Map<BlockPos, EnumSet<ConnectType>> positions = new HashMap<BlockPos, EnumSet<ConnectType>>();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);

                for (EnumFacing facing : EnumFacing.VALUES) {
                    positions.put(pos.offset(facing), all);
                }
                return positions;
            }

        };

        @Nonnull
        public abstract Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos);

    }

    final class ChargeDef {
        @Nonnull
        private final ConnectType connectType;
        private final double cost;
        private final BiFunction<World, BlockPos, ChargeBattery> batterySupplier;

        public ChargeDef(@Nonnull ConnectType connectType, double cost) {
            this(connectType, cost, null);
        }

        public ChargeDef(@Nonnull ConnectType connectType, @Nullable BiFunction<World, BlockPos, ChargeBattery> batterySupplier) {
            this(connectType, 0.0, batterySupplier);
        }

        private ChargeDef(@Nonnull ConnectType connectType, double cost, @Nullable BiFunction<World, BlockPos, ChargeBattery> batterySupplier) {
            this.connectType = connectType;
            this.cost = cost;
            this.batterySupplier = batterySupplier;
        }

        public double getCost() {
            return cost;
        }

        ConnectType getConnectType() {
            return connectType;
        }

        @Nullable
        ChargeBattery makeBattery(World world, BlockPos pos) {
            if (batterySupplier == null)
                return null;
            return batterySupplier.apply(world, pos);
        }

        @Override
        public String toString() {
            return String.format("ChargeDef{%s, cost=%f, hasBat=%s}", connectType, cost, batterySupplier != null);
        }
    }

    class ChargeBattery {
        public static final String NBT_CHARGE_TAG = "charge";
        public static final double MAX_CHARGE = 20000.0;
        private double charge;

        public double getCharge() {
            return charge;
        }

        public void setCharge(double charge) {
            this.charge = charge;
        }

        public double getCapacity() {
            return MAX_CHARGE;
        }

        public void addCharge(double charge) {
            this.charge += charge;
        }

        /**
         * Remove up to the requested amount of charge and returns the amount
         * removed.
         *
         * @return charge removed
         */
        public double removeCharge(double request) {
            if (charge >= request) {
                charge -= request;
//                lastTickDraw += request;
                return request;
            }
            double ret = charge;
            charge = 0.0;
//            lastTickDraw += ret;
            return ret;
        }

        /**
         * Must be called by the owning object's save function.
         */
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setDouble(NBT_CHARGE_TAG, charge);
        }

        /**
         * Must be called by the owning object's load function.
         */
        public void readFromNBT(NBTTagCompound nbt) {
            charge = nbt.getDouble(NBT_CHARGE_TAG);
        }
    }
}
