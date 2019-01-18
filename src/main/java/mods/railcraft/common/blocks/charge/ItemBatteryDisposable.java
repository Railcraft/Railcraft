/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

/**
 * Created by CovertJaguar on 11/9/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemBatteryDisposable extends ItemBattery<BlockBatteryDisposable> {
    public ItemBatteryDisposable(BlockBatteryDisposable block) {
        super(block);
        setHasSubtypes(true);
    }
}
