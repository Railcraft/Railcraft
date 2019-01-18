/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileTankSteelWall extends TileTankIronWall {

    @Override
    public TankDefinition getTankDefinition() {
        return TankDefinition.STEEL;
    }
}
