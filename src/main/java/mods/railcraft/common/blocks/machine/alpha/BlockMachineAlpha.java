/*
 * ******************************************************************************
 *  Copyright 2011-2016 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.RailcraftBlockProperties;
import mods.railcraft.common.blocks.TileManager;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockMachineAlpha extends BlockMachine<EnumMachineAlpha> {
    public static final PropertyEnum<EnumMachineAlpha> VARIANT = RailcraftBlockProperties.MACHINE_ALPHA_VARIANT;
    public static final PropertyEnum<EnumFacing> FRONT = RailcraftBlockProperties.FACING;
    public static final PropertyBool ACTIVE = RailcraftBlockProperties.ACTIVE;

    public BlockMachineAlpha() {
        super(EnumMachineAlpha.PROXY, false);
        setDefaultState(getDefaultState().withProperty(FRONT, EnumFacing.UP).withProperty(ACTIVE, false));
    }

    @Override
    public Class<? extends IVariantEnum> getVariantEnum() {
        return EnumMachineAlpha.class;
    }

    @Override
    public IVariantEnum[] getVariants() {
        return EnumMachineAlpha.VALUES;
    }

    @Override
    public IProperty<EnumMachineAlpha> getVariantProperty() {
        return VARIANT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty(), FRONT, ACTIVE);
    }

    @Override
    public IBlockState getItemRenderState(@Nullable IVariantEnum variant) {
        EnumFacing facing = EnumFacing.UP;
        if (variant != null)
            switch ((EnumMachineAlpha) variant) {
                case TRADE_STATION:
                case STEAM_OVEN:
                case STEAM_TRAP_AUTO:
                case STEAM_TRAP_MANUAL:
                    return getState(variant);
            }
        return getState(variant).withProperty(FRONT, facing);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return super.getActualState(state, worldIn, pos)
                .withProperty(FRONT, TileManager.forTile(this::getTileClass, state, worldIn, pos)
                .retrieve(ITileRotate.class, ITileRotate::getFacing).orElse(EnumFacing.UP));
    }
}
