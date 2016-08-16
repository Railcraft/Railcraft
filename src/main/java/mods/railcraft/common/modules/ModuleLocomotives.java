/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.charge.BlockChargeFeeder;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.tracks.kits.TrackKits;
import mods.railcraft.common.carts.ItemLocomotive;
import mods.railcraft.common.carts.LocomotivePaintingRecipe;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("railcraft:locomotives")
public class ModuleLocomotives extends RailcraftModulePayload {
    public ModuleLocomotives() {
        setEnabledEventHandler(new ModuleEventHandler() {

            @Override
            public void construction() {
                add(
                        RailcraftItems.WHISTLE_TUNER,
                        TrackKits.WHISTLE,
                        TrackKits.LOCOMOTIVE,
                        TrackKits.LIMITER
//                        RailcraftBlocks.track
                );
            }

            @Override
            public void preInit() {

                RailcraftCarts cart = RailcraftCarts.LOCO_STEAM_SOLID;
                if (cart.setup()) {
                    paintLocomotive(cart.getCartItem());

                    ItemStack tank;
                    if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvailable())
                        tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem();
                    else if (EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.isAvailable())
                        tank = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE.getItem();
                    else if (EnumMachineBeta.TANK_IRON_WALL.isAvailable())
                        tank = EnumMachineBeta.TANK_IRON_WALL.getItem();
                    else if (RailcraftItems.INGOT.getStack(Metal.STEEL) != null)
                        tank = RailcraftItems.INGOT.getStack(Metal.STEEL);
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

                cart = RailcraftCarts.LOCO_ELECTRIC;
                if (cart.setup()) {
                    paintLocomotive(cart.getCartItem());
                    RailcraftItems.GEAR.register();
                    RailcraftItems.PLATE.register();
                }

                cart = RailcraftCarts.LOCO_CREATIVE;
                if (cart.setup()) {
                    paintLocomotive(cart.getCartItem());
                }
            }

            @Override
            public void init() {
                if (RailcraftCarts.LOCO_ELECTRIC.isLoaded()) {
                    Object feederUnit = RailcraftBlocks.CHARGE_FEEDER.getStack(BlockChargeFeeder.FeederVariant.IC2);
                    if (feederUnit == null) feederUnit = "blockCopper";
                    ItemStack cartStack = RailcraftCarts.LOCO_ELECTRIC.getCartItem();
                    ItemLocomotive.setItemColorData(cartStack, EnumColor.YELLOW, EnumColor.BLACK);
                    CraftingPlugin.addRecipe(cartStack,
                            "LT ",
                            "TUT",
                            "GMG",
                            'L', Blocks.REDSTONE_LAMP,
                            'U', feederUnit,
                            'M', Items.MINECART,
                            'G', RailcraftItems.GEAR, EnumGear.STEEL,
                            'T', RailcraftItems.PLATE, Metal.STEEL);
                }
            }

            private void paintLocomotive(ItemStack base) {
                IRecipe recipe = new LocomotivePaintingRecipe(base);
                CraftingPlugin.addRecipe(recipe);
            }

        });
    }
}
