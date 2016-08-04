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
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 5/3/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("railcraft:redstone_flux")
public class ModuleRF extends RailcraftModulePayload {
    public ModuleRF() {
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
                RailcraftCarts cart = RailcraftCarts.REDSTONE_FLUX;
                if (cart.setup()) {
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "LRL",
                            "RMR",
                            "LRL",
                            'R', new ItemStack(Blocks.REDSTONE_BLOCK),
                            'L', RailcraftItems.ingot, Metal.LEAD,
                            'M', Items.MINECART
                    );
                }

                EnumMachineGamma gamma = EnumMachineGamma.RF_LOADER;
                if (gamma.isAvailable()) {
                    ItemStack detector = EnumDetector.ADVANCED.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addRecipe(gamma.getItem(),
                            "RLR",
                            "LRL",
                            "RDR",
                            'D', detector,
                            'R', "blockRedstone",
                            'L', "blockLead");
                }

                gamma = EnumMachineGamma.RF_UNLOADER;
                if (gamma.isAvailable()) {
                    ItemStack detector = EnumDetector.ADVANCED.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.STONE_PRESSURE_PLATE);
                    CraftingPlugin.addRecipe(gamma.getItem(),
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
