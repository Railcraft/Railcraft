/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.client.render.tools.ModelManager;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/16/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftBlock extends IRailcraftObject {

    default IBlockState getState(@Nullable IVariantEnum variant) {
        return ((Block) this).getDefaultState();
    }

    default IBlockState getItemRenderState(@Nullable IVariantEnum variant) {
        return getState(variant);
    }

    @SideOnly(Side.CLIENT)
    default void registerItemModel(ItemStack stack, @Nullable IVariantEnum variant) {
        ModelManager.registerBlockItemModel(stack, getItemRenderState(variant));
    }

    default ResourceLocation getBlockTexture() {
        return ((Block) this).getRegistryName();
    }

    default Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 1);
    }
}
