/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import cpw.mods.fml.common.FMLCommonHandler;
import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.firestone.FirestoneTickHandler;
import mods.railcraft.common.items.firestone.*;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ModuleMagic extends RailcraftModule {

    @Override
    public void initFirst() {
        BlockOre.registerBlock();
        BlockFirestoneRecharge.registerBlock();

        EntityItemFirestone.register();

        ItemFirestoneRaw.registerItem();
        ItemFirestoneCut.registerItem();
        ItemFirestoneRefined.registerItem();
        ItemFirestoneCracked.registerItem();

        FMLCommonHandler.instance().bus().register(new FirestoneTickHandler());

        if (EnumOre.FIRESTONE.isEnabled() && ItemFirestoneRaw.item != null && ItemFirestoneCut.item != null) {
            IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(EnumOre.FIRESTONE.getItem(), true, false);
            recipe.addOutput(ItemFirestoneRaw.getItem(), 1F);

            CraftingPlugin.addShapedRecipe(ItemFirestoneCut.getItem(),
                    " P ",
                    "PFP",
                    " P ",
                    'P', Items.diamond_pickaxe,
                    'F', ItemFirestoneRaw.item);

            for (ItemStack stack : FluidHelper.getContainersFilledWith(Fluids.LAVA.get(FluidHelper.BUCKET_VOLUME))) {
                CraftingPlugin.addShapedRecipe(ItemFirestoneRefined.getItemEmpty(),
                        "LRL",
                        "RFR",
                        "LRL",
                        'R', "blockRedstone",
                        'L', stack,
                        'F', ItemFirestoneCut.item);
                CraftingPlugin.addShapedRecipe(ItemFirestoneRefined.getItemEmpty(),
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

}
