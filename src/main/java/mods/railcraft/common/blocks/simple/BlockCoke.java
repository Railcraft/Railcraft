/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.simple;

import mods.railcraft.api.crafting.Crafters;
import mods.railcraft.api.crafting.ICokeOvenCrafter;
import mods.railcraft.common.blocks.BlockFlammable;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.items.ItemCoke;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by CovertJaguar on 8/26/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockCoke extends BlockFlammable {

    public BlockCoke() {
        super(Material.ROCK, MapColor.BLACK, 5, 300);
        setHardness(5.0F).setResistance(10.0F);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void initializeDefinition() {
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, getDefaultState());
        ForestryPlugin.addBackpackItem("forestry.miner", getStack());
        OreDictionary.registerOre("blockFuelCoke", getStack());
    }

    @Override
    public void defineRecipes() {
        Crafters.cokeOven().newRecipe(Ingredients.from(Blocks.COAL_BLOCK))
                .name("railcraft:coke_block")
                .output(getStack())
                .fluid(Fluids.CREOSOTE.get(ItemCoke.COKE_COOK_CREOSOTE * 9))
                .time(ICokeOvenCrafter.DEFAULT_COOK_TIME * 9)
                .register();

        CraftingPlugin.addShapedRecipe(getStack(),
                "CCC",
                "CCC",
                "CCC",
                'C', RailcraftItems.COKE);
        CraftingPlugin.addShapelessRecipe(RailcraftItems.COKE.getStack(9), getStack());
    }
}
