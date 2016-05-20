/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.items.*;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.CartFilterRecipe;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleTransport extends RailcraftModule {

    @Override
    public void initFirst() {
        ItemNotepad.registerItem();
        if (ItemNotepad.item != null) {
            ItemStack magGlass = ItemMagnifyingGlass.getItem();
            CraftingPlugin.addShapedRecipe(new ItemStack(ItemNotepad.item),
                    "IF",
                    "XP",
                    'I', new ItemStack(Items.dye, 1, 0),
                    'F', Items.feather,
                    'X', magGlass,
                    'P', Items.paper);
        }

        EnumMachineAlpha alpha = EnumMachineAlpha.TANK_WATER;
        if (alpha.register())
            CraftingPlugin.addShapedRecipe(alpha.getItem(6),
                    "WWW",
                    "ISI",
                    "WWW",
                    'I', "ingotIron",
                    'S', "slimeball",
                    'W', "plankWood");

        initIronTank();
        initSteelTank();

        EnumMachineBeta voidChest = EnumMachineBeta.VOID_CHEST;
        if (voidChest.register())
            CraftingPlugin.addShapedRecipe(voidChest.getItem(),
                    "OOO",
                    "OPO",
                    "OOO",
                    'O', new ItemStack(Blocks.obsidian),
                    'P', new ItemStack(Items.ender_pearl));

        EnumMachineGamma itemLoader = EnumMachineGamma.ITEM_LOADER;
        if (itemLoader.register()) {
            ItemStack stack = itemLoader.getItem();
            ItemStack detector = EnumDetector.ITEM.getItem();
            if (detector == null)
                detector = new ItemStack(Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(stack,
                    "SSS",
                    "SLS",
                    "SDS",
                    'S', "cobblestone",
                    'D', detector,
                    'L', new ItemStack(Blocks.hopper));

            itemLoader = EnumMachineGamma.ITEM_LOADER_ADVANCED;
            if (itemLoader.register())
                CraftingPlugin.addShapedRecipe(itemLoader.getItem(),
                        "IRI",
                        "RLR",
                        "ISI",
                        'I', "ingotSteel",
                        'R', "dustRedstone",
                        'S', RailcraftToolItems.getSteelShovel(),
                        'L', stack);
        }

        EnumMachineGamma itemUnloader = EnumMachineGamma.ITEM_UNLOADER;
        if (itemUnloader.register()) {
            ItemStack stack = itemUnloader.getItem();
            ItemStack detector = EnumDetector.ITEM.getItem();
            if (detector == null)
                detector = new ItemStack(Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(stack,
                    "SSS",
                    "SDS",
                    "SLS",
                    'S', "cobblestone",
                    'D', detector,
                    'L', new ItemStack(Blocks.hopper));

            itemUnloader = EnumMachineGamma.ITEM_UNLOADER_ADVANCED;
            if (itemUnloader.register())
                CraftingPlugin.addShapedRecipe(itemUnloader.getItem(),
                        "IRI",
                        "RLR",
                        "ISI",
                        'I', "ingotSteel",
                        'R', "dustRedstone",
                        'S', RailcraftToolItems.getSteelShovel(),
                        'L', stack);
        }

        EnumMachineGamma liquidLoader = EnumMachineGamma.FLUID_LOADER;

        if (liquidLoader.register()) {
            ItemStack detector = EnumDetector.TANK.getItem();
            if (detector == null)
                detector = new ItemStack(Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(liquidLoader.getItem(),
                    "GLG",
                    "G G",
                    "GDG",
                    'D', detector,
                    'G', "blockGlassColorless",
                    'L', Blocks.hopper);
        }

        EnumMachineGamma liquidUnloader = EnumMachineGamma.FLUID_UNLOADER;
        if (liquidUnloader.register()) {
            ItemStack detector = EnumDetector.TANK.getItem();
            if (detector == null)
                detector = new ItemStack(Blocks.stone_pressure_plate);
            CraftingPlugin.addShapedRecipe(liquidUnloader.getItem(),
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
            if (EnumMachineBeta.TANK_IRON_GAUGE.isAvaliable()) {
                CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                        "T",
                        "M",
                        'T', EnumMachineBeta.TANK_IRON_GAUGE.getItem(),
                        'M', Items.minecart);
                cart.setContents(getColorTank(EnumMachineBeta.TANK_IRON_GAUGE, EnumColor.WHITE, 1));
            } else {
                CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                        "GGG",
                        "GMG",
                        "GGG",
                        'G', "blockGlassColorless",
                        'M', Items.minecart);
                cart.setContents(new ItemStack(Blocks.glass, 8));
            }
            CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), cart.getCartItem());
            CraftingPlugin.addRecipe(new CartFilterRecipe());
        }

        cart = EnumCart.CARGO;

        if (cart.setup()) {
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "B",
                    "M",
                    'B', Blocks.trapped_chest,
                    'M', Items.minecart);
            CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), cart.getCartItem());
            CraftingPlugin.addRecipe(new CartFilterRecipe());
        }

    }

    private void addColorRecipes(EnumMachineBeta type) {
        for (EnumColor color : EnumColor.VALUES) {
            ItemStack output = getColorTank(type, color, 8);
            CraftingPlugin.addShapedRecipe(output,
                    "OOO",
                    "ODO",
                    "OOO",
                    'O', type.getItem(),
                    'D', color.getDye());
        }
    }

    private ItemStack getColorTank(EnumMachineBeta type, EnumColor color, int qty) {
        ItemStack stack = type.getItem(qty);
        return InvTools.setItemColor(stack, color);
    }

    private boolean defineTank(EnumMachineBeta type, Object... recipe) {
        if (type.register()) {
            addColorRecipes(type);
            CraftingPlugin.addShapedRecipe(getColorTank(type, EnumColor.WHITE, 8), recipe);
            return true;
        }
        return false;
    }

    private boolean defineIronTank(EnumMachineBeta type, Object... recipe) {
        if (defineTank(type, recipe)) {
            RailcraftCraftingManager.blastFurnace.addRecipe(type.getItem(), true, false, 640, RailcraftItem.nugget.getStack(4, ItemNugget.EnumNugget.STEEL));
            return true;
        }
        return false;
    }

    private void initIronTank() {
        defineIronTank(EnumMachineBeta.TANK_IRON_WALL,
                "PP",
                "PP",
                'P', RailcraftItem.plate.getRecipeObject(EnumPlate.IRON));

        defineIronTank(EnumMachineBeta.TANK_IRON_GAUGE,
                "GPG",
                "PGP",
                "GPG",
                'P', RailcraftItem.plate.getRecipeObject(EnumPlate.IRON),
                'G', "paneGlassColorless");

        defineIronTank(EnumMachineBeta.TANK_IRON_VALVE,
                "GPG",
                "PLP",
                "GPG",
                'P', RailcraftItem.plate.getRecipeObject(EnumPlate.IRON),
                'L', new ItemStack(Blocks.lever),
                'G', new ItemStack(Blocks.iron_bars));
    }

    private void initSteelTank() {
        defineTank(EnumMachineBeta.TANK_STEEL_WALL,
                "PP",
                "PP",
                'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL));

        defineTank(EnumMachineBeta.TANK_STEEL_GAUGE,
                "GPG",
                "PGP",
                "GPG",
                'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL),
                'G', "paneGlassColorless");

        defineTank(EnumMachineBeta.TANK_STEEL_VALVE,
                "GPG",
                "PLP",
                "GPG",
                'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL),
                'L', new ItemStack(Blocks.lever),
                'G', new ItemStack(Blocks.iron_bars));
    }

}
