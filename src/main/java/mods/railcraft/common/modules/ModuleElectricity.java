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
import mods.railcraft.common.blocks.frame.BlockFrame;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.items.ItemElectricMeter;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.items.RailcraftPartItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.RotorRepairRecipe;
import net.minecraft.init.Items;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ModuleElectricity extends RailcraftModule {

    @Override
    public void initFirst() {
        ItemElectricMeter.register();
        BlockFrame.registerBlock();

        EnumMachineAlpha alpha = EnumMachineAlpha.TURBINE;
        if (alpha.register()) {
            CraftingPlugin.addShapedRecipe(alpha.getItem(3),
                    "BPB",
                    "P P",
                    "BPB",
                    'P', RailcraftItem.plate.getRecipeObject(EnumPlate.STEEL),
                    'B', "blockSteel");

            RailcraftPartItems.getTurbineRotor();

            CraftingPlugin.addRecipe(new RotorRepairRecipe());

//                ItemStack rotor = RailcraftPartItems.getTurbineRotor();
//                rotor.setItemDamage(25000);
//                CraftingPlugin.addShapelessRecipe(rotor, RailcraftPartItems.getTurbineRotor());
        }

        EnumMachineEpsilon epsilon = EnumMachineEpsilon.ELECTRIC_FEEDER;
        if (epsilon.register())
            CraftingPlugin.addShapedRecipe(epsilon.getItem(),
                    "PCP",
                    "CCC",
                    "PCP",
                    'P', RailcraftItem.plate.getRecipeObject(EnumPlate.TIN),
                    'C', "ingotCopper");

        epsilon = EnumMachineEpsilon.ELECTRIC_FEEDER_ADMIN;
        epsilon.register();

        epsilon = EnumMachineEpsilon.FORCE_TRACK_EMITTER;
        if (epsilon.register())
            CraftingPlugin.addShapedRecipe(epsilon.getItem(),
                    "PCP",
                    "CDC",
                    "PCP",
                    'P', RailcraftItem.plate.getRecipeObject(EnumPlate.TIN),
                    'D', "blockDiamond",
                    'C', "ingotCopper");

        epsilon = EnumMachineEpsilon.FLUX_TRANSFORMER;
        if (epsilon.register())
            CraftingPlugin.addShapedRecipe(epsilon.getItem(2),
                    "PGP",
                    "GRG",
                    "PGP",
                    'P', RailcraftItem.plate.getRecipeObject(EnumPlate.COPPER),
                    'G', "ingotGold",
                    'R', "blockRedstone");

        EnumMachineDelta delta = EnumMachineDelta.WIRE;
        if (delta.register())
            RailcraftCraftingManager.rollingMachine.getRecipeList().add(new ShapedOreRecipe(delta.getItem(8),
                    "LPL",
                    "PCP",
                    "LPL",
                    'C', "blockCopper",
                    'P', Items.paper,
                    'L', "ingotLead"));
    }

}
