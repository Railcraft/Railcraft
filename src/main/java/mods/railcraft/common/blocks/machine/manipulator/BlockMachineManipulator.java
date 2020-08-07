/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.TileManager;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.machine.BlockMachine;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Created by CovertJaguar on 9/8/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
@BlockMeta.Variant(ManipulatorVariant.class)
public class BlockMachineManipulator extends BlockMachine<ManipulatorVariant> {
    public static final PropertyEnum<EnumFacing> FRONT = PropertyEnum.create("front", EnumFacing.class);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockMachineManipulator() {
        super(Material.ROCK);
        setDefaultState(getDefaultState().withProperty(FRONT, EnumFacing.DOWN).withProperty(ACTIVE, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), FRONT, ACTIVE);
    }

    @Override
    public IBlockState getItemRenderState(@Nullable IVariantEnum variant) {
        EnumFacing facing = EnumFacing.NORTH;
        if (variant != null)
            switch ((ManipulatorVariant) variant) {
                case ITEM_LOADER:
                case FLUID_LOADER:
                    facing = EnumFacing.DOWN;
                    break;
                case ITEM_UNLOADER:
                case FLUID_UNLOADER:
                    facing = EnumFacing.UP;
                    break;
                case RF_LOADER:
                    facing = EnumFacing.EAST;
                    break;
            }
        return getState(variant).withProperty(ACTIVE, true).withProperty(FRONT, facing);
    }


    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (getVariant(state)) {
            case ITEM_LOADER:
            case ITEM_UNLOADER:
            case FLUID_LOADER:
            case FLUID_UNLOADER:
            case DISPENSER_CART:
                return SoundType.STONE;
            case ITEM_LOADER_ADVANCED:
            case ITEM_UNLOADER_ADVANCED:
            case RF_LOADER:
            case RF_UNLOADER:
            case DISPENSER_TRAIN:
                return SoundType.METAL;
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        TileManager tm = TileManager.forTile(this::getTileClass, state, worldIn, pos);
        state = state.withProperty(FRONT,
                tm.retrieve(ITileRotate.class, ITileRotate::getFacing).orElse(EnumFacing.DOWN));
        state = state.withProperty(ACTIVE,
                tm.retrieve(TileManipulatorCart.class, TileManipulatorCart::isProcessing).orElse(false));
        return state;
    }
}
