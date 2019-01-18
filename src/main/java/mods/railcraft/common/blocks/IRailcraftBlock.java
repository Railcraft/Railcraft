/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
import mods.railcraft.common.plugins.color.ColorPlugin;
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

import javax.annotation.OverridingMethodsMustInvokeSuper;

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
    default @Nullable StateMapperBase getStateMapper() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @OverridingMethodsMustInvokeSuper
    default void initializeClient() {
        StateMapperBase stateMapper = getStateMapper();
        if (stateMapper != null)
            ModelLoader.setCustomStateMapper(getObject(), stateMapper);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    default void finalizeDefinition() {
        if (getObject() instanceof ColorPlugin.IColorHandlerBlock) {
            ColorPlugin.instance.register(getObject(), (ColorPlugin.IColorHandlerBlock) getObject());
        }
    }

    @SideOnly(Side.CLIENT)
    default void registerItemModel(ItemStack stack, @Nullable IVariantEnum variant) {
        ModelManager.registerBlockItemModel(stack, getItemRenderState(variant));
    }

    @SideOnly(Side.CLIENT)
    default @Nullable ResourceLocation getBlockTexture() {
        return getRegistryName();
    }

    @SideOnly(Side.CLIENT)
    default void registerTextures(TextureMap textureMap) {
        ResourceLocation texture = getBlockTexture();
        if (texture != null)
            TextureAtlasSheet.unstitchIcons(textureMap, texture, getTextureDimensions());
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

    default Tuple<Integer, Integer> getTextureDimensions() {
        return new Tuple<>(1, 1);
    }
}
