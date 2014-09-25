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
import mods.railcraft.common.gui.EnumGui;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static mods.railcraft.common.plugins.forge.PowerPlugin.FULL_POWER;
import static mods.railcraft.common.plugins.forge.PowerPlugin.NO_POWER;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class DetectorSheep extends DetectorFilter {

    public DetectorSheep() {
        super(1);
    }

    @Override
    public int testCarts(List<EntityMinecart> carts) {
        for (EntityMinecart cart : carts) {
            if (cart.riddenByEntity instanceof EntitySheep) {
                EntitySheep sheep = (EntitySheep) cart.riddenByEntity;
                ItemStack wool = getFilters().getStackInSlot(0);
                if (!sheep.isChild() && !sheep.getSheared() && (wool == null || sheep.getFleeceColor() == wool.getItemDamage())) {
                    return FULL_POWER;
                }
            }
        }
        return NO_POWER;
    }

    @Override
    public boolean blockActivated(EntityPlayer player) {
        openGui(EnumGui.DETECTOR_SHEEP, player);
        return true;
    }

    @Override
    public EnumDetector getType() {
        return EnumDetector.SHEEP;
    }

}
