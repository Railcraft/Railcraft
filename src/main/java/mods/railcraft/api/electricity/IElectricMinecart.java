/*
 * ******************************************************************************
 *  Copyright 2011-2015 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.api.electricity;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import mods.railcraft.api.tracks.RailTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

/**
 * This interface provides a simple means of using or producing Electricity
 * within a train.
 * <p>
 * The original Ic2 Battery Carts implement IEnergyTransfer. IEnergyTransfer was
 * a naive implementation of a Energy storage system for carts. I'll leave it in
 * place because of its Ic2 specific functions, but for all intents and purposes
 * this is the recommended and easier to implement interface for Electricity
 * related minecarts. In fact, the Railcraft Ic2 Energy Carts will be
 * redirecting to this interface. The Energy Loaders will continue to work
 * exclusively with IEnergyTransfer for the moment due to the high Ic2 coupling
 * of their design. An alternative loader block utilizing the IElectricMinecart
 * interface may be provided in the future, but no guarantee.
 * <p>
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public interface IElectricMinecart {

    ChargeHandler getChargeHandler();

    final class ChargeHandler {

        static final int DRAW_INTERVAL = 8;
        private static final Random rand = new Random();

        public enum Type {

            /**
             * Users draw power from tracks, sources, and storage.
             */
            USER,
            /**
             * Sources provide power to users, but not storage. This interface
             * specifies no explicit way to charge Sources, that's up to the
             * implementer. Railcraft provides no Sources currently, and may
             * never do so.
             */
            SOURCE,
            /**
             * Storage provide power to users, but can't draw from tracks or
             * sources. This interface specifies no explicit way to charge
             * Storage, that's up to the implementer. Railcraft may provide a
             * trackside block in the future for charging Storage, but does not
             * currently.
             */
            STORAGE
        }

        private final EntityMinecart minecart;
        private final Type type;
        private double capacity, charge, draw, lastTickDraw;
        private final double lossPerTick;
        private int clock = rand.nextInt();
        private int drewFromTrack;

        public ChargeHandler(EntityMinecart minecart, Type type, double capactiy) {
            this(minecart, type, capactiy, 0.0);
        }

        public ChargeHandler(EntityMinecart minecart, Type type, double capacity, double lossPerTick) {
            this.minecart = minecart;
            this.type = type;
            this.capacity = capacity;
            this.lossPerTick = lossPerTick;
        }

        public double getCharge() {
            return charge;
        }

        public double getCapacity() {
            return capacity;
        }

        public double getLosses() {
            return lossPerTick;
        }

        public double getDraw() {
            return draw;
        }

        public Type getType() {
            return type;
        }

        public void setCharge(double charge) {
            if (type == Type.USER)
                return;
            this.charge = charge;
        }

        public void addCharge(double charge) {
            if (type == Type.USER)
                return;
            this.charge += charge;
        }

        /**
         * Remove up to the requested amount of charge and returns the amount
         * removed.
         * <p>
         *
         * @return charge removed
         */
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

        /**
         * ********************************************************************
         * The following functions must be called from your EntityMinecart
         * subclass
         * ********************************************************************
         */
        /**
         * Must be called once per tick while on tracks by the owning object.
         * Server side only.
         * <p>
         * <blockquote><pre>
         * {@code
         * public void onEntityUpdate()
         *  {
         *     super.onEntityUpdate();
         *     if (!world.isRemote)
         *        chargeHandler.tick();
         *  }
         * }
         * </pre></blockquote>
         */
        public void tick() {
            clock++;
            removeLosses();

            draw = (draw * 49.0 + lastTickDraw) / 50.0;
            lastTickDraw = 0.0;

            if (drewFromTrack > 0)
                drewFromTrack--;
            else if (type == Type.USER && charge < (capacity / 2.0) && clock % DRAW_INTERVAL == 0) {
                ILinkageManager lm = CartTools.getLinkageManager(minecart.worldObj);
                for (EntityMinecart cart : lm.getCartsInTrain(minecart)) {
                    if (cart instanceof IElectricMinecart) {
                        ChargeHandler ch = ((IElectricMinecart) cart).getChargeHandler();
                        if (ch.getType() != Type.USER && ch.getCharge() > 0) {
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
         * means overriding the EnityMinecart.func_145821_a() function. You
         * don't have to call this function if you don't care about drawing from
         * tracks.
         * <p>
         * <blockquote><pre>
         * {@code
         * protected void func_145821_a(int trackX, int trackY, int trackZ, double maxSpeed, double slopeAdjustement, Block trackBlock, int trackMeta)
         *  {
         *     super.func_145821_a(trackX, trackY, trackZ, maxSpeed, slopeAdjustement, trackBlock, trackMeta);
         *     chargeHandler.tickOnTrack(trackX, trackY, trackZ);
         *  }
         * }
         * </pre></blockquote>
         */
        public void tickOnTrack(int trackX, int trackY, int trackZ) {
            if (type == Type.USER && charge < capacity && clock % DRAW_INTERVAL == 0) {
                IElectricGrid track = RailTools.getTrackObjectAt(minecart.worldObj, trackX, trackY, trackZ, IElectricGrid.class);
                if (track != null) {
                    double drawnFromTrack = track.getChargeHandler().removeCharge(capacity - charge);
                    if (drawnFromTrack > 0.0)
                        drewFromTrack = DRAW_INTERVAL * 4;
                    charge += drawnFromTrack;
                }
            }
        }

        /**
         * Must be called by the owning object's save function.
         * <p>
         * <blockquote><pre>
         * {@code
         * public void writeEntityToNBT(NBTTagCompound data)
         *  {
         *     super.writeEntityToNBT(data);
         *     chargeHandler.writeToNBT(data);
         *  }
         * }
         * </pre></blockquote>
         * <p>
         */
        public void writeToNBT(NBTTagCompound nbt) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setDouble("charge", charge);
            nbt.setTag("chargeHandler", tag);
        }

        /**
         * Must be called by the owning object's load function.
         * <p>
         * <blockquote><pre>
         * {@code
         * public void readFromNBT(NBTTagCompound data)
         *  {
         *     super.readFromNBT(data);
         *     chargeHandler.readFromNBT(data);
         *  }
         * }
         * </pre></blockquote>
         * <p>
         */
        public void readFromNBT(NBTTagCompound nbt) {
            NBTTagCompound tag = nbt.getCompoundTag("chargeHandler");
            charge = tag.getDouble("charge");
        }

    }
}
