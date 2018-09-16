package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.IBlockBattery;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ChargeRegion {
    private final ChargeDimension dimension;
    private final Set<ChargeNode> nodes = new HashSet<>();
    private final Set<ChargeNode> batteryNodes = new HashSet<>();
    private double totalMaintenanceCost;
    private double chargeUsedThisTick;
    private double averageUsagePerTick;

    public ChargeRegion(ChargeDimension dimension) {
        this.dimension = dimension;
    }

    public Set<ChargeNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    void add(ChargeNode chargeNode) {
        nodes.add(chargeNode);
        totalMaintenanceCost += chargeNode.getMaintenanceCost();
        chargeNode.chargeRegion = this;
        if (chargeNode.chargeBattery != null)
            batteryNodes.add(chargeNode);
    }

    void tick() {
        removeCharge(totalMaintenanceCost);

        // balance the charge in all the batteries in the graph
        double capacity = getCapacity();
        if (capacity > 0.0) {
            final double chargeLevel = getCharge() / capacity;
            batteryNodes.forEach((k) -> {
                IBlockBattery v = k.chargeBattery;
                v.setCharge(chargeLevel * v.getCapacity());
                dimension.getChargeSaveData().updateBatteryRecord(k.getPos(), v);
            });
        }

        // track usage patterns
        averageUsagePerTick = (averageUsagePerTick * 49D + chargeUsedThisTick) / 50D;
        chargeUsedThisTick = 0.0;
    }

    void updateBattery(ChargeNode node) {
        if (node.chargeBattery == null) {
            batteryNodes.remove(node);
        } else {
            batteryNodes.add(node);
        }
    }

    public double getCharge() {
        return batteryNodes.stream().map((node) -> node.chargeBattery).mapToDouble(IBlockBattery::getCharge).sum();
    }

    public double getCapacity() {
        return batteryNodes.stream().map((node) -> node.chargeBattery).mapToDouble(IBlockBattery::getCapacity).sum();
    }

    public double getMaxNetworkDraw() {
        return batteryNodes.stream().map((node) -> node.chargeBattery).mapToDouble(IBlockBattery::getAvailableCharge).sum();
    }

    public double getNetworkEfficiency() {
        return batteryNodes.stream().map((node) -> node.chargeBattery).mapToDouble(IBlockBattery::getEfficiency).average().orElse(1.0);
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

    public double getUsageRatio() {
        if (isInfinite())
            return 0.0;
        double maxDraw = getMaxNetworkDraw();
        if (maxDraw <= 0.0)
            return 1.0;
        return Math.min(getAverageUsagePerTick() / maxDraw, 1.0);
    }

    public boolean isInfinite() {
        return batteryNodes.stream().map((node) -> node.chargeBattery).anyMatch(battery -> (battery instanceof ChargeBattery && ((ChargeBattery) battery).isInfinite()));
    }

    /**
     * Remove the requested amount of charge if possible and
     * return whether sufficient charge was available to perform the operation.
     *
     * @return true if charge could be removed in full
     */
    public boolean useCharge(double amount) {
        double efficiency = getNetworkEfficiency();
        if (getMaxNetworkDraw() >= amount / efficiency) {
            removeCharge(amount, efficiency);
            return true;
        }
        return false;
    }

    public boolean canUseCharge(double amount) {
        return getMaxNetworkDraw() >= amount / getNetworkEfficiency();
    }

    /**
     * Remove up to the requested amount of charge and returns the amount
     * removed.
     *
     * @return charge removed
     */
    public double removeCharge(double desiredAmount) {
        return removeCharge(desiredAmount, getNetworkEfficiency());
    }

    /**
     * Remove up to the requested amount of charge and returns the amount
     * removed.
     *
     * @return charge removed
     */
    private double removeCharge(double desiredAmount, double efficiency) {
        final double amountToDraw = desiredAmount / efficiency;
        double amountNeeded = amountToDraw;
        for (ChargeNode node : batteryNodes) {
            IBlockBattery battery = node.chargeBattery;
            amountNeeded -= battery.removeCharge(amountNeeded);
            dimension.getChargeSaveData().updateBatteryRecord(node.getPos(), battery);
            if (amountNeeded <= 0.0)
                break;
        }
        double chargeRemoved = amountToDraw - amountNeeded;
        chargeUsedThisTick += chargeRemoved;
        return chargeRemoved * efficiency;
    }

    @Override
    public String toString() {
        return String.format("ChargeRegion{@=%d,s=%d,b=%d}", hashCode(), nodes.size(), batteryNodes.size());
    }
}
