/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.plugins.forge.EnergyPlugin;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Created by CovertJaguar on 1/28/2019 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class EnergyPushLogic extends Logic {
    private final int outputRate;
    private final EnumFacing[] outputFaces;

    public EnergyPushLogic(Adapter adapter, int outputRate, EnumFacing[] outputFaces) {
        super(adapter);
        this.outputRate = outputRate;
        this.outputFaces = outputFaces;
    }

    @Override
    protected void updateServer() {
        super.updateServer();
        adapter.tile().ifPresent(tile -> getLogic(IEnergyStorage.class).ifPresent(energy -> {
            EnergyPlugin.pushToTiles(tile, energy, outputRate, outputFaces);
        }));
    }
}
