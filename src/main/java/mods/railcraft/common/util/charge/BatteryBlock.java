/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.charge;

import mods.railcraft.api.charge.IBatteryBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Created by CovertJaguar on 10/29/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BatteryBlock extends Battery implements IBatteryBlock {

    private final BlockPos pos;
    private final Spec batterySpec;
    private StateImpl stateImpl = StateImpl.RECHARGEABLE;
    private State state = State.RECHARGEABLE;

    public BatteryBlock(BlockPos pos, Spec batterySpec) {
        super(batterySpec.getCapacity());
        this.pos = pos;
        this.batterySpec = batterySpec;
        setState(batterySpec.getInitialState());
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
        this.stateImpl = StateImpl.valueOf(state.name());
    }

    public Spec getBatterySpec() {
        return batterySpec;
    }

    @Override
    public double getCharge() {
        return stateImpl.getCharge(this);
    }

    @Override
    public double getEfficiency() {
        return batterySpec.getEfficiency();
    }

    /**
     * The maximum amount of charge that can be drawn from this battery per tick.
     */
    public double getMaxDraw() {
        return stateImpl.getMaxDraw(this);
    }

    public void tick() {
        chargeDrawnThisTick = 0.0;
//        chargeAddedThisTick = 0.0;
    }

    @Override
    public double getCapacity() {
        return stateImpl.getCapacity(this);
    }

    /**
     * Remove up to the requested amount of charge and returns the amount
     * removed.
     *
     * @return charge removed
     */
    @Override
    public double removeCharge(double request) {
        return stateImpl.removeCharge(this, request);
    }

    @SuppressWarnings("MethodOnlyUsedFromInnerClass")
    private double oldRemoveCharge(double request) {
        return super.removeCharge(request);
    }

    public double getPotentialDraw() {
        return MathHelper.clamp(getMaxDraw(), 0.0, getCharge());
    }

    /**
     * The amount of charge remaining that can be drawn from this battery this tick.
     *
     * @return The amount of charge that can be withdraw from the battery right now
     */
    @Override
    public double getAvailableCharge() {
        return MathHelper.clamp(getMaxDraw() - chargeDrawnThisTick, 0.0, getCharge() * getEfficiency());
    }

    public double getInitialCharge() {
        return state == State.DISPOSABLE ? getCapacity() : 0.0;
    }

    @Override
    public String toString() {
        return String.format("%s@%s { c:%.2f }", getClass().getSimpleName(), Integer.toHexString(hashCode()), charge);
    }

    private enum StateImpl {
        INFINITE {
            @Override
            public double getCharge(BatteryBlock battery) {
                return battery.getCapacity();
            }

            @Override
            public double removeCharge(BatteryBlock battery, double request) {
                return request;
            }
        },
        SOURCE,
        RECHARGEABLE,
        DISPOSABLE,
        DISABLED {
            @Override
            public double getCharge(BatteryBlock battery) {
                return 0.0;
            }

            @Override
            public double getCapacity(BatteryBlock battery) {
                return 0.0;
            }

            @Override
            public double getMaxDraw(BatteryBlock battery) {
                return 0.0;
            }

            @Override
            public double removeCharge(BatteryBlock battery, double request) {
                return 0.0;
            }
        };

        public double getCharge(BatteryBlock battery) {
            return battery.charge;
        }

        public double getCapacity(BatteryBlock battery) {
            return battery.getBatterySpec().getCapacity();
        }

        public double getMaxDraw(BatteryBlock battery) {
            return battery.getBatterySpec().getMaxDraw();
        }

        public double removeCharge(BatteryBlock battery, double desiredAmount) {
            return battery.oldRemoveCharge(desiredAmount);
        }
    }
}
