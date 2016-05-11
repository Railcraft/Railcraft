package mods.railcraft.common.modules;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.items.ItemIngot;
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
public class ModuleRF extends RailcraftModulePayload {
    public ModuleRF() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.detector,
                        RailcraftBlocks.machine_gamma
                );
            }

            @Override
            public void preInit() {
                EnumCart cart = EnumCart.REDSTONE_FLUX;
                if (cart.setup()) {
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "LRL",
                            "RMR",
                            "LRL",
                            'R', new ItemStack(Blocks.redstone_block),
                            'L', RailcraftItems.ingot, ItemIngot.EnumIngot.LEAD,
                            'M', Items.minecart
                    );
                }

                EnumMachineGamma gamma = EnumMachineGamma.RF_LOADER;
                if (gamma.isAvailable()) {
                    ItemStack detector = EnumDetector.ADVANCED.getItem();
                    if (detector == null)
                        detector = new ItemStack(Blocks.stone_pressure_plate);
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
                        detector = new ItemStack(Blocks.stone_pressure_plate);
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
