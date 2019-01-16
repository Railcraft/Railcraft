/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forestry;

import forestry.api.storage.BackpackResupplyEvent;
import forestry.api.storage.BackpackStowEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BackpackEventHandler {

    @SubscribeEvent
    public void stow(BackpackStowEvent event) {
        if (event.backpackDefinition instanceof BaseBackpack)
            event.setCanceled(((BaseBackpack) event.backpackDefinition).stow(event.backpackInventory, event.stackToStow));
    }

    @SubscribeEvent
    public void stow(BackpackResupplyEvent event) {
        if (event.backpackDefinition instanceof BaseBackpack)
            event.setCanceled(((BaseBackpack) event.backpackDefinition).resupply(event.backpackInventory));
    }

}
