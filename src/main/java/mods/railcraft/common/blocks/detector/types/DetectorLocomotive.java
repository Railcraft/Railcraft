/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.detector.types;

import java.util.List;
import mods.railcraft.common.blocks.detector.DetectorFilter;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.gui.EnumGui;

import static mods.railcraft.common.plugins.forge.PowerPlugin.*;

import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorLocomotive extends DetectorFilter {

    public DetectorLocomotive() {
        super(2);
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart instanceof EntityLocomotive) {
                EntityLocomotive loco = (EntityLocomotive) cart;
                ItemStack primary = getFilters().getStackInSlot(0);
                EnumColor pColor = InvTools.getItemColor(primary);
                if (pColor != null && pColor.ordinal() != loco.getPrimaryColor()) {
                    continue;
                }
                ItemStack secondary = getFilters().getStackInSlot(1);
                EnumColor sColor = InvTools.getItemColor(secondary);
                if (sColor != null && sColor.ordinal() != loco.getSecondaryColor()) {
                    continue;
                }
                return FULL_POWER;
            }
        }
        return NO_POWER;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_LOCOMOTIVE, player);
        return true;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.LOCOMOTIVE;
    }

}
