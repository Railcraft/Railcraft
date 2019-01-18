/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.util.network;

import mods.railcraft.api.core.IWorldSupplier;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IGuiReturnHandler extends IWorldSupplier {

    default void writeGuiData(RailcraftOutputStream data) throws IOException { }

    default void readGuiData(RailcraftInputStream data, EntityPlayer sender) throws IOException { }
}
