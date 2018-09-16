package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.*;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.BiConsumer;

/**
 *
 */
public class ChargeNode implements IChargeNode {
    private final ChargeDimension chargeNetwork;
    private final BlockPos pos;
    private final ChargeNodeDefinition chargeDef;
    protected IBlockBattery chargeBattery;
    ChargeRegion chargeRegion;
    boolean invalid;

    // TODO move to listeners
    private boolean recording;
    private double chargeUsedRecorded;
    private int ticksToRecord;
    private int ticksRecorded;
    // TODO sort out
    @Nullable
    private BiConsumer<ChargeNode, Double> usageConsumer;
    private Collection<BiConsumer<? super IChargeNode, ? super Double>> listeners = new LinkedHashSet<>();

    ChargeNode(ChargeDimension chargeDimension, BlockPos pos, ChargeNodeDefinition chargeDef) {
        this.chargeNetwork = chargeDimension;
        this.pos = pos;
        this.chargeDef = chargeDef;
        this.chargeBattery = chargeDef.createBattery(chargeDimension.getWorld(), pos);
        this.chargeRegion = chargeDimension.nullGraph;
    }

    ChargeNodeDefinition getDefinition() {
        return chargeDef;
    }

    ConnectType getConnectType() {
        return chargeDef.getConnectType();
    }

    public ChargeRegion getChargeRegion() {
        return chargeRegion;
    }

    @Override
    public void addListener(BiConsumer<? super IChargeNode, ? super Double> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(BiConsumer<? super IChargeNode, ? super Double> listener) {
        listeners.remove(listener);
    }

    public void startRecordingUsage(int ticksToRecord, BiConsumer<ChargeNode, Double> usageConsumer) {
        recording = true;
        this.ticksToRecord = ticksToRecord;
        this.usageConsumer = usageConsumer;
        chargeUsedRecorded = 0.0;
        ticksRecorded = 0;
        chargeNetwork.tickingNodes.add(this);
    }

    public boolean tickUsageRecording() {
        ticksRecorded++;
        if (ticksRecorded > ticksToRecord) {
            recording = false;
            double averageUsage = chargeUsedRecorded / ticksToRecord;
            if (usageConsumer != null)
                usageConsumer.accept(this, averageUsage);
            usageConsumer = null;
            chargeUsedRecorded = 0.0;
            ticksToRecord = 0;
            ticksRecorded = 0;
        }
        return recording;
    }

    @Override
    public boolean canUseCharge(double amount) {
        return chargeRegion.canUseCharge(amount);
    }

    /**
     * Remove the requested amount of charge if possible and
     * return whether sufficient charge was available to perform the operation.
     *
     * @return true if charge could be removed in full
     */
    @Override
    public boolean useCharge(double amount) {
        boolean removed = chargeRegion.useCharge(amount);
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
    @Override
    public double removeCharge(double desiredAmount) {
        double removed = chargeRegion.removeCharge(desiredAmount);
        listeners.forEach(c -> c.accept(this, removed));
        if (recording)
            chargeUsedRecorded += removed;
        return removed;
    }

    public boolean isNull() {
        return false;
    }

    public double getMaintenanceCost() {
        return chargeDef.getMaintenanceCost() * RailcraftConfig.chargeMaintenanceCostMultiplier();
    }

    @Override
    public IChargeDimension getDimension() {
        return chargeNetwork;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public IBlockBattery getBattery() {
        return chargeBattery;
    }

    @Override
    public void unloadBattery() {
        this.chargeBattery = null;
        if (chargeRegion != chargeNetwork.nullGraph) {
            chargeRegion.updateBattery(this);
        }
    }

    @Override
    public void loadBattery() {
        this.chargeBattery = chargeDef.createBattery(chargeNetwork.getWorld(), pos);
        if (chargeRegion != chargeNetwork.nullGraph) {
            chargeRegion.updateBattery(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChargeNode that = (ChargeNode) o;

        return getPos().equals(that.getPos());
    }

    @Override
    public int hashCode() {
        return getPos().hashCode();
    }

    @Override
    public String toString() {
        return String.format("ChargeNode{%s|%s}", getPos(), chargeDef);
    }
}
