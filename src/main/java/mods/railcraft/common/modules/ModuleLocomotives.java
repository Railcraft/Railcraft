/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
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
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("locomotives")
public class ModuleLocomotives extends RailcraftModulePayload {
    public ModuleLocomotives() {
        setEnabledEventHandler(new ModuleEventHandler() {

            @Override
            public void construction() {
                add(
                        RailcraftItems.whistleTuner,
                        RailcraftBlocks.track
                );
            }

            @Override
            public void preInit() {
                EnumTrack.WHISTLE.register();
                EnumTrack.LOCOMOTIVE.register();
                EnumTrack.LIMITER.register();

                EnumCart cart = EnumCart.LOCO_STEAM_SOLID;
                if (cart.setup()) {
                    paintLocomotive(cart.getCartItem());

                    ItemStack tank;
                    if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvailable())
                        tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem();
                    else if (EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.isAvailable())
                        tank = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getItem();
                    else if (EnumMachineBeta.TANK_IRON_WALL.isAvailable())
                        tank = EnumMachineBeta.TANK_IRON_WALL.getItem();
                    else if (RailcraftItems.ingot.getStack(ItemIngot.EnumIngot.STEEL) != null)
                        tank = RailcraftItems.ingot.getStack(ItemIngot.EnumIngot.STEEL);
                    else
                        tank = new ItemStack(Items.iron_ingot);

                    ItemStack firebox;
                    if (EnumMachineBeta.BOILER_FIREBOX_SOLID.isAvailable())
                        firebox = EnumMachineBeta.BOILER_FIREBOX_SOLID.getItem();
                    else if (EnumMachineAlpha.BLAST_FURNACE.isAvailable())
                        firebox = EnumMachineAlpha.BLAST_FURNACE.getItem();
                    else
                        firebox = new ItemStack(Blocks.furnace);

                    CraftingPlugin.addRecipe(cart.getCartItem(),
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
                    RailcraftItems.gear.register();
                    RailcraftItems.plate.register();
                }
            }

            @Override
            public void init() {
                if (EnumCart.LOCO_ELECTRIC.isEnabled()) {
                    Object feederUnit = EnumMachineEpsilon.ELECTRIC_FEEDER.isAvailable() ? EnumMachineEpsilon.ELECTRIC_FEEDER.getItem() : "blockCopper";
                    ItemStack cartStack = EnumCart.LOCO_ELECTRIC.getCartItem();
                    ItemLocomotive.setItemColorData(cartStack, EnumColor.YELLOW, EnumColor.BLACK);
                    CraftingPlugin.addRecipe(cartStack,
                            "LT ",
                            "TUT",
                            "GMG",
                            'L', Blocks.redstone_lamp,
                            'U', feederUnit,
                            'M', Items.minecart,
                            'G', RailcraftItems.gear.getRecipeObject(EnumGear.STEEL),
                            'T', RailcraftItems.plate.getRecipeObject(EnumPlate.STEEL));
                }
            }

            private void paintLocomotive(ItemStack base) {
                IRecipe recipe = new LocomotivePaintingRecipe(base);
                CraftingPlugin.addRecipe(recipe);
            }

        });
    }
}
