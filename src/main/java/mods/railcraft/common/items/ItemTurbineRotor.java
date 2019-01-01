/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RotorRepairRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 5/19/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTurbineRotor extends ItemRailcraft {

    private static final int DAMAGE_CHANCE = 200;

    public ItemTurbineRotor() {
        setMaxDamage(30000);
        setMaxStackSize(1);
    }

    public ItemStack useRotor(ItemStack stack) {
        if (MiscTools.RANDOM.nextInt(DAMAGE_CHANCE) == 0)
            return InvTools.damageItem(stack, 1);
        return stack;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "DDD",
                'D', RailcraftItems.TURBINE_DISK);

        CraftingPlugin.addRecipe(new RotorRepairRecipe());

        // Damaged Test Rotor recipe
//        ItemStack rotor = RailcraftItems.TURBINE_ROTOR.getStack();
//        rotor.setItemDamage(25000);
//        CraftingPlugin.addShapelessRecipe(rotor, RailcraftItems.TURBINE_ROTOR.getStack());
    }
}
