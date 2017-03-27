/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.OreDictPlugin;
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
        Object tank;
        if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvailable())
            tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem();
        else if (EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.isAvailable())
            tank = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getItem();
        else if (EnumMachineBeta.TANK_IRON_WALL.isAvailable())
            tank = EnumMachineBeta.TANK_IRON_WALL.getItem();
        else if (Metal.STEEL.getStack(Metal.Form.PLATE) != null)
            tank = Metal.STEEL.getStack(Metal.Form.PLATE);
        else if (OreDictPlugin.oreExists("ingotSteel"))
            tank = "ingotSteel";
        else
            tank = new ItemStack(Items.IRON_INGOT);

        ItemStack firebox;
        if (EnumMachineBeta.BOILER_FIREBOX_SOLID.isAvailable())
            firebox = EnumMachineBeta.BOILER_FIREBOX_SOLID.getItem();
        else if (EnumMachineAlpha.BLAST_FURNACE.isAvailable())
            firebox = EnumMachineAlpha.BLAST_FURNACE.getItem();
        else
            firebox = new ItemStack(Blocks.FURNACE);

        CraftingPlugin.addRecipe(getStack(),
                "TTF",
                "TTF",
                "BMM",
                'T', tank,
                'F', firebox,
                'M', Items.MINECART,
                'B', new ItemStack(Blocks.IRON_BARS));
    }
}
