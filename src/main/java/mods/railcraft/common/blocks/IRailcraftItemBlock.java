/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.common.items.IRailcraftItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Optional;

/**
 * Created by CovertJaguar on 7/20/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItemBlock extends IRailcraftItem {

    @SideOnly(Side.CLIENT)
    default Optional<ModelResourceLocation> getStateMapperModelOverride(IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof IRailcraftBlock) {
            StateMapperBase stateMapper = ((IRailcraftBlock) block).getStateMapper();

            if (stateMapper != null) {
                Map<IBlockState, ModelResourceLocation> stateMap = stateMapper.putStateModelLocations(block);
                return Optional.ofNullable(stateMap.get(state));
            }
        }
        return Optional.empty();
    }

    @SideOnly(Side.CLIENT)
    default ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        return getStateMapperModelOverride(state)
                .orElseGet(() -> new ModelResourceLocation(state.getBlock().getRegistryName(), "inventory"));
    }
}
