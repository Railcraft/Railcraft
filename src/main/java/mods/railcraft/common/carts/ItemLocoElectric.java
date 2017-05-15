/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.charge.FeederVariant;
import mods.railcraft.common.items.ItemGear;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

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
    public void finalizeDefinition() {
        super.finalizeDefinition();
        //TODO: Change to battery
        Object feederUnit = RailcraftBlocks.CHARGE_FEEDER.getStack(FeederVariant.IC2);
        if (feederUnit == null) feederUnit = "blockCopper";
        ItemStack cartStack = RailcraftCarts.LOCO_ELECTRIC.getStack();
        ItemLocomotive.setItemColorData(cartStack, EnumColor.YELLOW, EnumColor.BLACK);
        CraftingPlugin.addRecipe(cartStack,
                "LT ",
                "TUT",
                "GMG",
                'L', Blocks.REDSTONE_LAMP,
                'U', feederUnit,
                'M', Items.MINECART,
                'G', RailcraftItems.GEAR, ItemGear.EnumGear.STEEL,
                'T', RailcraftItems.PLATE, Metal.STEEL);
    }
}
