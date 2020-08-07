/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.util.collections.ArrayTools;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by CovertJaguar on 7/8/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface ISubtypedBlock<V extends Enum<V> & IVariantEnum> extends IRailcraftBlock {
    class VariantData<V extends Enum<V> & IVariantEnum> {
        private BlockMeta.Variant annotation;
        private Class<V> variantClass;
        private V[] variantValues;
        private PropertyEnum<V> property;
    }

    default VariantData<V> getVariantData() {
        VariantData<V> data = new VariantData<>();
        data.annotation = getClass().getAnnotation(BlockMeta.Variant.class);
        //noinspection unchecked
        data.variantClass = (Class<V>) data.annotation.value();
        data.variantValues = ((Class<? extends V>) data.variantClass).getEnumConstants();
        data.property = PropertyEnum.create(data.annotation.propertyName(), data.variantClass);
        return data;
    }

    @Nonnull
    default IProperty<V> getVariantEnumProperty() {
        return getVariantData().property;
    }

    @Nonnull
    @Override
    default Class<? extends V> getVariantEnumClass() {
        return getVariantData().variantClass;
    }

    @Nonnull
    @Override
    default V[] getVariants() {
        return getVariantData().variantValues;
    }

    default V getVariant(IBlockState state) {
        return state.getValue(getVariantEnumProperty());
    }

    @SuppressWarnings("unchecked")
    @Override
    default IBlockState getState(@Nullable IVariantEnum variant) {
        if (variant == null)
            return ((Block) this).getDefaultState();
        checkVariant(variant);
        return ((Block) this).getDefaultState().withProperty(getVariantEnumProperty(), (V) variant);
    }

    @Nonnull
    default IBlockState convertMetaToState(int meta) {
        IBlockState state = ((Block) this).getDefaultState();
        V[] variantValues = getVariantData().variantValues;
        if (ArrayTools.indexInBounds(variantValues.length, meta))
            state = state.withProperty(getVariantEnumProperty(), variantValues[meta]);
        return state;
    }
}
