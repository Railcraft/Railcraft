/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;

/**
 * Created by CovertJaguar on 10/4/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ICartBattery extends IChargeBattery {
    enum Type {

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

    Type getType();

    void setCharge(double charge);

    void addCharge(double charge);

    double removeCharge(double request);

    double getLosses();

    double getDraw();

    void tick(EntityMinecart owner);

    void tickOnTrack(EntityMinecart owner, BlockPos pos);
}
