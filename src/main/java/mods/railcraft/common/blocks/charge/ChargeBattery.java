package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.charge.IBlockBattery;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

/**
 *
 */
public class ChargeBattery implements IBlockBattery {
    public static final double DEFAULT_MAX_CHARGE = 1000.0;
    public static final double DEFAULT_MAX_DRAW = 20.0;
    private final double capacity;
    private final double efficiency;
    private final double maxDraw;
    private boolean initialized;
    private double charge;

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

    @Override
    public double getEfficiency() {
        return efficiency;
    }

    @Override
    public double getMaxDraw() {
        return maxDraw;
    }

    @Override
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
    public double addCharge(double charge) {
        double result = this.charge + charge;
        if (result > capacity) {
            this.charge = capacity;
            return result - capacity;
        } else {
            this.charge = result;
            return 0;
        }
    }

    /**
     * Remove up to the requested amount of charge and returns the amount
     * removed.
     *
     * @return charge removed
     */
    public double removeCharge(double request) {
        double availableCharge = getAvailableCharge();
        if (availableCharge >= request) {
            charge -= request;
            return request;
        }
        charge -= availableCharge;
        return availableCharge;
    }

    public double getAvailableCharge() {
        return Math.min(charge, getMaxDraw());
    }

    @Override
    public String toString() {
        return String.format("%s@%s { cap:%.2f; eff:%.2f; max:%.2f; c:%.2f; }", getClass().getSimpleName(), Integer.toHexString(hashCode()), capacity, efficiency, maxDraw, charge);
    }

    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("charge", NBT.TAG_DOUBLE)) {
            initCharge(tag.getDouble("charge"));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setDouble("charge", charge);
        return tag;
    }
}
