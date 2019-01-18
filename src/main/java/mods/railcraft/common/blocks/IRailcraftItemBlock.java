/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.items.IRailcraftItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * Created by CovertJaguar on 7/20/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItemBlock extends IRailcraftItem {

    @SideOnly(Side.CLIENT)
    default ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        StateMapperBase stateMapper = null;

        if (state.getBlock() instanceof IRailcraftBlock)
            stateMapper = ((IRailcraftBlock) state.getBlock()).getStateMapper();

        if (stateMapper == null)
            return new ModelResourceLocation(state.getBlock().getRegistryName(), new DefaultStateMapper().getPropertyString(state.getProperties()));

        Map<IBlockState, ModelResourceLocation> stateMap = stateMapper.putStateModelLocations(state.getBlock());
        return stateMap.get(state);
    }
}
