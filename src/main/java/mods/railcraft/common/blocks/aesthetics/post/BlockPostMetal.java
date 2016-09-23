/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlockProperties;
import mods.railcraft.common.plugins.color.EnumColor;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockPostMetal extends BlockPostBase {

    public static final PropertyEnum<EnumColor> COLOR = RailcraftBlockProperties.COLOR;
    public static BlockPostMetal post;
    public static BlockPostMetal platform;
    public final boolean isPlatform;

    public BlockPostMetal(boolean isPlatform) {
        setSoundType(SoundType.METAL);
        this.isPlatform = isPlatform;
        setDefaultState(blockState.getBaseState().withProperty(COLOR, EnumColor.WHITE));
    }

    @Override
    public void initializeDefinintion() {
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 2, this);

        ForestryPlugin.addBackpackItem("builder", this);

        for (EnumColor color : EnumColor.VALUES) {
            ItemStack stack = getStack(1, color);
            if (stack != null)
                RailcraftRegistry.register(this, color, stack);
        }
    }

    @Nullable
    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return EnumColor.class;
    }

    @Override
    public IBlockState getState(@Nullable IVariantEnum variant) {
        IBlockState state = getDefaultState();
        if (variant != null) {
            checkVariant(variant);
            state = state.withProperty(COLOR, (EnumColor) variant);
        }
        return state;
    }

    public EnumColor getColor(IBlockState state) {
        return state.getValue(COLOR);
    }

    @Override
    public boolean isPlatform(IBlockState state) {
        return isPlatform;
    }

    @Override
    public void getSubBlocks(@Nonnull Item item, CreativeTabs tab, List<ItemStack> list) {
        for (EnumColor color : EnumColor.VALUES) {
            list.add(getStack(1, color.ordinal()));
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, @Nonnull IBlockState state, EntityPlayer player) {
        return true;
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        if (isPlatform)
            list.add(EnumPost.METAL_PLATFORM_UNPAINTED.getStack());
        else
            list.add(EnumPost.METAL_UNPAINTED.getStack());
        return list;
    }

    @Override
    public boolean recolorBlock(World world, @Nonnull BlockPos pos, EnumFacing side, @Nonnull EnumDyeColor color) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (getColor(state).getDye() != color) {
            world.setBlockState(pos, getDefaultState().withProperty(COLOR, EnumColor.fromDye(color)));
            return true;
        }
        return false;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @Override
    @Nonnull
    public MapColor getMapColor(IBlockState state) {
        return getColor(state).getMapColor();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COLOR, EnumColor.fromOrdinal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return getColor(state).ordinal();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR);
    }
}
