package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.ChargeNodeDefinition;
import mods.railcraft.api.charge.ConnectType;
import net.minecraft.util.math.BlockPos;

/**
 *
 */
public class NullNode extends ChargeNode {
    private ChargeDimension chargeNetwork;

    public NullNode(ChargeDimension chargeNetwork) {
        super(chargeNetwork, BlockPos.ORIGIN, new ChargeNodeDefinition(ConnectType.BLOCK, 0.0, null));
        this.chargeNetwork = chargeNetwork;
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public ChargeRegion getChargeRegion() {
        return chargeNetwork.nullGraph;
    }

    @Override
    public String toString() {
        return "ChargeNode{NullNode}";
    }
}
