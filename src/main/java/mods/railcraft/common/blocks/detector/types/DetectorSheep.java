/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector.types;

import mods.railcraft.common.blocks.detector.DetectorFilter;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

import java.util.List;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorSheep extends DetectorFilter {

    public DetectorSheep() {
        super(1);
    }

    @Override
    //TODO: test
    public int testCarts(List<EntityMinecart> carts) {
        ItemStack wool = getFilters().getStackInSlot(0);
        for (EntityMinecart cart : carts) {
            if (cart.getPassengers().stream().filter(e -> e instanceof EntitySheep).map(entity -> (EntitySheep) entity)
                    .anyMatch(sheep -> !sheep.isChild() && !sheep.getSheared() && (InvTools.isEmpty(wool)|| sheep.getFleeceColor() == EnumDyeColor.byDyeDamage(wool.getItemDamage()))))
                return FULL_POWER;
        }
        return NO_POWER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_SHEEP, player);
        return true;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.SHEEP;
    }
}
