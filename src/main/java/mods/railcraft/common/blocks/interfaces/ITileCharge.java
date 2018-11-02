/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.interfaces;

import mods.railcraft.common.blocks.charge.Charge;
import mods.railcraft.common.blocks.charge.IChargeBlock;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 6/14/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ITileCharge {
    default @Nullable IChargeBlock.ChargeDef getChargeDef(Charge network) {
        return null;
    }
}
