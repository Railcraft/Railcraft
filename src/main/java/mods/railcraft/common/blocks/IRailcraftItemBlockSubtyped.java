/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by CovertJaguar on 8/27/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftItemBlockSubtyped extends IRailcraftItemBlock {
    @Override
    @SideOnly(Side.CLIENT)
    default ModelResourceLocation getModelLocation(ItemStack stack, IBlockState state) {
        return getStateMapperModelOverride(state)
                .orElseGet(() -> new ModelResourceLocation(state.getBlock().getRegistryName(), new DefaultStateMapper().getPropertyString(state.getProperties())));
    }
}
