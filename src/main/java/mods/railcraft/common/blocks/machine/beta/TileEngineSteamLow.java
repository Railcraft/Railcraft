/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.util.steam.Steam;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileEngineSteamLow extends TileEngineSteam
{

    private static final int OUTPUT_MJ = 4;

    public TileEngineSteamLow() {
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.ENGINE_STEAM_LOW;
    }

    @Override
    public float getMaxOutputMJ() {
        return OUTPUT_MJ;
    }

    @Override
    public int steamUsedPerTick() {
        return Steam.STEAM_PER_MJ * OUTPUT_MJ;
    }

    @Override
    public int maxEnergy() {
        return 20000;
    }

    @Override
    public int maxEnergyReceived() {
        return 600;
    }

    @Override
    public int maxEnergyExtracted() {
        return 80;
    }
}
