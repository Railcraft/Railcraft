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
import mods.railcraft.api.crafting.ICrusherCraftingManager;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.firestone.*;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
@RailcraftModule("magic")
public class ModuleMagic extends RailcraftModulePayload {
    public ModuleMagic() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void preInit() {
                BlockOre.registerBlock();
                BlockFirestoneRecharge.registerBlock();

                EntityItemFirestone.register();

                ItemFirestoneRaw.registerItem();
                ItemFirestoneCut.registerItem();
                ItemFirestoneRefined.registerItem();
                ItemFirestoneCracked.registerItem();

                FMLCommonHandler.instance().bus().register(new FirestoneTickHandler());

                if (EnumOre.FIRESTONE.isEnabled() && ItemFirestoneRaw.item != null && ItemFirestoneCut.item != null) {
                    ICrusherCraftingManager.ICrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createAndAddRecipe(EnumOre.FIRESTONE.getItem(), true, false);
                    recipe.addOutput(ItemFirestoneRaw.getItem(), 1F);

                    CraftingPlugin.addRecipe(ItemFirestoneCut.getItem(),
                            " P ",
                            "PFP",
                            " P ",
                            'P', Items.DIAMOND_PICKAXE,
                            'F', ItemFirestoneRaw.item);

                    for (ItemStack stack : FluidHelper.getContainersFilledWith(Fluids.LAVA.get(FluidHelper.BUCKET_VOLUME))) {
                        CraftingPlugin.addRecipe(ItemFirestoneRefined.getItemEmpty(),
                                "LRL",
                                "RFR",
                                "LRL",
                                'R', "blockRedstone",
                                'L', stack,
                                'F', ItemFirestoneCut.item);
                        CraftingPlugin.addRecipe(ItemFirestoneRefined.getItemEmpty(),
                                "LOL",
                                "RFR",
                                "LRL",
                                'R', "blockRedstone",
                                'L', stack,
                                'O', ItemFirestoneRaw.item,
                                'F', new ItemStack(ItemFirestoneCracked.item, 1, OreDictionary.WILDCARD_VALUE));
                    }
                }
            }

        });
    }

}
