/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.simple;

import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.misc.MicroBlockPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Created by CovertJaguar on 8/27/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockRailcraftStone extends BlockRailcraft {
    public BlockRailcraftStone(MapColor mapColor) {
        super(Material.ROCK, mapColor);
        setHardness(2f).setResistance(10f);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativePlugin.STRUCTURE_TAB);
    }

    @Override
    public void initializeDefinition() {
        HarvestPlugin.setStateHarvestLevel("pickaxe", 1, getDefaultState());

        EntityTunnelBore.addMineableBlock(this);

        ForestryPlugin.addBackpackItem("forestry.digger", getStack());

        MicroBlockPlugin.addMicroBlockCandidate(this);
    }
}
