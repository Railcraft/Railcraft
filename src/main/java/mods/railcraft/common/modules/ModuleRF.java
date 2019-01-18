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
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.manipulator.ManipulatorVariant;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 5/3/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule(value = "railcraft:redstone_flux", description = "redstone flux cart")
public class ModuleRF extends RailcraftModulePayload {
    public ModuleRF() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftCarts.REDSTONE_FLUX,
                        RailcraftBlocks.MANIPULATOR,
                        RailcraftBlocks.FLUX_TRANSFORMER
                );
            }

            @Override
            public void init() {
                ManipulatorVariant gamma = ManipulatorVariant.RF_LOADER;
                if (gamma.isAvailable()) {
                    ItemStack detector = EnumDetector.ADVANCED.getStack();
                    if (InvTools.isEmpty(detector))
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addShapedRecipe(gamma.getStack(),
                            "RLR",
                            "LRL",
                            "RDR",
                            'D', detector,
                            'R', "blockRedstone",
                            'L', "blockLead");
                }

                gamma = ManipulatorVariant.RF_UNLOADER;
                if (gamma.isAvailable()) {
                    ItemStack detector = EnumDetector.ADVANCED.getStack();
                    if (InvTools.isEmpty(detector))
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addShapedRecipe(gamma.getStack(),
                            "RDR",
                            "LRL",
                            "RLR",
                            'D', detector,
                            'R', "blockRedstone",
                            'L', "blockLead");
                }
            }
        });
    }
}
