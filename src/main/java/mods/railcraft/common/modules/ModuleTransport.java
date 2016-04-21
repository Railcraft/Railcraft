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
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.items.ItemNugget;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.items.RailcraftToolItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.CartFilterRecipe;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("transport")
public class ModuleTransport extends RailcraftModulePayload {
    public ModuleTransport() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.machine_alpha,
                        RailcraftBlocks.machine_beta,
                        RailcraftBlocks.machine_gamma
                );
            }

            @Override
            public void preInit() {
                EnumMachineAlpha alpha = EnumMachineAlpha.TANK_WATER;
                if (alpha.isAvailable())
                    CraftingPlugin.addRecipe(alpha.getItem(6),
                            "WWW",
                            "ISI",
                            "WWW",
                            'I', "ingotIron",
                            'S', "slimeball",
                            'W', "plankWood");

                initIronTank();
                initSteelTank();

                EnumMachineBeta voidChest = EnumMachineBeta.VOID_CHEST;
                if (voidChest.isAvailable())
                    CraftingPlugin.addRecipe(voidChest.getItem(),
                            "OOO",
                            "OPO",
                            "OOO",
                            'O', new ItemStack(Blocks.obsidian),
                            'P', new ItemStack(Items.ender_pearl));

                EnumMachineGamma itemLoader = EnumMachineGamma.ITEM_LOADER;
                if (itemLoader.isAvailable()) {
                    ItemStack stack = itemLoader.getItem();
                    ItemStack detector = EnumDetector.ITEM.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.stone_pressure_plate);
                    CraftingPlugin.addRecipe(stack,
                            "SSS",
                            "SLS",
                            "SDS",
                            'S', "cobblestone",
                            'D', detector,
                            'L', new ItemStack(Blocks.hopper));

                    itemLoader = EnumMachineGamma.ITEM_LOADER_ADVANCED;
                    if (itemLoader.isAvailable())
                        CraftingPlugin.addRecipe(itemLoader.getItem(),
                                "IRI",
                                "RLR",
                                "ISI",
                                'I', "ingotSteel",
                                'R', "dustRedstone",
                                'S', RailcraftToolItems.getSteelShovel(),
                                'L', stack);
                }

                EnumMachineGamma itemUnloader = EnumMachineGamma.ITEM_UNLOADER;
                if (itemUnloader.isAvailable()) {
                    ItemStack stack = itemUnloader.getItem();
                    ItemStack detector = EnumDetector.ITEM.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.stone_pressure_plate);
                    CraftingPlugin.addRecipe(stack,
                            "SSS",
                            "SDS",
                            "SLS",
                            'S', "cobblestone",
                            'D', detector,
                            'L', new ItemStack(Blocks.hopper));

                    itemUnloader = EnumMachineGamma.ITEM_UNLOADER_ADVANCED;
                    if (itemUnloader.isAvailable())
                        CraftingPlugin.addRecipe(itemUnloader.getItem(),
                                "IRI",
                                "RLR",
                                "ISI",
                                'I', "ingotSteel",
                                'R', "dustRedstone",
                                'S', RailcraftToolItems.getSteelShovel(),
                                'L', stack);
                }

                EnumMachineGamma liquidLoader = EnumMachineGamma.FLUID_LOADER;

                if (liquidLoader.isAvailable()) {
                    ItemStack detector = EnumDetector.TANK.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.stone_pressure_plate);
                    CraftingPlugin.addRecipe(liquidLoader.getItem(),
                            "GLG",
                            "G G",
                            "GDG",
                            'D', detector,
                            'G', "blockGlassColorless",
                            'L', Blocks.hopper);
                }

                EnumMachineGamma liquidUnloader = EnumMachineGamma.FLUID_UNLOADER;
                if (liquidUnloader.isAvailable()) {
                    ItemStack detector = EnumDetector.TANK.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.stone_pressure_plate);
                    CraftingPlugin.addRecipe(liquidUnloader.getItem(),
                            "GDG",
                            "G G",
                            "GLG",
                            'D', detector,
                            'G', "blockGlassColorless",
                            'L', Blocks.hopper);
                }

//        EnumMachineDelta delta = EnumMachineDelta.CAGE;
//        if (delta.register())
//            CraftingPlugin.addShapedOreRecipe(alpha.getItem(6), 
//                "III",
//                "IWI",
//                "PPP",
//                'I', new ItemStack(Block.fenceIron),
//                'W', new ItemStack(Item.wheat),
//                'P', ItemPlate.getPlate(ItemPlate.EnumPlate.STEEL));
                EnumCart cart = EnumCart.TANK;

                if (cart.setup()) {
                    if (EnumMachineBeta.TANK_IRON_GAUGE.isAvailable()) {
                        CraftingPlugin.addRecipe(cart.getCartItem(),
                                "T",
                                "M",
                                'T', EnumMachineBeta.TANK_IRON_GAUGE.getItem(),
                                'M', Items.minecart);
                        cart.setContents(getColorTank(EnumMachineBeta.TANK_IRON_GAUGE, EnumColor.WHITE, 1));
                    } else {
                        CraftingPlugin.addRecipe(cart.getCartItem(),
                                "GGG",
                                "GMG",
                                "GGG",
                                'G', "blockGlassColorless",
                                'M', Items.minecart);
                        cart.setContents(new ItemStack(Blocks.glass, 8));
                    }
                    CraftingPlugin.addRecipe(new CartFilterRecipe());
                }

                cart = EnumCart.CARGO;

                if (cart.setup()) {
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "B",
                            "M",
                            'B', Blocks.trapped_chest,
                            'M', Items.minecart);
                    CraftingPlugin.addRecipe(new CartFilterRecipe());
                }

            }

            private void addColorRecipes(EnumMachineBeta type) {
                for (EnumColor color : EnumColor.VALUES) {
                    ItemStack output = getColorTank(type, color, 8);
                    CraftingPlugin.addRecipe(output,
                            "OOO",
                            "ODO",
                            "OOO",
                            'O', type.getItem(),
                            'D', color.getDyeOreDictTag());
                }
            }

            private ItemStack getColorTank(EnumMachineBeta type, EnumColor color, int qty) {
                ItemStack stack = type.getItem(qty);
                color.setItemColor(stack);
                return stack;
            }

            private boolean defineTank(EnumMachineBeta type, Object... recipe) {
                if (type.isAvailable()) {
                    addColorRecipes(type);
                    CraftingPlugin.addRecipe(getColorTank(type, EnumColor.WHITE, 8), recipe);
                    return true;
                }
                return false;
            }

            private boolean defineIronTank(EnumMachineBeta type, Object... recipe) {
                if (defineTank(type, recipe)) {
                    RailcraftCraftingManager.blastFurnace.addRecipe(type.getItem(), true, false, 640, RailcraftItems.nugget.getStack(4, ItemNugget.EnumNugget.STEEL));
                    return true;
                }
                return false;
            }

            private void initIronTank() {
                defineIronTank(EnumMachineBeta.TANK_IRON_WALL,
                        "PP",
                        "PP",
                        'P', RailcraftItems.plate.getRecipeObject(EnumPlate.IRON));

                defineIronTank(EnumMachineBeta.TANK_IRON_GAUGE,
                        "GPG",
                        "PGP",
                        "GPG",
                        'P', RailcraftItems.plate.getRecipeObject(EnumPlate.IRON),
                        'G', "paneGlassColorless");

                defineIronTank(EnumMachineBeta.TANK_IRON_VALVE,
                        "GPG",
                        "PLP",
                        "GPG",
                        'P', RailcraftItems.plate.getRecipeObject(EnumPlate.IRON),
                        'L', new ItemStack(Blocks.lever),
                        'G', new ItemStack(Blocks.iron_bars));
            }

            private void initSteelTank() {
                defineTank(EnumMachineBeta.TANK_STEEL_WALL,
                        "PP",
                        "PP",
                        'P', RailcraftItems.plate.getRecipeObject(EnumPlate.STEEL));

                defineTank(EnumMachineBeta.TANK_STEEL_GAUGE,
                        "GPG",
                        "PGP",
                        "GPG",
                        'P', RailcraftItems.plate.getRecipeObject(EnumPlate.STEEL),
                        'G', "paneGlassColorless");

                defineTank(EnumMachineBeta.TANK_STEEL_VALVE,
                        "GPG",
                        "PLP",
                        "GPG",
                        'P', RailcraftItems.plate.getRecipeObject(EnumPlate.STEEL),
                        'L', new ItemStack(Blocks.lever),
                        'G', new ItemStack(Blocks.iron_bars));
            }
        });
    }
}
