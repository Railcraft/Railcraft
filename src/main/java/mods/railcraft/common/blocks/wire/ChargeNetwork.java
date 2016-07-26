/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.wire;

import com.google.common.collect.ForwardingSet;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * Created by CovertJaguar on 7/23/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeNetwork {
    public static final Map<BlockPos, ChargeNode> chargeNodes = new HashMap<>();
    private static final ChargeGraph NULL_GRAPH = new ChargeGraph() {
        @Override
        protected Set<ChargeNode> delegate() {
            return Collections.emptySet();
        }
    };

    public ChargeProducer registerChargeProducer(BlockPos pos, float runCost) {
        ChargeProducer producer = new ChargeProducer(pos, runCost);
        registerChargeNode(producer);
        return producer;
    }

    public void registerChargeNode(BlockPos pos, float runCost) {
        registerChargeNode(new ChargeNode(pos, runCost));
    }

    private void registerChargeNode(ChargeNode node) {
        chargeNodes.put(node.pos, node);
        // TODO: merge with neighbor graphs
    }

    public void deregisterChargeNode(BlockPos pos) {
        ChargeNode chargeNode = chargeNodes.remove(pos);
        if (chargeNode != null) {
            chargeNode.invalid = true;
            chargeNode.chargeGraph.destroy();
        }
    }

    public ChargeGraph getChargeTree(BlockPos pos) {
        ChargeNode chargeNode = chargeNodes.get(pos);
        if (chargeNode == null)
            return NULL_GRAPH;
        return chargeNode.getChargeGraph();
    }

    private static class ChargeNode {
        final BlockPos pos;
        final double runCost;
        ChargeGraph chargeGraph = NULL_GRAPH;
        boolean invalid;

        public ChargeNode(BlockPos pos, double runCost) {
            this.pos = pos;
            this.runCost = runCost;
        }

        private void constructTree() {
            Set<ChargeNode> allTheNodes = new HashSet<>();
            Set<ChargeNode> newNodes = new HashSet<>();
            TreeSet<ChargeGraph> graphs = new TreeSet<>((o1, o2) -> Integer.compare(o1.size(), o2.size()));
            newNodes.add(this);
            while (!newNodes.isEmpty()) {
                Set<ChargeNode> currentNodes = newNodes;
                newNodes = new HashSet<>();
                for (ChargeNode current : currentNodes) {
                    // TODO: this is wrong, it assumes 2D world, need to steel old code
                    for (EnumFacing side : EnumFacing.VALUES) {
                        ChargeNode neighbor = chargeNodes.get(current.pos.offset(side));
                        if (neighbor != null && !allTheNodes.contains(neighbor)) {
                            graphs.add(neighbor.chargeGraph);
                            allTheNodes.addAll(neighbor.chargeGraph);
                            allTheNodes.add(neighbor);
                            newNodes.add(neighbor);
                        }
                    }
                }
            }
            chargeGraph = graphs.last();
            chargeGraph.addAll(allTheNodes);
            chargeGraph.removeIf(n -> n.invalid);
            chargeGraph.forEach(n -> n.chargeGraph = chargeGraph);
            graphs.remove(chargeGraph);
            graphs.forEach(ChargeGraph::clear);
        }

        public ChargeGraph getChargeGraph() {
            if (chargeGraph != NULL_GRAPH)
                return chargeGraph;
            constructTree();
            return chargeGraph;
        }
    }

    public static class ChargeGraph extends ForwardingSet<ChargeNode> {
        private final Set<ChargeNode> chargeNodes = new HashSet<>();

        @Override
        protected Set<ChargeNode> delegate() {
            return chargeNodes;
        }

        public void merge(ChargeGraph chargeGraph) {
            if (chargeGraph == this)
                return;
            chargeGraph.forEach(n -> n.chargeGraph = this);
            addAll(chargeGraph);
            removeIf(n -> n.invalid);
            chargeGraph.clear();
        }

        public void destroy() {
            forEach(n -> n.chargeGraph = NULL_GRAPH);
            clear();
        }
        //TODO: add power manipulation (tick?)
    }

    public static class ChargeProducer extends ChargeNode {
        public ChargeProducer(BlockPos pos, double runCost) {
            super(pos, runCost);
        }
        //TODO: add power pool
    }
}
