/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules.orehandlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;

public class BoreOreHandler {

    @SubscribeEvent
    public void onOreEvent(OreRegisterEvent event) {
        String oreClass = event.Name;
        ItemStack ore = event.Ore;
        if (ore == null)
            return;
        if (ore.getItem() instanceof ItemBlock && (
                oreClass.startsWith("ore") 
                || oreClass.equals("stone")
                || oreClass.equals("cobblestone") 
                || oreClass.equals("logWood")
                || oreClass.equals("treeSapling")
                || oreClass.equals("treeLeaves")
                )) {
            Game.log(Level.DEBUG, "Automation Module: Ore Detected, adding to blocks Tunnel Bore can mine: {0}, id={1} meta={2}", oreClass, ore, ore.getItemDamage());
            EntityTunnelBore.addMineableBlock(InvTools.getBlockFromStack(ore), ore.getItemDamage());
        }
    }

}
