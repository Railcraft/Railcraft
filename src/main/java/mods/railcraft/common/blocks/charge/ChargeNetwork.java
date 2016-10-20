/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Iterators;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 7/23/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeNetwork {
    private final ChargeGraph NULL_GRAPH = new NullGraph();
    public final Map<BlockPos, ChargeNode> chargeNodes = new HashMap<>();
    public final Map<BlockPos, ChargeNode> chargeQueue = new LinkedHashMap<>();
    public final Set<ChargeNode> tickingNodes = new LinkedHashSet<>();
    public final Set<ChargeGraph> chargeGraphs = Collections.newSetFromMap(new WeakHashMap<>());
    private final ChargeNode NULL_NODE = new NullNode();
    private final WeakReference<World> world;
    private final BatterySaveData batterySaveData;

    public ChargeNetwork(World world) {
        this.world = new WeakReference<World>(world);
        this.batterySaveData = BatterySaveData.forWorld(world);
    }

    public void tick() {
        World worldObj = world.get();
        if (worldObj == null)
            return;
        Iterator<ChargeNode> nodeIterator = tickingNodes.iterator();
        while (nodeIterator.hasNext()) {
            ChargeNode chargeNode = nodeIterator.next();
            if (!chargeNode.tickUsageRecording())
                nodeIterator.remove();
        }

        Map<BlockPos, ChargeNode> added = new LinkedHashMap<>();
        Iterator<Map.Entry<BlockPos, ChargeNode>> iterator = chargeQueue.entrySet().iterator();
        int count = 0;
        while (iterator.hasNext() && count < 500) {
            count++;
            Map.Entry<BlockPos, ChargeNode> action = iterator.next();
            if (action.getValue() == null) {
                deleteNode(action.getKey());
            } else {
                insertNode(action.getKey(), action.getValue());
                added.put(action.getKey(), action.getValue());
            }
            iterator.remove();
        }

        Set<BlockPos> newNodes = new HashSet<>();
        for (Map.Entry<BlockPos, ChargeNode> addedNode : added.entrySet()) {
            ChargeManager.forConnections(worldObj, addedNode.getKey(), (conPos, conDef) -> {
                if (registerChargeNode(worldObj, conPos, conDef))
                    newNodes.add(conPos);
            });
            if (addedNode.getValue().isGraphNull())
                addedNode.getValue().constructGraph();
        }

        chargeGraphs.removeIf(g -> g.invalid);
        chargeGraphs.forEach(ChargeGraph::tick);

        if (!newNodes.isEmpty())
            Game.log(Level.INFO, "Nodes queued: {0}", newNodes.size());
    }

    private void insertNode(BlockPos pos, ChargeNode node) {
        ChargeNode oldNode = chargeNodes.put(pos, node);

        if (node.chargeBattery != null)
            batterySaveData.initBattery(pos, node.chargeBattery);
        else
            batterySaveData.removeBattery(pos);

        if (oldNode != null) {
            oldNode.invalid = true;
            if (oldNode.chargeGraph.isActive()) {
                node.chargeGraph = oldNode.chargeGraph;
                node.chargeGraph.add(node);
            }
            oldNode.chargeGraph = NULL_GRAPH;
        }
    }

    private void deleteNode(BlockPos pos) {
        ChargeNode chargeNode = chargeNodes.remove(pos);
        if (chargeNode != null) {
            chargeNode.invalid = true;
            chargeNode.chargeGraph.destroy(true);
        }
        batterySaveData.removeBattery(pos);
    }

    public boolean registerChargeNode(World world, BlockPos pos, IChargeBlock.ChargeDef chargeDef) {
        if (!nodeMatches(pos, chargeDef)) {
//            Game.log(Level.INFO, "Registering Node: {0}->{1}", pos, chargeDef);
            chargeQueue.put(pos, new ChargeNode(pos, chargeDef, chargeDef.makeBattery(world, pos)));
            return true;
        }
        return false;
    }

    public void deregisterChargeNode(BlockPos pos) {
        chargeQueue.put(pos, null);
    }

    public boolean isUndefined(BlockPos pos) {
        ChargeNode chargeNode = chargeNodes.get(pos);
        return chargeNode == null || chargeNode.isGraphNull() || !chargeQueue.containsKey(pos);
    }

    public ChargeGraph getGraph(BlockPos pos) {
        return getNode(pos).getChargeGraph();
    }

    public ChargeNode getNode(BlockPos pos) {
        ChargeNode node = chargeNodes.get(pos);
        if (node != null && node.invalid) {
            deleteNode(pos);
            node = null;
        }
        if (node == null) {
            World worldObj = world.get();
            if (worldObj != null) {
                IBlockState state = WorldPlugin.getBlockState(worldObj, pos);
                if (state.getBlock() instanceof IChargeBlock) {
                    IChargeBlock.ChargeDef chargeDef = ((IChargeBlock) state.getBlock()).getChargeDef(state, worldObj, pos);
                    if (chargeDef != null) {
                        node = new ChargeNode(pos, chargeDef, chargeDef.makeBattery(worldObj, pos));
                        insertNode(pos, node);
                        if (node.isGraphNull())
                            node.constructGraph();
                    }
                }
            }
        }
        if (node == null)
            return NULL_NODE;
        return node;
    }

    public boolean nodeMatches(BlockPos pos, IChargeBlock.ChargeDef chargeDef) {
        ChargeNode node = chargeNodes.get(pos);
        return node != null && !node.isNull() && !node.invalid && node.chargeDef == chargeDef;
    }

    public class ChargeGraph extends ForwardingSet<ChargeNode> {
        private final Set<ChargeNode> chargeNodes = new HashSet<>();
        private final Map<ChargeNode, IChargeBlock.ChargeBattery> chargeBatteries = new LinkedHashMap<>();
        private boolean invalid;
        private double totalMaintenanceCost;
        private double chargeUsedThisTick;
        private double averageUsagePerTick;

        @Override
        protected Set<ChargeNode> delegate() {
            return chargeNodes;
        }

        @Override
        public boolean add(ChargeNode chargeNode) {
            boolean added = super.add(chargeNode);
            if (added) {
                totalMaintenanceCost += chargeNode.chargeDef.getMaintenanceCost();
                chargeNode.chargeGraph = this;
                if (chargeNode.chargeBattery != null)
                    chargeBatteries.put(chargeNode, chargeNode.chargeBattery);
                else {
                    chargeBatteries.remove(chargeNode);
                    batterySaveData.removeBattery(chargeNode.pos);
                }
            }
            return added;
        }

        @Override
        public boolean addAll(Collection<? extends ChargeNode> collection) {
            return standardAddAll(collection);
        }

        @Override
        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(Predicate<? super ChargeNode> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<ChargeNode> iterator() {
            return Iterators.unmodifiableIterator(super.iterator());
        }

        private void destroy(boolean touchNodes) {
            if (isActive()) {
                Game.log(Level.INFO, "Destroying graph: {0}", this);
                invalid = true;
                totalMaintenanceCost = 0.0;
                if (touchNodes) {
                    forEach(n -> n.chargeGraph = NULL_GRAPH);
                }
                chargeBatteries.clear();
                super.clear();
                chargeGraphs.remove(this);
            }
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        private void tick() {
            removeCharge(totalMaintenanceCost);
            double averageCharge = chargeBatteries.values().stream().mapToDouble(IChargeBlock.ChargeBattery::getCharge).average().orElse(0.0);
            if (averageCharge < 0.0)
                averageCharge = 0.0;
            double finalCharge = averageCharge;
            chargeBatteries.entrySet().forEach(b -> {
                b.getValue().setCharge(finalCharge);
                batterySaveData.updateBatteryRecord(b.getKey().pos, b.getValue());
            });
            averageUsagePerTick = (averageUsagePerTick * 49D + chargeUsedThisTick) / 50D;
            chargeUsedThisTick = 0.0;
        }

        public double getCharge() {
            return chargeBatteries.values().stream().mapToDouble(IChargeBlock.ChargeBattery::getCharge).sum();
        }

        public double getCapacity() {
            return chargeBatteries.values().stream().mapToDouble(IChargeBlock.ChargeBattery::getCapacity).sum();
        }

        public int getComparatorOutput() {
            double level = getCharge() / getCapacity();
            return Math.round((float) (15.0 * level));
        }

        public double getMaintenanceCost() {
            return totalMaintenanceCost;
        }

        public double getAverageUsagePerTick() {
            return averageUsagePerTick;
        }

        public boolean isInfinite() {
            return chargeBatteries.values().stream().anyMatch(IChargeBlock.ChargeBattery::isInfinite);
        }

        public boolean isActive() {
            return !isNull();
        }

        public boolean isNull() {
            return false;
        }

        /**
         * Remove the requested amount of charge if possible and
         * return whether sufficient charge was available to perform the operation.
         *
         * @return true if charge could be removed in full
         */
        public boolean useCharge(double amount) {
            double searchAmount = 0;
            for (IChargeBlock.ChargeBattery battery : chargeBatteries.values()) {
                searchAmount += battery.getCharge();
                if (searchAmount >= amount)
                    break;
            }
            if (searchAmount >= amount) {
                for (Map.Entry<ChargeNode, IChargeBlock.ChargeBattery> battery : chargeBatteries.entrySet()) {
                    amount -= battery.getValue().removeCharge(amount);
                    batterySaveData.updateBatteryRecord(battery.getKey().pos, battery.getValue());
                    if (amount <= 0.0)
                        break;
                }
                chargeUsedThisTick += amount;
            }
            return amount <= 0.0;
        }

        /**
         * Remove up to the requested amount of charge and returns the amount
         * removed.
         *
         * @return charge removed
         */
        public double removeCharge(double desiredAmount) {
            double amountNeeded = desiredAmount;
            for (Map.Entry<ChargeNode, IChargeBlock.ChargeBattery> battery : chargeBatteries.entrySet()) {
                amountNeeded -= battery.getValue().removeCharge(amountNeeded);
                batterySaveData.updateBatteryRecord(battery.getKey().pos, battery.getValue());
                if (amountNeeded <= 0.0)
                    break;
            }
            double chargeRemoved = desiredAmount - amountNeeded;
            chargeUsedThisTick += chargeRemoved;
            return chargeRemoved;
        }

        @Override
        public String toString() {
            return String.format("ChargeGraph{s=%d,b=%d}", size(), chargeBatteries.size());
        }
    }

    private class NullGraph extends ChargeGraph {
        @Override
        protected Set<ChargeNode> delegate() {
            return Collections.emptySet();
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public String toString() {
            return "ChargeGraph{NullGraph}";
        }
    }

    public class ChargeNode {
        @Nullable
        protected final IChargeBlock.ChargeBattery chargeBattery;
        private final BlockPos pos;
        private final IChargeBlock.ChargeDef chargeDef;
        private ChargeGraph chargeGraph = NULL_GRAPH;
        private boolean invalid;
        private boolean recording;
        private double chargeUsedRecorded;
        private int ticksToRecord;
        private int ticksRecorded;
        private BiConsumer<ChargeNode, Double> usageConsumer;
        private Collection<BiConsumer<ChargeNode, Double>> listeners = new LinkedHashSet<>();

        private ChargeNode(BlockPos pos, IChargeBlock.ChargeDef chargeDef, @Nullable IChargeBlock.ChargeBattery chargeBattery) {
            this.pos = pos;
            this.chargeDef = chargeDef;
            this.chargeBattery = chargeBattery;
        }

        public IChargeBlock.ChargeDef getChargeDef() {
            return chargeDef;
        }

        private void forConnections(Consumer<ChargeNode> action) {
            Map<BlockPos, EnumSet<IChargeBlock.ConnectType>> possibleConnections = chargeDef.getConnectType().getPossibleConnectionLocations(pos);
            for (Map.Entry<BlockPos, EnumSet<IChargeBlock.ConnectType>> connection : possibleConnections.entrySet()) {
                ChargeNode other = chargeNodes.get(connection.getKey());
                if (other != null && connection.getValue().contains(other.chargeDef.getConnectType())
                        && other.chargeDef.getConnectType().getPossibleConnectionLocations(connection.getKey()).get(pos).contains(chargeDef.getConnectType())) {
                    action.accept(other);
                }
            }
        }

        public ChargeGraph getChargeGraph() {
            if (chargeGraph.isActive())
                return chargeGraph;
            constructGraph();
            return chargeGraph;
        }

        public void addListener(BiConsumer<ChargeNode, Double> listener) {
            listeners.add(listener);
        }

        public void removeListener(BiConsumer<ChargeNode, Double> listener) {
            listeners.remove(listener);
        }

        public void startRecordingUsage(int ticksToRecord, BiConsumer<ChargeNode, Double> usageConsumer) {
            recording = true;
            this.ticksToRecord = ticksToRecord;
            this.usageConsumer = usageConsumer;
            chargeUsedRecorded = 0.0;
            ticksRecorded = 0;
            tickingNodes.add(this);
        }

        public boolean tickUsageRecording() {
            ticksRecorded++;
            if (ticksRecorded > ticksToRecord) {
                recording = false;
                double averageUsage = chargeUsedRecorded / ticksToRecord;
                usageConsumer.accept(this, averageUsage);
                usageConsumer = null;
                chargeUsedRecorded = 0.0;
                ticksToRecord = 0;
                ticksRecorded = 0;
            }
            return recording;
        }

        /**
         * Remove the requested amount of charge if possible and
         * return whether sufficient charge was available to perform the operation.
         *
         * @return true if charge could be removed in full
         */
        public boolean useCharge(double amount) {
            boolean removed = chargeGraph.useCharge(amount);
            if (removed) {
                listeners.forEach(c -> c.accept(this, amount));
                if (recording)
                    chargeUsedRecorded += amount;
            }
            return removed;
        }

        /**
         * @return amount removed, may be less than desiredAmount
         */
        public double removeCharge(double desiredAmount) {
            double removed = chargeGraph.removeCharge(desiredAmount);
            listeners.forEach(c -> c.accept(this, removed));
            if (recording)
                chargeUsedRecorded += removed;
            return removed;
        }

        public boolean isNull() {
            return false;
        }

        public boolean isGraphNull() {
            return chargeGraph.isNull();
        }

        protected void constructGraph() {
            Set<ChargeNode> visitedNodes = new HashSet<>();
            visitedNodes.add(this);
            Set<ChargeNode> nullNodes = new HashSet<>();
            nullNodes.add(this);
            Set<ChargeNode> newNodes = new HashSet<>();
            newNodes.add(this);
            TreeSet<ChargeGraph> graphs = new TreeSet<>((o1, o2) -> Integer.compare(o1.size(), o2.size()));
            graphs.add(chargeGraph);
            while (!newNodes.isEmpty()) {
                Set<ChargeNode> currentNodes = new HashSet<>(newNodes);
                newNodes.clear();
                for (ChargeNode current : currentNodes) {
                    current.forConnections(n -> {
                        if (!visitedNodes.contains(n) && (n.isGraphNull() || !graphs.contains(n.chargeGraph))) {
                            if (n.isGraphNull())
                                nullNodes.add(n);
                            graphs.add(n.chargeGraph);
                            visitedNodes.add(n);
                            newNodes.add(n);
                        }
                    });
                }
            }
            chargeGraph = graphs.pollLast();
            if (chargeGraph.isNull() && nullNodes.size() > 1) {
                chargeGraph = new ChargeGraph();
                chargeGraphs.add(chargeGraph);
            }
            if (chargeGraph.isActive()) {
//                int originalSize = chargeGraph.size();
                chargeGraph.addAll(nullNodes);
                for (ChargeGraph graph : graphs) {
                    chargeGraph.addAll(graph);
                }
                graphs.forEach(g -> g.destroy(false));
//                Game.log(Level.INFO, "Constructing Graph: {0}->{1} Added {2} nodes", pos, chargeGraph, chargeGraph.size() - originalSize);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChargeNode that = (ChargeNode) o;

            return pos.equals(that.pos);
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }

        @Nullable
        public IChargeBlock.ChargeBattery getBattery() {
            return chargeBattery;
        }

        @Override
        public String toString() {
            return String.format("ChargeNode{%s|%s}", pos, chargeDef);
        }
    }

    public class NullNode extends ChargeNode {
        public NullNode() {
            super(new BlockPos(0, 0, 0), new IChargeBlock.ChargeDef(IChargeBlock.ConnectType.BLOCK, 0.0), null);
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public ChargeGraph getChargeGraph() {
            return NULL_GRAPH;
        }

        @Override
        protected void constructGraph() {
        }

        @Override
        public String toString() {
            return "ChargeNode{NullNode}";
        }
    }
}
