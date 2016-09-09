/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.blocks.machine.BlockMachine;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockMachineManipulator extends BlockMachine<ManipulatorVariant> {
    public BlockMachineManipulator() {
        super(ManipulatorVariant.PROXY, false);
    }
}
