/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.Maps;
import mods.railcraft.api.charge.ChargeNodeDefinition;
import mods.railcraft.api.charge.ConnectType;
import mods.railcraft.api.charge.IChargeDimension;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by CovertJaguar on 7/23/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class ChargeDimension implements IChargeDimension {

    final ChargeRegion nullGraph;
    private final ChargeNode nullNode;
    final Collection<ChargeNode> tickingNodes = new ArrayList<>();

    // New fields
    private static final int MAX_PROCESS = Integer.MAX_VALUE;
    private final World world;
    private final ChargeSaveData chargeSaveData;
    private final Set<ChargeRegion> chargeRegions = Collections.newSetFromMap(new WeakHashMap<>());
    private final Map<BlockPos, ChargeNode> chargeNodes = new HashMap<>();
    private final Map<BlockPos, ChargeNodeDefinition> chargeDefinitions;
    private final Queue<ChargeNode> additions = new ArrayDeque<>();
    private final Queue<BlockPos> removals = new ArrayDeque<>();

    ChargeDimension(World world) {
        this.world = world;
        nullGraph = new NullRegion(this);
        nullNode = new NullNode(this);
        this.chargeSaveData = ChargeSaveData.forWorld(world);
        this.chargeDefinitions = this.chargeSaveData.getPositions();
        printDebug("Detected {0} nodes from the save", chargeDefinitions.size());
        initOldNodes();
    }

    private void initOldNodes() {
        Collection<ChargeNode> nodes = new ArrayList<>();
        for (Entry<BlockPos, ChargeNodeDefinition> entry : chargeDefinitions.entrySet()) {
            BlockPos pos = entry.getKey();
            ChargeNode node = new ChargeNode(this, pos, entry.getValue());
            chargeNodes.put(pos, node);
            if (node.chargeBattery != null) {
                chargeSaveData.initBattery(pos, node.chargeBattery);
            }
            nodes.add(node);
        }
        new BreathFistSearcher(nodes).search();
    }

    void printDebug(String msg, Object... args) {
        if (RailcraftConfig.printChargeDebug())
            Game.log(Level.INFO, msg, args);
    }

    void tick() {
        tickingNodes.removeIf(chargeNode -> !chargeNode.tickUsageRecording());

        // Process the queue of nodes waiting to be added/removed from the network

        Set<ChargeNode> graphLess = new HashSet<>();
        List<ChargeNode> avoid = new ArrayList<>();

        int counter = 0;
        while (!removals.isEmpty() && counter < MAX_PROCESS) {
            BlockPos removal = removals.remove();
            ChargeNode got = deleteNode(removal);
            if (got == null) {
                continue;
            }
            avoid.add(got);
            chargeRegions.remove(got.chargeRegion);
            graphLess.addAll(got.chargeRegion.getNodes());
            counter++;
        }

        for (ChargeNode each : avoid) {
            graphLess.remove(each);
        }

        while (!additions.isEmpty() && counter < MAX_PROCESS) {
            ChargeNode node = additions.remove();
            addNode(node.getPos(), node);
            graphLess.add(node);
        }

        if (!removals.isEmpty() || !additions.isEmpty()) {
            printDebug("Removals queued: {0}; Additions queued: {1}", removals.size(), additions.size());
        }

        new BreathFistSearcher(graphLess).search();

        getChargeRegions().forEach(ChargeRegion::tick);
    }

    /**
     * Add the node to the network and clean up any node that used to exist there
     */
    private void addNode(BlockPos pos, ChargeNode node) {
        chargeNodes.put(pos, node);
        chargeDefinitions.put(pos, node.getDefinition());
        chargeSaveData.markDirty();
        // update the battery in the save data tracker
        if (node.chargeBattery != null)
            chargeSaveData.initBattery(pos, node.chargeBattery);
    }

    @Nullable
    private ChargeNode deleteNode(BlockPos pos) {
        ChargeNode chargeNode = chargeNodes.remove(pos);
        chargeDefinitions.remove(pos);
        chargeSaveData.markDirty();
        if (chargeNode != null) {
            chargeNode.invalid = true;
            chargeSaveData.removeBattery(pos);
        }
        return chargeNode;
    }

    /**
     * Queues the node to be added to the network
     */
    @Override
    public void registerChargeNode(BlockPos pos, ChargeNodeDefinition chargeDef) {
        printDebug("Registering charge node at {0} with definition {1}.", pos, chargeDef);
        if (!isUndefined(pos)) {
            deleteNode(pos);
        }
        additions.add(new ChargeNode(this, pos, chargeDef));
    }

    /**
     * Queues the node to be removed to the network
     */
    @Override
    public void deregisterChargeNode(BlockPos pos) {
        printDebug("Removing charge node at {0}.", pos);
        removals.add(pos);
    }

    public boolean isUndefined(BlockPos pos) {
        return !chargeNodes.containsKey(pos);
    }

    public ChargeRegion getGraph(BlockPos pos) {
        return getNode(pos).getChargeRegion();
    }

    /**
     * Get any node for the position and add it to the charge network/regions if it isn't already
     */
    @Override
    public ChargeNode getNode(BlockPos pos) {
        ChargeNode node = chargeNodes.get(pos);
        if (node == null)
            return nullNode;
        return node;
    }

    public ChargeSaveData getChargeSaveData() {
        return chargeSaveData;
    }

    Set<ChargeRegion> getChargeRegions() {
        return chargeRegions;
    }

    public World getWorld() {
        return world;
    }

    private final class BreathFistSearcher {

        final Map<BlockPos, ChargeNode> graphLess;
        final Set<BlockPos> visited;
        final Set<ChargeRegion> regions;
        final Collection<ChargeNode> collected;

        BreathFistSearcher(Collection<ChargeNode> graphLess) {
            this.graphLess = Maps.uniqueIndex(graphLess, ChargeNode::getPos);
            this.visited = new HashSet<>();
            this.regions = new HashSet<>();
            this.collected = new ArrayList<>();
        }

        void search() {
            for (Entry<BlockPos, ChargeNode> entry : graphLess.entrySet()) {
                BlockPos pos = entry.getKey();
                if (!visited.contains(pos)) {
                    bfsGraph(pos, entry.getValue());
                    merge();
                    regions.clear();
                    collected.clear();
                }
            }
        }

        private void bfsGraph(BlockPos current, ChargeNode initialNode) {
            Queue<ChargeNode> queue = new ArrayDeque<>();
            queue.add(initialNode);
            visited.add(current);
            while (!queue.isEmpty()) {
                ChargeNode now = queue.remove();
                collected.add(now);
                for (Map.Entry<BlockPos, EnumSet<ConnectType>> entry : now.getConnectType().getPossibleConnectionLocations(now.getPos()).entrySet()) {
                    BlockPos pos = entry.getKey();
                    if (!visited.contains(pos)) {
                        ChargeNode node = graphLess.get(pos);
                        if (node != null && entry.getValue().contains(node.getConnectType())) {
                            visited.add(pos);
                            queue.add(node);
                            continue;
                        }

                        ChargeNode outside = getOutsideNode(pos);
                        if (!outside.isNull() && entry.getValue().contains(outside.getConnectType())) {
                            regions.add(outside.chargeRegion);
                        }
                    }
                }
            }

        }

        private void merge() {
            ChargeRegion target;
            if (regions.isEmpty()) {
                target = new ChargeRegion(ChargeDimension.this);
            } else {
                target = regions.iterator().next();
                regions.remove(target);
            }

            for (ChargeNode node : collected) {
                target.add(node);
            }

            for (ChargeRegion graph : regions) {
                for (ChargeNode node : graph.getNodes()) {
                    target.add(node);
                }
                ChargeDimension.this.getChargeRegions().remove(graph);
            }
        }

        private ChargeNode getOutsideNode(BlockPos pos) {
            return ChargeDimension.this.getNode(pos);
        }
    }
}
