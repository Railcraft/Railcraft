/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by CovertJaguar on 7/21/2021 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class ExploderLogic extends Logic {
    private boolean primed = false;

    protected ExploderLogic(Adapter adapter) {
        super(adapter);
    }

    public void primeToExplode() {primed = true;}

    protected abstract void boom();

    @Override
    protected void updateServer() {
        super.updateServer();

        if (primed) {
            primed = false;
            boom();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("primed", primed);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        primed = data.getBoolean("primed");
    }
}
