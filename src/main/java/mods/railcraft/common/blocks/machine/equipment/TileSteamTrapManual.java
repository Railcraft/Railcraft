/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.equipment;

import mods.railcraft.common.gui.EnumGui;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileSteamTrapManual extends TileSteamTrap {

    @Override
    public @Nullable EnumGui getGui() {
        return null;
    }

    @Override
    protected void triggerCheck() {
        if (powered) {
            jet();
        }
    }
}
