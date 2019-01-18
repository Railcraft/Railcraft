/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:steam", description = "engines, boilers, steam traps")
public class ModuleSteam extends RailcraftModulePayload {

    public ModuleSteam() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.EQUIPMENT,
                        RailcraftBlocks.ADMIN_STEAM_PRODUCER,
                        RailcraftBlocks.BOILER_FIREBOX_FLUID,
                        RailcraftBlocks.BOILER_FIREBOX_SOLID,
                        RailcraftBlocks.BOILER_TANK_PRESSURE_HIGH,
                        RailcraftBlocks.BOILER_TANK_PRESSURE_LOW
                );
            }

            @Override
            public void preInit() {
//              LiquidItems.getSteamBottle(1);
//                EnumMachineBeta beta = EnumMachineBeta.ENGINE_STEAM_HOBBY;
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "NNN",
//                            " C ",
//                            "GPG",
//                            'P', new ItemStack(Blocks.PISTON),
//                            'N', RailcraftItems.PLATE.getRecipeObject(Metal.BRASS),
//                            'C', "blockGlassColorless",
//                            'G', "gearBrass");
//                }

//                beta = EnumMachineBeta.ENGINE_STEAM_LOW;
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "III",
//                            " C ",
//                            "GPG",
//                            'P', new ItemStack(Blocks.PISTON),
//                            'I', RailcraftItems.PLATE.getRecipeObject(Metal.IRON),
//                            'C', "blockGlassColorless",
//                            'G', "gearIron");

//                    Crafters.blastFurnace.addRecipe(beta.getStack(), true, false, 15360, RailcraftItems.INGOT.getStack(12, Metal.STEEL));
//                }

//                beta = EnumMachineBeta.ENGINE_STEAM_HIGH;
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "III",
//                            " C ",
//                            "GPG",
//                            'P', new ItemStack(Blocks.PISTON),
//                            'I', RailcraftItems.PLATE, Metal.STEEL,
//                            'C', "blockGlassColorless",
//                            'G', "gearSteel");
//                }

//                beta = EnumMachineBeta.BOILER_FIREBOX_SOLID; TODO
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "BBB",
//                            "BCB",
//                            "BFB",
//                            'B', "ingotBrick",
//                            'C', new ItemStack(Items.FIRE_CHARGE),
//                            'F', new ItemStack(Blocks.FURNACE));
//                }

//                beta = EnumMachineBeta.BOILER_FIREBOX_FLUID;
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "PBP",
//                            "GCG",
//                            "PFP",
//                            'B', new ItemStack(Items.BUCKET),
//                            'G', new ItemStack(Blocks.IRON_BARS),
//                            'C', new ItemStack(Items.FIRE_CHARGE),
//                            'P', RailcraftItems.PLATE, Metal.STEEL,
//                            'F', new ItemStack(Blocks.FURNACE));
//                }

//                beta = EnumMachineBeta.BOILER_TANK_LOW_PRESSURE;
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "P",
//                            "P",
//                            'P', RailcraftItems.PLATE, Metal.IRON);

//                    Crafters.blastFurnace.addRecipe(beta.getStack(), true, false, 2560, RailcraftItems.INGOT.getStack(2, Metal.STEEL));
//                }

//                beta = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE;
//                if (beta.isAvailable()) {
//                    CraftingPlugin.addRecipe(beta.getStack(),
//                            "P",
//                            "P",
//                            'P', RailcraftItems.PLATE, Metal.STEEL);
//                }
            }

            @Override
            public void init() {
//                EnumMachineAlpha alpha = EnumMachineAlpha.STEAM_TRAP_MANUAL;
//                if (alpha.isAvailable()) {
//                    CraftingPlugin.addRecipe(alpha.getStack(),
//                            " G ",
//                            " T ",
//                            " D ",
//                            'G', new ItemStack(Blocks.IRON_BARS),
//                            'T', getTankItem(),
//                            'D', new ItemStack(Blocks.DISPENSER));
//                }
//
//                alpha = EnumMachineAlpha.STEAM_TRAP_AUTO;
//                if (alpha.isAvailable()) {
//                    CraftingPlugin.addRecipe(alpha.getStack(),
//                            " G ",
//                            "RTR",
//                            " D ",
//                            'G', new ItemStack(Blocks.IRON_BARS),
//                            'T', getTankItem(),
//                            'R', "dustRedstone",
//                            'D', new ItemStack(Blocks.DISPENSER));
//                    if (EnumMachineAlpha.STEAM_TRAP_MANUAL.isAvailable()) {
//                        CraftingPlugin.addRecipe(alpha.getStack(),
//                                "RTR",
//                                'T', EnumMachineAlpha.STEAM_TRAP_MANUAL.getStack(),
//                                'R', "dustRedstone");
//                        CraftingPlugin.addShapelessRecipe(EnumMachineAlpha.STEAM_TRAP_MANUAL.getStack(), alpha.getStack());
//                    }
//                }
            }

            private ItemStack getTankItem() {
                ItemStack tank; //TODO
//                if (EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.isAvailable())
//                    tank = EnumMachineBeta.BOILER_TANK_HIGH_PRESSURE.getStack();
                tank = RailcraftBlocks.TANK_STEEL_WALL.getStack(1);
                if (InvTools.isEmpty(tank))
                    tank = RailcraftItems.PLATE.getStack(1, Metal.STEEL);
                if (InvTools.isEmpty(tank))
                    tank = RailcraftItems.INGOT.getStack(1, Metal.STEEL);
                if (InvTools.isEmpty(tank))
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
