/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import ic2.api.recipe.Recipes;
import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.misc.Mod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@RailcraftModule("railcraft:ic2")
public class ModuleIC2 extends RailcraftModulePayload {

    public static Item lapotronUpgrade;
//    private static Item creosoteWood;

    @Override
    public void checkPrerequisites() throws MissingPrerequisiteException {
        if (!Mod.IC2.isLoaded() && !Mod.IC2_CLASSIC.isLoaded())
            throw new MissingPrerequisiteException("Reason: IC2 not detected");
    }

    public ModuleIC2() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.detector
//                        RailcraftBlocks.machine_gamma
                );
            }

            @Override
            public void preInit() {
                if (RailcraftConfig.isItemEnabled("ic2.upgrade.lapotron")) {
                    lapotronUpgrade = new ItemRailcraft().setUnlocalizedName("railcraft.upgrade.lapotron").setMaxStackSize(9);

                    RailcraftRegistry.register(lapotronUpgrade);

                    RailcraftRegistry.register("ic2.upgrade.lapotron", new ItemStack(lapotronUpgrade));
                }

                RailcraftCarts.ENERGY_BATBOX.setup();
                RailcraftCarts.ENERGY_MFE.setup();
                if (Mod.IC2_CLASSIC.isLoaded()) RailcraftCarts.ENERGY_MFSU.setup();
                else RailcraftCarts.ENERGY_CESU.setup();

//        id = RailcraftConfig.getItemId("item.creosote.wood");
//        if(id > 0){
//            creosoteWood = new ItemRailcraft(id).setItemName("creosoteWood").setIconIndex(184);
//            ItemStack wood = new ItemStack(creosoteWood);
//            RailcraftLanguage.instance().registerItemName(creosoteWood, "Creosote Wood");
//
//            ItemStack oil = RailcraftPartItems.getCreosoteOil(2);
//            Ic2Recipes.addExtractorRecipe(wood, oil);
//
//            CropCard bush = new CreosoteBush(wood);
//
//            if(!CropCard.registerCrop(bush, 156)){
//                CropCard.registerCrop(bush);
//            }
//        }
            }

            @Override
            public void postInit() {
                Block blockDetector = RailcraftBlocks.detector.block();

                if (blockDetector != null) {
                    ItemStack stack = EnumDetector.ENERGY.getItem();
                    Object tin = RailcraftItems.plate.getRecipeObject(Metal.TIN);
                    if (tin == null)
                        tin = "ingotTin";
                    CraftingPlugin.addRecipe(stack, false,
                            "XXX",
                            "XPX",
                            "XXX",
                            'X', tin,
                            'P', Blocks.STONE_PRESSURE_PLATE);
                }

                ItemStack batbox = IC2Plugin.getItem("batBox");
                if (batbox != null) {
                    RailcraftCarts cart = RailcraftCarts.ENERGY_BATBOX;
                    cart.setContents(batbox);
                    ItemStack stack = cart.getCartItem();
                    if (stack != null) {
                        CraftingPlugin.addRecipe(stack,
                                "E",
                                "M",
                                'E', batbox,
                                'M', Items.MINECART
                        );
                    }
                }

                if (!Mod.IC2_CLASSIC.isLoaded()) {
                    ItemStack cesu = IC2Plugin.getItem("cesuUnit");
                    if (cesu != null) {
                        RailcraftCarts cart = RailcraftCarts.ENERGY_CESU;
                        cart.setContents(cesu);
                        ItemStack stack = cart.getCartItem();
                        if (stack != null) {
                            CraftingPlugin.addRecipe(stack,
                                    "E",
                                    "M",
                                    'E', cesu,
                                    'M', Items.MINECART
                            );
                        }
                    }
                } else {
                    ItemStack mfsu = IC2Plugin.getItem("mfsUnit");
                    if (mfsu != null) {
                        RailcraftCarts cart = RailcraftCarts.ENERGY_MFSU;
                        cart.setContents(mfsu);
                        ItemStack stack = cart.getCartItem();
                        if (stack != null) {
                            CraftingPlugin.addRecipe(stack,
                                    "E",
                                    "M",
                                    'E', mfsu,
                                    'M', Items.MINECART
                            );
                        }
                    }
                }

                ItemStack mfe = IC2Plugin.getItem("mfeUnit");
                if (mfe != null) {
                    RailcraftCarts cart = RailcraftCarts.ENERGY_MFE;
                    cart.setContents(mfe);
                    ItemStack stack = cart.getCartItem();
                    if (stack != null) {

                        CraftingPlugin.addRecipe(stack,
                                "E",
                                "M",
                                'E', mfe,
                                'M', Items.MINECART
                        );
                    }
                }

                ItemStack battery = IC2Plugin.getItem("reBattery");
                ItemStack machine = IC2Plugin.getItem("machine");

                ItemStack detector;
                if (blockDetector != null)
                    detector = EnumDetector.ENERGY.getItem();
                else
                    detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);

                if (battery != null && machine != null) {
                    if (EnumMachineGamma.ENERGY_LOADER.isAvailable())
                        Recipes.advRecipes.addRecipe(EnumMachineGamma.ENERGY_LOADER.getItem(),
                                "BLB",
                                "BIB",
                                "BDB",
                                'D', detector,
                                'B', battery,
                                'I', machine,
                                'L', new ItemStack(Blocks.HOPPER));

                    if (EnumMachineGamma.ENERGY_UNLOADER.isAvailable())
                        Recipes.advRecipes.addRecipe(EnumMachineGamma.ENERGY_UNLOADER.getItem(),
                                "BDB",
                                "BIB",
                                "BLB",
                                'D', detector,
                                'B', battery,
                                'I', machine,
                                'L', new ItemStack(Blocks.HOPPER));
                }

                if (RailcraftConfig.isItemEnabled("ic2.upgrade.lapotron")) {
                    ItemStack lapotron = IC2Plugin.getItem("lapotronCrystal");
                    ItemStack glassCable = IC2Plugin.getItem("glassFiberCableItem");
                    ItemStack circuit = IC2Plugin.getItem("advancedCircuit");

                    if (lapotron != null && glassCable != null && circuit != null) {
                        lapotron.copy();
//                lapotron.setItemDamage(-1);
                        Recipes.advRecipes.addRecipe(new ItemStack(lapotronUpgrade),
                                "GGG",

                                "wLw",
                                "GCG",
                                'G', new ItemStack(Blocks.GLASS, 1, 0),
                                'w', glassCable,
                                'C', circuit,
                                'L', lapotron);
                    }
                }
            }
        });
    }

    public static ItemStack getLapotronUpgrade() {
        if (lapotronUpgrade == null)
            return null;
        return new ItemStack(lapotronUpgrade);
    }

}
