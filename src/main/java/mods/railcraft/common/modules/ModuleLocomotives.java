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
import mods.railcraft.common.items.Metal;
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
                    else if (RailcraftItems.ingot.getStack(Metal.STEEL) != null)
                        tank = RailcraftItems.ingot.getStack(Metal.STEEL);
                    else
                        tank = new ItemStack(Items.IRON_INGOT);

                    ItemStack firebox;
                    if (EnumMachineBeta.BOILER_FIREBOX_SOLID.isAvailable())
                        firebox = EnumMachineBeta.BOILER_FIREBOX_SOLID.getItem();
                    else if (EnumMachineAlpha.BLAST_FURNACE.isAvailable())
                        firebox = EnumMachineAlpha.BLAST_FURNACE.getItem();
                    else
                        firebox = new ItemStack(Blocks.FURNACE);

                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "TTF",
                            "TTF",
                            "BMM",
                            'T', tank,
                            'F', firebox,
                            'M', Items.MINECART,
                            'B', new ItemStack(Blocks.IRON_BARS));
                }

                cart = EnumCart.LOCO_ELECTRIC;
                if (cart.setup()) {
                    paintLocomotive(cart.getCartItem());
                    RailcraftItems.gear.register();
                    RailcraftItems.plate.register();
                }

                cart = EnumCart.LOCO_CREATIVE;
                if (cart.setup()) {
                    paintLocomotive(cart.getCartItem());
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
                            'L', Blocks.REDSTONE_LAMP,
                            'U', feederUnit,
                            'M', Items.MINECART,
                            'G', RailcraftItems.gear.getRecipeObject(EnumGear.STEEL),
                            'T', RailcraftItems.plate.getRecipeObject(Metal.STEEL));
                }
            }

            private void paintLocomotive(ItemStack base) {
                IRecipe recipe = new LocomotivePaintingRecipe(base);
                CraftingPlugin.addRecipe(recipe);
            }

        });
    }
}
