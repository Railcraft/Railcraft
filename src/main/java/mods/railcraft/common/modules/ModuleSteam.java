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
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.items.ItemGear.EnumGear;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("railcraft:steam")
public class ModuleSteam extends RailcraftModulePayload {

    public ModuleSteam() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
//                        RailcraftBlocks.machine_beta
                );
            }

            @Override
            public void preInit() {
//              LiquidItems.getSteamBottle(1);
                EnumMachineBeta beta = EnumMachineBeta.ENGINE_STEAM_HOBBY;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "NNN",
                            " C ",
                            "GPG",
                            'P', new ItemStack(Blocks.PISTON),
                            'N', "nuggetGold",
                            'C', "blockGlassColorless",
                            'G', RailcraftItems.gear.getRecipeObject(EnumGear.GOLD_PLATE));
                }

                beta = EnumMachineBeta.ENGINE_STEAM_LOW;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "III",
                            " C ",
                            "GPG",
                            'P', new ItemStack(Blocks.PISTON),
                            'I', RailcraftItems.plate.getRecipeObject(Metal.IRON),
                            'C', "blockGlassColorless",
                            'G', "gearIron");

                    RailcraftCraftingManager.blastFurnace.addRecipe(beta.getItem(), true, false, 15360, RailcraftItems.ingot.getStack(12, Metal.STEEL));
                }

                beta = EnumMachineBeta.ENGINE_STEAM_HIGH;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "III",
                            " C ",
                            "GPG",
                            'P', new ItemStack(Blocks.PISTON),
                            'I', RailcraftItems.plate.getRecipeObject(Metal.STEEL),
                            'C', "blockGlassColorless",
                            'G', RailcraftItems.gear.getRecipeObject(EnumGear.STEEL));
                }

                beta = EnumMachineBeta.BOILER_FIREBOX_SOLID;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "BBB",
                            "BCB",
                            "BFB",
                            'B', "ingotBrick",
                            'C', new ItemStack(Items.FIRE_CHARGE),
                            'F', new ItemStack(Blocks.FURNACE));
                }

                beta = EnumMachineBeta.BOILER_FIREBOX_FLUID;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "PBP",
                            "GCG",
                            "PFP",
                            'B', new ItemStack(Items.BUCKET),
                            'G', new ItemStack(Blocks.IRON_BARS),
                            'C', new ItemStack(Items.FIRE_CHARGE),
                            'P', RailcraftItems.plate.getRecipeObject(Metal.STEEL),
                            'F', new ItemStack(Blocks.FURNACE));
                }

                beta = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "P",
                            "P",
                            'P', RailcraftItems.plate.getRecipeObject(Metal.IRON));

                    RailcraftCraftingManager.blastFurnace.addRecipe(beta.getItem(), true, false, 2560, RailcraftItems.ingot.getStack(2, Metal.STEEL));
                }

                beta = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE;
                if (beta.isAvailable()) {
                    CraftingPlugin.addRecipe(beta.getItem(),
                            "P",
                            "P",
                            'P', RailcraftItems.plate.getRecipeObject(Metal.STEEL));
                }
            }

            @Override
            public void init() {
                EnumMachineAlpha alpha = EnumMachineAlpha.STEAM_TRAP_MANUAL;
                if (alpha.isAvailable()) {
                    CraftingPlugin.addRecipe(alpha.getItem(),
                            " G ",
                            " T ",
                            " D ",
                            'G', new ItemStack(Blocks.IRON_BARS),
                            'T', getTankItem(),
                            'D', new ItemStack(Blocks.DISPENSER));
                }

                alpha = EnumMachineAlpha.STEAM_TRAP_AUTO;
                if (alpha.isAvailable()) {
                    CraftingPlugin.addRecipe(alpha.getItem(),
                            " G ",
                            "RTR",
                            " D ",
                            'G', new ItemStack(Blocks.IRON_BARS),
                            'T', getTankItem(),
                            'R', "dustRedstone",
                            'D', new ItemStack(Blocks.DISPENSER));
                    if (EnumMachineAlpha.STEAM_TRAP_MANUAL.isAvailable()) {
                        CraftingPlugin.addRecipe(alpha.getItem(),
                                "RTR",
                                'T', EnumMachineAlpha.STEAM_TRAP_MANUAL.getItem(),
                                'R', "dustRedstone");
                        CraftingPlugin.addShapelessRecipe(EnumMachineAlpha.STEAM_TRAP_MANUAL.getItem(), alpha.getItem());
                    }
                }
            }

            private ItemStack getTankItem() {
                ItemStack tank;
                if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvailable())
                    tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getItem();
                else if (EnumMachineBeta.TANK_STEEL_WALL.isAvailable())
                    tank = EnumMachineBeta.TANK_STEEL_WALL.getItem();
                else
                    tank = RailcraftItems.plate.getStack(1, Metal.STEEL);
                if (tank == null)
                    tank = RailcraftItems.ingot.getStack(1, Metal.STEEL);
                if (tank == null)
                    tank = new ItemStack(Blocks.IRON_BLOCK);
                return tank;
            }

            @Override
            public void postInit() {
                IC2Plugin.nerfSyntheticCoal();
            }
        });
    }
}
