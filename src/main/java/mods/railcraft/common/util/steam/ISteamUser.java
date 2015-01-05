/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.util.steam;

import mods.railcraft.common.util.misc.ITileFilter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ISteamUser extends IFluidHandler {

    public static ITileFilter FILTER = new ITileFilter() {
        @Override
        public boolean matches(TileEntity tile) {
            return tile instanceof ISteamUser;
        }

    };
}
