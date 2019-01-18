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
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 8/30/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemLocoSteamSolid extends ItemLocomotive {
    public ItemLocoSteamSolid(IRailcraftCartContainer cart) {
        super(cart, LocomotiveRenderType.STEAM_SOLID, EnumColor.SILVER, EnumColor.GRAY);
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        ItemStack tank;
        //TODO fix, its all wrong
//        if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvailable())
//            tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getStack();
//        else if (EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.isAvailable())
//            tank = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getStack();
            tank = RailcraftBlocks.TANK_IRON_WALL.getStack(1);
         if (InvTools.isEmpty(tank))
            tank = RailcraftItems.PLATE.getStack(1, Metal.STEEL);
         if (InvTools.isEmpty(tank))
            tank = RailcraftItems.INGOT.getStack(1, Metal.STEEL);
         if (InvTools.isEmpty(tank))
            tank = new ItemStack(Items.IRON_INGOT);

        ItemStack firebox;
//        if (EnumMachineBeta.BOILER_FIREBOX_SOLID.isAvailable())
//            firebox = EnumMachineBeta.BOILER_FIREBOX_SOLID.getStack();
            firebox = RailcraftBlocks.BLAST_FURNACE.getStack(1);
        if (InvTools.isEmpty(firebox))
            firebox = new ItemStack(Blocks.FURNACE);

        CraftingPlugin.addShapedRecipe(getStack(),
                "TTF",
                "TTF",
                "BMM",
                'T', tank,
                'F', firebox,
                'M', Items.MINECART,
                'B', new ItemStack(Blocks.IRON_BARS));
    }
}
