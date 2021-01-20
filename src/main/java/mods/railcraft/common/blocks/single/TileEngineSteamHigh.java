/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import buildcraft.api.mj.MjAPI;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.steam.SteamConstants;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileEngineSteamHigh extends TileEngineSteam {

    private static final long OUTPUT_MJ = 8 * MjAPI.MJ;

    @Override
    public EnumGui getGui() {
        return EnumGui.ENGINE_HOBBY;
    }

    @Override
    public long getMaxOutputMJ() {
        return OUTPUT_MJ;
    }

    @Override
    public int steamUsedPerTick() {
        return 40;
    }

    @Override
    public long maxEnergy() {
        return 30000 * MjAPI.MJ;
    }

    @Override
    public long maxEnergyReceived() {
        return 1200 * MjAPI.MJ;
    }
}
