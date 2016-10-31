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
import mods.railcraft.common.util.collections.ArrayTools;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class RailcraftBlockSubtyped<T extends Enum<T> & IVariantEnum> extends BlockRailcraft {
    private final Class<? extends T> variantClass;
    private final T[] variantValues;

    protected RailcraftBlockSubtyped(Material materialIn, Class<? extends T> variantClass) {
        this(materialIn, materialIn.getMaterialMapColor(), variantClass);
    }

    protected RailcraftBlockSubtyped(Material material, MapColor mapColor, Class<? extends T> variantClass) {
        super(material, mapColor);
        this.variantClass = variantClass;
        this.variantValues = variantClass.getEnumConstants();
    }

    public abstract IProperty<T> getVariantProperty();

    public T getVariant(IBlockState state) {
        return state.getValue(getVariantProperty());
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return variantClass;
    }

    @Nullable
    @Override
    public IVariantEnum[] getVariants() {
        return variantValues;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        if (variant != null) {
            checkVariant(variant);
            return getDefaultState().withProperty(getVariantProperty(), (T) variant);
        }
        return getDefaultState();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        IVariantEnum[] variants = getVariants();
        if (variants != null) {
            for (IVariantEnum variant : variants) {
                list.add(getStack(variant));
            }
        } else {
            list.add(getStack(null));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getStack(getVariant(state));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getVariant(state).ordinal();
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
