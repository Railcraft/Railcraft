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
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class BlockContainerRailcraftSubtyped<T extends TileRailcraft, V extends Enum<V> & IVariantEnum> extends BlockContainerRailcraft<T> implements ISubtypedBlock<V> {
    private VariantData<V> variantData;

    @SuppressWarnings("SameParameterValue")
    protected BlockContainerRailcraftSubtyped(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    protected BlockContainerRailcraftSubtyped(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    @Override
    public VariantData<V> getVariantData() {
        if (variantData == null)
            variantData = ISubtypedBlock.super.getVariantData();
        return variantData;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return convertMetaToState(meta);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(getVariantEnumProperty()).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (V variant : getVariants()) {
            CreativePlugin.addToList(list, getStack(variant));
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getStack(getVariant(state));
    }
}
