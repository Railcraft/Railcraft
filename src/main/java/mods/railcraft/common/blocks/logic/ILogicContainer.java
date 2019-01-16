/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;

import java.util.Optional;

/**
 * Created by CovertJaguar on 12/17/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ILogicContainer extends INetworkedObject<RailcraftInputStream, RailcraftOutputStream>, IGuiReturnHandler {
    <L> Optional<L> getLogic(Class<L> logicClass);
}
