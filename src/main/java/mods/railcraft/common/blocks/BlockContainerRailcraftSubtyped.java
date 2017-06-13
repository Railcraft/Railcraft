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
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.collections.ArrayTools;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockContainerRailcraftSubtyped<V extends Enum<V> & IVariantEnumBlock<V>> extends BlockContainerRailcraft {
    private RailcraftBlockMetadata annotation;
    private Class<V> variantClass;
    private V[] variantValues;
    private PropertyEnum<V> variantProperty;

    protected BlockContainerRailcraftSubtyped(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    protected BlockContainerRailcraftSubtyped(Material material, MapColor mapColor) {
        super(material, mapColor);
        setup();
    }

    private void setup() {
        if (annotation == null) {
            annotation = getClass().getAnnotation(RailcraftBlockMetadata.class);
            //noinspection unchecked
            variantClass = (Class<V>) annotation.variant();
            variantValues = variantClass.getEnumConstants();
            variantProperty = PropertyEnum.create("variant", variantClass);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }

    @SuppressWarnings("unchecked")
    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        if (variant == null)
            return getDefaultState();
        checkVariant(variant);
        return getDefaultState().withProperty(getVariantProperty(), (V) variant);
    }

    @Nonnull
    public final IProperty<V> getVariantProperty() {
        setup();
        return variantProperty;
    }

    public final V getVariant(IBlockState state) {
        return state.getValue(getVariantProperty());
    }

    @Nonnull
    @Override
    public final Class<? extends V> getVariantEnum() {
        return variantClass;
    }

    @Nonnull
    @Override
    public final V[] getVariants() {
        return variantValues;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState();
        if (ArrayTools.indexInBounds(variantValues.length, meta))
            state = state.withProperty(getVariantProperty(), variantValues[meta]);
        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantProperty()).ordinal();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        V[] variants = getVariants();
        if (variants != null) {
            for (V variant : variants) {
                CreativePlugin.addToList(list, getStack(variant));
            }
        } else {
            CreativePlugin.addToList(list, getStack(null));
        }
    }

    @Nullable
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getStack(getVariant(state));
    }

}
