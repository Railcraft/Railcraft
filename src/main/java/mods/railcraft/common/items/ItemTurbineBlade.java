/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.api.crafting.Crafters;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 5/19/2016 for Railcraft.
 * e
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTurbineBlade extends ItemRailcraft {
    @Override
    public void defineRecipes() {
        Crafters.rollingMachine().newRecipe(new ItemStack(this)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "plateNickel");
        Crafters.rollingMachine().newRecipe(new ItemStack(this)).shaped(
                "  I",
                " I ",
                "I  ",
                'I', "plateSteel");
    }
}
