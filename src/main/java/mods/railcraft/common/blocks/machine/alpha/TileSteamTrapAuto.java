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
import net.minecraft.util.IIcon;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileSteamTrapAuto extends TileSteamTrap {

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.STEAM_TRAP_AUTO;
    }

    @Override
    public IIcon getIcon(int side) {
        if (direction.ordinal() == side) {
            if (isJetting()) {
                return getMachineType().getTexture(9);
            }
            return getMachineType().getTexture(8);
        }
        if (side == 0 || side == 1) {
            return getMachineType().getTexture(6);
        }
        return getMachineType().getTexture(7);
    }

    @Override
    protected void triggerCheck() {
        if (!powered && canJet() && !getEntitiesInSteamArea().isEmpty()) {
            jet();
        }
    }

}
