/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.carts.ItemLocomotive;
import mods.railcraft.common.carts.LocomotivePaintingRecipe;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.ItemIngot;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.ItemWhistleTuner;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleLocomotives extends RailcraftModule {

    @Override
    public boolean canModuleLoad() {
        return true;
    }

    @Override
    public void initFirst() {

        MiscTools.registerTrack(EnumTrack.WHISTLE);
        MiscTools.registerTrack(EnumTrack.LOCOMOTIVE);
        MiscTools.registerTrack(EnumTrack.LIMITER);

        ItemWhistleTuner.registerItem();

        EnumCart cart = EnumCart.LOCO_STEAM_SOLID;
        if (cart.setup()) {
            paintLocomotive(cart.getCartItem());

            ItemStack tank;
            if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvaliable())
                tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem();
            else if (EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.isAvaliable())
                tank = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getItem();
            else if (EnumMachineBeta.TANK_IRON_WALL.isAvaliable())
                tank = EnumMachineBeta.TANK_IRON_WALL.getItem();
            else if (RailcraftItem.ingot.getStack(ItemIngot.EnumIngot.STEEL) != null)
                tank = RailcraftItem.ingot.getStack(ItemIngot.EnumIngot.STEEL);
            else
                tank = new ItemStack(Items.iron_ingot);

            ItemStack firebox;
            if (EnumMachineBeta.BOILER_FIREBOX_SOLID.isAvaliable())
                firebox = EnumMachineBeta.BOILER_FIREBOX_SOLID.getItem();
            else if (EnumMachineAlpha.BLAST_FURNACE.isAvaliable())
                firebox = EnumMachineAlpha.BLAST_FURNACE.getItem();
            else
                firebox = new ItemStack(Blocks.furnace);

            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "TTF",
                    "TTF",
                    "BMM",
                    'T', tank,
                    'F', firebox,
                    'M', Items.minecart,
                    'B', new ItemStack(Blocks.iron_bars));
        }

        cart = EnumCart.LOCO_ELECTRIC;
        if (cart.setup()) {
            paintLocomotive(cart.getCartItem());
            RailcraftItem.gear.registerItem();
            RailcraftItem.plate.registerItem();
        }

        cart = EnumCart.LOCO_CREATIVE;
        if (cart.setup()) {
            paintLocomotive(cart.getCartItem());
        }
    }

    @Override
    public void initSecond() {
        if (EnumCart.LOCO_ELECTRIC.isEnabled()) {
            Object feederUnit = EnumMachineEpsilon.ELECTRIC_FEEDER.isAvaliable() ? EnumMachineEpsilon.ELECTRIC_FEEDER.getItem() : "blockCopper";
            ItemStack cartStack = EnumCart.LOCO_ELECTRIC.getCartItem();
            ItemLocomotive.setItemColorData(cartStack, EnumColor.YELLOW, EnumColor.BLACK);
            CraftingPlugin.addShapedRecipe(cartStack,
                    "LT ",
                    "TUT",
                    "GMG",
                    'L', Blocks.redstone_lamp,
                    'U', feederUnit,
                    'M', Items.minecart,
                    'G', RailcraftItem.gear.getRecipeObject(EnumGear.STEEL),
                    'T', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL));
        }
    }

    private void paintLocomotive(ItemStack base) {
        IRecipe recipe = new LocomotivePaintingRecipe(base);
        CraftingPlugin.addRecipe(recipe);
    }

}
