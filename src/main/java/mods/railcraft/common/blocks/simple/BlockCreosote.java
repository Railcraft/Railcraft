/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.simple;

import mods.railcraft.common.blocks.BlockFlammable;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

/**
 * Created by CovertJaguar on 8/26/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockCreosote extends BlockFlammable {
    public BlockCreosote() {
        super(Material.WOOD, MapColor.BROWN, 5, 300);
        setHardness(5.0F).setResistance(10.0F);
        setSoundType(SoundType.WOOD);
    }

    @Override
    public void initializeDefinition() {
        HarvestPlugin.setStateHarvestLevel("axe", 0, getDefaultState());
        ForestryPlugin.addBackpackItem("forestry.builder", getStack());
        MicroBlockPlugin.addMicroBlockCandidate(this);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe("railcraft:block_creosote", getStack(),
                "logWood", Fluids.CREOSOTE.getBucket());
        ForestryPlugin.instance().addCarpenterRecipe("railcraft:block_creosote", 40,
                Fluids.CREOSOTE.get(750), ItemStack.EMPTY, getStack(), "L", 'L', "logWood");
    }
}
