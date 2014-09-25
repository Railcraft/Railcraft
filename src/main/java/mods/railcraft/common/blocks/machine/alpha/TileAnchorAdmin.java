/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.IEnumMachine;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileAnchorAdmin extends TileAnchorWorld {

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.ADMIN_ANCHOR;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

}
