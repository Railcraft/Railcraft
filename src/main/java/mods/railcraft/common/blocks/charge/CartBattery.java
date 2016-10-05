/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.ILinkageManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

/**
 * This interface provides a simple means of using or producing Electricity
 * within a train.
 * <p/>
 * The original Ic2 Battery Carts implement IEnergyTransfer. IEnergyTransfer was
 * a naive implementation of a Energy storage system for carts. I'll leave it in
 * place because of its Ic2 specific functions, but for all intents and purposes
 * this is the recommended and easier to implement interface for Electricity
 * related minecarts. In fact, the Railcraft Ic2 Energy Carts will be
 * redirecting to this interface. The Energy Loaders will continue to work
 * exclusively with IEnergyTransfer for the moment due to the high Ic2 coupling
 * of their design. An alternative loader block utilizing the CartBattery
 * interface may be provided in the future, but no guarantee.
 * <p/>
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class CartBattery implements ICartBattery {

    static final int DRAW_INTERVAL = 8;
    private static final Random rand = new Random();

    private final Type type;
    private final double capacity;
    private final double lossPerTick;
    private double charge;
    private double draw;
    private double lastTickDraw;
    private int clock = rand.nextInt();
    private int drewFromTrack;

    public CartBattery() {
        this(Type.USER, 5000.0, 0.0);
    }

    public CartBattery(Type type, double capacity) {
        this(type, capacity, 0.0);
    }

    public CartBattery(Type type, double capacity, double lossPerTick) {
        this.type = type;
        this.capacity = capacity;
        this.lossPerTick = lossPerTick;
    }

    @Override
    public double getCharge() {
        return charge;
    }

    @Override
    public void setCharge(double charge) {
        if (type == Type.USER)
            return;
        this.charge = charge;
    }

    @Override
    public double getCapacity() {
        return capacity;
    }

    @Override
    public double getLosses() {
        return lossPerTick;
    }

    @Override
    public double getDraw() {
        return draw;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void addCharge(double charge) {
        if (type == Type.USER)
            return;
        this.charge += charge;
    }

    /**
     * Remove up to the requested amount of charge and returns the amount
     * removed.
     * <p/>
     *
     * @return charge removed
     */
    @Override
    public double removeCharge(double request) {
        if (request <= 0.0)
            return 0.0;
        if (charge >= request) {
            charge -= request;
            lastTickDraw += request;
            return request;
        }
        double ret = charge;
        charge = 0.0;
        lastTickDraw += ret;
        return ret;
    }

    private void removeLosses() {
        if (lossPerTick > 0.0)
            if (charge >= lossPerTick)
                charge -= lossPerTick;
            else
                charge = 0.0;
    }

    /*
     * ********************************************************************
     * The following functions must be called from your EntityMinecart
     * subclass
     * ********************************************************************
     */

    /**
     * Must be called once per tick while on tracks by the owning object.
     * Server side only.
     * <p/>
     * <blockquote><pre>
     * {@code
     * public void onEntityUpdate()
     *  {
     *     super.onEntityUpdate();
     *     if (!world.isRemote)
     *        cartBattery.tick(this);
     *  }
     * }
     * </pre></blockquote>
     */
    @Override
    public void tick(EntityMinecart owner) {
        clock++;
        removeLosses();

        draw = (draw * 24.0 + lastTickDraw) / 25.0;
        lastTickDraw = 0.0;

        if (drewFromTrack > 0)
            drewFromTrack--;
        else if (type == Type.USER && charge < (capacity / 2.0) && clock % DRAW_INTERVAL == 0) {
            ILinkageManager lm = CartToolsAPI.getLinkageManager(owner.worldObj);
            for (EntityMinecart cart : lm.trainIterator(owner)) {
                if (cart.hasCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null)) {
                    ICartBattery ch = cart.getCapability(CapabilityCartBattery.CHARGE_CART_CAPABILITY, null);
                    if (ch != null && ch.getType() != Type.USER && ch.getCharge() > 0) {
                        charge += ch.removeCharge(capacity - charge);
                        break;
                    }
                }
            }
        }
    }

    /**
     * If you want to be able to draw power from the track, this function
     * needs to be called once per tick. Server side only. Generally this
     * means overriding the EntityMinecart.func_145821_a() function. You
     * don't have to call this function if you don't care about drawing from
     * tracks.
     * <p/>
     * <blockquote><pre>
     * {@code
     * protected void func_145821_a(int trackX, int trackY, int trackZ, double maxSpeed, double slopeAdjustment, Block trackBlock, int trackMeta)
     *  {
     *     super.func_145821_a(trackPos, maxSpeed, slopeAdjustment, trackBlock, trackMeta);
     *     cartBattery.tickOnTrack(this, trackPos);
     *  }
     * }
     * </pre></blockquote>
     */
    @Override
    public void tickOnTrack(EntityMinecart owner, BlockPos pos) {
        if (type == Type.USER && charge < capacity && clock % DRAW_INTERVAL == 0) {
            double drawnFromTrack = ChargeManager.getNetwork(owner.worldObj).getNode(pos).removeCharge(capacity - charge);
            if (drawnFromTrack > 0.0)
                drewFromTrack = DRAW_INTERVAL * 4;
            charge += drawnFromTrack;
        }
    }
}
