/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.items.ActivationBlockingItem;
import mods.railcraft.common.blocks.ItemBlockRailcraft;

/**
 * Created by CovertJaguar on 12/16/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@ActivationBlockingItem
public class ItemWire extends ItemBlockRailcraft<BlockWire> {
    public ItemWire(BlockWire block) {
        super(block);
    }
}
