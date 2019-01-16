/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.client.render.carts.LocomotiveRenderType;
import mods.railcraft.common.blocks.charge.BlockBattery;
import mods.railcraft.common.items.ItemCharge;
import mods.railcraft.common.items.ItemGear;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemLocoElectric extends ItemLocomotive {
    public ItemLocoElectric(IRailcraftCartContainer cart) {
        super(cart, LocomotiveRenderType.ELECTRIC, EnumColor.YELLOW, EnumColor.BLACK);
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        CraftingPlugin.addShapedRecipe(getStack(),
                "LT ",
                "DBD",
                "GMG",
                'L', Blocks.REDSTONE_LAMP,
                'D', RailcraftItems.CHARGE, ItemCharge.EnumCharge.MOTOR,
                'B', BlockBattery.RECHARGEABLE_BATTERY_ORE_TAG,
                'M', Items.MINECART,
                'G', RailcraftItems.GEAR, ItemGear.EnumGear.STEEL,
                'T', RailcraftItems.PLATE, Metal.STEEL);
    }
}
