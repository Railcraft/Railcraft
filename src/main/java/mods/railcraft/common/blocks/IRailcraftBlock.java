/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.client.render.models.resource.ModelManager;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.core.IRailcraftObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 7/16/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IRailcraftBlock extends IRailcraftObject<Block> {

    default IBlockState getState(@Nullable IVariantEnum variant) {
        return ((Block) this).getDefaultState();
    }

    default IBlockState getItemRenderState(@Nullable IVariantEnum variant) {
        return getState(variant);
    }

    @SideOnly(Side.CLIENT)
    @Nullable
    default StateMapperBase getStateMapper() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    default void initializeClient() {
        StateMapperBase stateMapper = getStateMapper();
        if (stateMapper != null)
            ModelLoader.setCustomStateMapper(getObject(), stateMapper);
    }

    @SideOnly(Side.CLIENT)
    default void registerItemModel(ItemStack stack, @Nullable IVariantEnum variant) {
        ModelManager.registerBlockItemModel(stack, getItemRenderState(variant));
    }

    @SideOnly(Side.CLIENT)
    default ResourceLocation getBlockTexture() {
        return this.getRegistryName();
    }

    @SideOnly(Side.CLIENT)
    default void registerTextures(TextureMap textureMap) {
        TextureAtlasSheet.unstitchIcons(textureMap, getBlockTexture(), getTextureDimensions());
        IVariantEnum[] variants = getVariants();
        if (variants != null) {
            for (IVariantEnum variant : variants) {
                if (variant instanceof IVariantEnumBlock)
                    TextureAtlasSheet.unstitchIcons(textureMap,
                            new ResourceLocation(getRegistryName() + "_" + variant.getResourcePathSuffix()),
                            ((IVariantEnumBlock<?>) variant).getTextureDimensions());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    default Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 1);
    }
}
